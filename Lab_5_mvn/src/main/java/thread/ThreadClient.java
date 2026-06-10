package thread;

import db.OrganizationDao;
import exceptions.*;
import io.ByteUtil;
import io.InputManager;
import main.Invoker;
import net.Request;
import net.RequestType;
import net.Runner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import security.User;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;

public class ThreadClient extends Runner {
    private DatagramChannel CHANNEL;
    private final Deque<Request> cachedMessages = new ArrayDeque<>();
    private final BlockingQueue<Request> incomingQueue = new LinkedBlockingQueue<>();
    private volatile boolean waitingForResponse = false;
    private ScheduledExecutorService pingScheduler;
    private static final Logger logger = LogManager.getLogger(ThreadClient.class);

    public ThreadClient(Invoker invoker, int port, boolean isLab7) {
        super(port, invoker, isLab7);
        super.invoker.setRunner(this);
    }

    public static void main(String[] args) {
        Invoker invoker = new Invoker();
        ThreadClient client = new ThreadClient(invoker, 9898, true);
        OrganizationDao.setRunner(client);

        client.applyParams(false);
        client.connect();

        User user1 = null;
        while (user1 == null) {
            user1 = client.authorize();
        }
        client.setUser(user1);
        client.run();
    }

    @Override
    public Request sendAndWait(Request request) {
        long start = System.currentTimeMillis();
        long timeout = 1000;

        waitingForResponse = true;
        sendMessage(request);

        if (request.requestType() != RequestType.PING) {
            logger.info("Сообщение отправлено серверу");
        } else {
            logger.debug("Отправлен пинг");
        }

        try {
            while (System.currentTimeMillis() - start < timeout) {
                try {
                    Request response = incomingQueue.poll(15, TimeUnit.MILLISECONDS);
                    if (response == null) continue;

                    if (!runnerId.equals(response.runnerId())) {
                        incomingQueue.put(response);
                        continue;
                    }

                    if (response.requestType() == RequestType.PING
                            && request.requestType() != RequestType.PING) {
                        incomingQueue.put(response);
                        continue;
                    }

                    if (!silentConnection) {
                        serverOnline();
                        silentConnection = true;
                    }
                    silentConnectionError = false;
                    return response;

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }

            silentConnection = false;
            if (request.requestType() != RequestType.PING) {
                runnerNotConnected();
            }
            if (!silentConnectionError) {
                runnerNotConnected();
                silentConnectionError = true;
            }
            return null;

        } finally {
            waitingForResponse = false;
        }
    }

    public User authorize() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        logger.info("У вас уже есть аккаунт? (введите \"y\" or \"n\")");
        String input;
        try {
            input = br.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        input = InputManager.separateSecurity(input);

        boolean isRegistration;
        User user;
        if (input == null || input.isEmpty() || input.equals("y") || input.equals("Y")) {
            logger.info("===== Авторизация =====");
            isRegistration = false;
            user = InputManager.inputUser(br, false);
        } else {
            logger.info("===== Регистрация =====");
            isRegistration = true;
            user = InputManager.inputUser(br, true);
        }

        Request request = Request.build()
                .setRequestType(RequestType.USER)
                .setUser(user)
                .setRegistration(isRegistration)
                .setRunnerId(runnerId);

        Request response = null;
        while (response == null) {
            response = sendAndWait(request);
        }

        logger.info(response.feedback());
        return response.user();
    }

    @Override
    public void connect() {
        try {
            CHANNEL = DatagramChannel.open();
            CHANNEL.configureBlocking(false);
            CHANNEL.connect(new InetSocketAddress(IP_ADDRESS, port));
            logger.info("Клиент запущен и готов отправлять данные");

            for (int i = 0; i < 2; i++) {
                readPool.submit(this::receiveLoop);
            }

            pingScheduler = Executors.newSingleThreadScheduledExecutor(
                    new NamedThreadFactory("PING"));
            pingScheduler.scheduleAtFixedRate(
                    () -> ping(Request.build()
                            .setRequestId(UUID.randomUUID())
                            .setRunnerId(runnerId)),
                    0, 500, TimeUnit.MILLISECONDS);

        } catch (IOException e) {
            String t = "Клиент не смог подключиться к сети по порту " + port;
            logger.error(t, e);
            throw new RuntimeException(t);
        }
    }

    @Override
    public void sendMessage(Request request) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(ByteUtil.toByteArray(request, ARRAY_SIZE));
            synchronized (CHANNEL) {
                CHANNEL.send(buffer, new InetSocketAddress(IP_ADDRESS, port));
            }
            isUnreachable = false;
        } catch (PortUnreachableException e) {
            isUnreachable = true;
        } catch (IOException e) {
            logger.warn(e);
        }
    }

    @Override
    public Request receiveMessage() {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(ARRAY_SIZE);
            SocketAddress address;
            synchronized (CHANNEL) {
                address = CHANNEL.receive(buffer);
            }
            if (address == null) return null;

            buffer.flip();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            Request request = ByteUtil.fromBytesTo(data, Request.class);
            if (request.requestType() != RequestType.PING) {
                logger.info("Сообщение получено от сервера #{}", address);
            }
            return request;
        } catch (SocketTimeoutException | PortUnreachableException e) {
            return null;
        } catch (IOException | ClassNotFoundException e) {
            logger.warn(Arrays.toString(e.getStackTrace()).replace(",", "\n"), e);
            return null;
        }
    }

    @Override
    public void run() {
        run(false, "", isLab7);
    }

    @Override
    public void run(boolean isScript, String path, boolean isLab7) {
        if (isScript) {
            runScript(path);
            return;
        }

        isRunning = true;
        br = new BufferedReader(new InputStreamReader(System.in));

        for (int i = 0; i < 2; i++) {
            processPool.submit(this::processLoop);
        }

        consoleLoop();
        shutdownPools();
    }

    private void runScript(String path) {
        try (BufferedReader scriptReader = new BufferedReader(
                new InputStreamReader(new FileInputStream(Path.of(path).toFile())))) {

            String input;
            while ((input = scriptReader.readLine()) != null) {
                if (input.trim().isEmpty()) continue;
                logger.info(input);

                try {
                    Request request = invoker.defineCommand(input, true).execute(user);
                    if (request == null) continue;

                    Request response = null;
                    while (response == null) {
                        response = sendAndWait(request.setRunnerId(runnerId));
                    }

                    if (response.requestType() != RequestType.PING) {
                        logger.info(response.feedback());
                    }

                } catch (NoSuchEntityException | RecursionLimitReached | XmlUtilException e) {
                    logger.warn(e.getMessage());
                }
            }

            logger.info("Скрипт {} выполнен", path);

        } catch (FileNotFoundException e) {
            logger.warn("Файл скрипта не найден: {}", path);
        } catch (IOException e) {
            logger.warn("Ошибка чтения скрипта: {}", e.getMessage());
        }
    }

    private void receiveLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Request request = receiveMessage();
                if (request != null) {
                    incomingQueue.put(request);
                } else {
                    Thread.sleep(5);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void processLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                if (waitingForResponse) {
                    Thread.sleep(5);
                    continue;
                }

                Request head;
                synchronized (incomingQueue) {
                    head = incomingQueue.peek();
                    if (head == null || head.requestType() == RequestType.PING) {
                        Thread.sleep(10);
                        continue;
                    }
                    incomingQueue.poll();
                }

                synchronized (cachedMessages) {
                    cachedMessages.addFirst(head);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void consoleLoop() {
        if (initialRunShowUser) {
            showUser();
            initialRunShowUser = false;
        }

        while (isRunning) {
            try {
                if (!br.ready()) {
                    synchronized (cachedMessages) {
                        while (!cachedMessages.isEmpty()) {
                            Request cached = cachedMessages.removeLast();
                            if (cached.requestType() != RequestType.PING) {
                                logger.info(cached.feedback());
                                if (isRunning) showUser();
                            }
                        }
                    }
                    Thread.sleep(5);
                    continue;
                }

                String input = br.readLine();

                if (input.trim().isEmpty()) continue;
                logger.info(input);

                Thread.sleep(10);

                Request request = invoker.defineCommand(input, false).execute(user);
                if (isRunning && CHANNEL != null && request != null) {
                    Request response = sendAndWait(request.setRunnerId(runnerId));
                    if (response != null) {
                        synchronized (cachedMessages) {
                            cachedMessages.addFirst(response);
                        }
                    }
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (NoSuchEntityException | RecursionLimitReached | XmlUtilException | IOException e) {
                logger.warn(e.getMessage());
                if (isRunning) showUser();
            }
        }
    }

    private void shutdownPools() {
        pingScheduler.shutdown();
        readPool.shutdown();
        processPool.shutdown();
        sendPool.shutdown();
        try {
            if (!pingScheduler.awaitTermination(2, TimeUnit.SECONDS)) pingScheduler.shutdownNow();
            if (!readPool.awaitTermination(5, TimeUnit.SECONDS)) readPool.shutdownNow();
            if (!processPool.awaitTermination(5, TimeUnit.SECONDS)) processPool.shutdownNow();
            if (!sendPool.awaitTermination(5, TimeUnit.SECONDS)) sendPool.shutdownNow();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            pingScheduler.shutdownNow();
            readPool.shutdownNow();
            processPool.shutdownNow();
            sendPool.shutdownNow();
        }
    }

    @Override
    public Closeable getTunnel() { return CHANNEL; }

    @Override
    public void setRunning(boolean condition) { isRunning = condition; }

    @Override
    public Invoker getInvokerFather() { return invoker; }

    @Override
    public Logger getLogger() { return logger; }

    @Override
    public String toString() { return "ThreadClient"; }

}