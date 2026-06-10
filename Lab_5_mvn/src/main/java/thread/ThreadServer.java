package thread;

import commands.Command;
import commands.ExitCommand;
import commands.SaveCommand;
import db.OrganizationDao;
import db.UserDao;
import exceptions.*;
import io.ByteUtil;
import io.InputManager;
import io.ObjWithFeedback;
import io.XmlUtil;
import main.Invoker;
import main.OrganizationContainer;
import net.Request;
import net.RequestType;
import net.Runner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import security.User;
import sorts.SortById;

import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;

public class ThreadServer extends Runner {
    private DatagramSocket SOCKET;
    private final HashMap<UUID, SocketAddress> socketAddressMap = new HashMap<>();
    private static final Logger logger = LogManager.getLogger(ThreadServer.class);

    private static final int RECEIVER_THREADS = 2;
    private static final int PROCESSOR_THREADS = 4;

    private final ExecutorService receiverPool = Executors.newFixedThreadPool(RECEIVER_THREADS);
    private final ExecutorService processorPool = Executors.newFixedThreadPool(PROCESSOR_THREADS);
    private final ForkJoinPool senderPool = new ForkJoinPool();

    public ThreadServer(Invoker invoker, int port, boolean isLab7) {
        super(port, invoker, isLab7);
        invoker.setRunner(this);
    }

    public static void main(String[] args) {
        Invoker invoker = new Invoker();
        ThreadServer server = new ThreadServer(invoker, 9898, true);
        OrganizationDao.setRunner(server);
        try {
            server.setUser(UserDao.getInstance().findByUserName("server"));
        } catch (NoSuchEntityException e) {
            logger.fatal("Сервер не может быть запущен из-за отсутствия пользователя server в базе данных");
            return;
        }

        if (!server.isLab7) {
            OrganizationContainer container = new OrganizationContainer(new SortById<>());
            invoker.setCommand(new SaveCommand("save", invoker));
            String filePath = System.getenv("LAB5_8");
            Path path = InputManager.parseInitCollection(filePath);
            if (path != null) container.addList(XmlUtil.readListFromFile(path));
        }

        server.applyParams(true);
        server.connect();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> ((ExitCommand) server
                .getInvokerFather()
                .getAllCommands()
                .get("exit"))
                .setInterrupt(true)
                .execute(null)));

        server.run();
    }

    @Override
    public void connect() {
        try {
            SOCKET = new DatagramSocket(port);
            SOCKET.setSoTimeout(35);
            logger.info("Сервер подключился к сети");
        } catch (SocketException e) {
            String t = "Сервер не смог подключиться к сети по порту " + port;
            logger.error(t, e);
            throw new RuntimeException(t);
        }
    }

    @Override
    public void sendMessage(Request request) {
        SocketAddress address;
        synchronized (socketAddressMap) {
            address = socketAddressMap.get(request.runnerId());
        }
        if (address == null) {
            isUnreachable = true;
            return;
        }
        try {
            byte[] buf = ByteUtil.toByteArray(request, ARRAY_SIZE);
            DatagramPacket toClient = new DatagramPacket(buf, buf.length, address);
            isUnreachable = false;
            synchronized (SOCKET) {
                SOCKET.send(toClient);
            }
        } catch (IOException e) {
            logger.warn("{} {}", this.getClass().getSimpleName(), e);
        }
    }

    @Override
    public Request receiveMessage() {
        try {
            byte[] buf = new byte[ARRAY_SIZE];
            DatagramPacket fromClient = new DatagramPacket(buf, ARRAY_SIZE);
            synchronized (SOCKET) {
                SOCKET.receive(fromClient);
            }
            Request request = ByteUtil.fromBytesTo(fromClient.getData(), Request.class);
            synchronized (socketAddressMap) {
                socketAddressMap.put(request.runnerId(), fromClient.getSocketAddress());
            }
            if (request.requestType() != RequestType.PING) {
                SocketAddress address;
                synchronized (socketAddressMap) {
                    address = socketAddressMap.get(request.runnerId());
                }
                logger.info("Сообщение получено от клиента #{}#{}", address, request.user());
                logger.debug("request={}", request);
            }
            return request;
        } catch (SocketTimeoutException | PortUnreachableException e) {
            return null;
        } catch (IOException | ClassNotFoundException e) {
            logger.debug("receiveMsg{}", e, e);
            return null;
        }
    }

    @Override
    public void run() {
        run(false, "", isLab7);
    }

    @Override
    public void run(boolean isScript, String path, boolean isLab7) {
        isRunning = true;

        if (isScript) {
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(Path.of(path).toFile())));
            } catch (FileNotFoundException e) {
                logger.warn(e);
                return;
            }
        } else {
            br = new BufferedReader(new InputStreamReader(System.in));
        }

        if (!isScript && initialOnlineShowUser) {
            showUser();
            initialOnlineShowUser = false;
        }

        for (int i = 0; i < RECEIVER_THREADS; i++) {
            receiverPool.submit(() -> receiveLoop(isScript));
        }

        consoleLoop(isScript, path);

        shutdownPools();
    }

    private void receiveLoop(boolean isScript) {
        while (isRunning) {
            try {
                Thread.sleep(5);
                if (SOCKET == null || SOCKET.isClosed()) break;

                Request request = receiveMessage();
                if (request == null) continue;

                processorPool.submit(() -> handleRequest(request, isScript));

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void handleRequest(Request request, boolean isScript) {
        try {
            if (request.requestType() == RequestType.PING) {
                Request pong = Request.build()
                        .setRunnerId(request.runnerId())
                        .setRequestType(RequestType.PING);
                submitSend(pong);
                return;
            }

            Request response;

            if (request.requestType() == RequestType.USER) {
                response = handleUserRequest(request);
            } else if (request.requestType() == RequestType.COMMAND) {
                Command command = request.command();
                logger.info(command);
                response = command.setInvokerFather(invoker).execute(request.user());
            } else {
                response = Request.build()
                        .setFeedback("Неизвестный тип реквеста")
                        .setRequestType(RequestType.FEEDBACK);
            }

            if (response != null) {
                response = response.setRunnerId(request.runnerId());
                submitSend(response);
                if (!isScript && isRunning) {
                    showUser();
                }
            }

        } catch (RecursionLimitReached | XmlUtilException e) {
            logger.warn("{}", e.getMessage());
        }
    }

    private Request handleUserRequest(Request request) {
        boolean isRegistration = request.isScript();
        UserDao userDao = UserDao.getInstance();
        User user1 = request.user();
        StringBuilder feedback = new StringBuilder();
        Request result = Request.build().setRequestType(RequestType.USER_WRONG);

        if (!isRegistration) {
            Optional<User> optionalUser = userDao.findAll().stream()
                    .filter(u -> u.getUserName().equals(user1.getUserName()))
                    .findFirst();
            if (optionalUser.isEmpty()) {
                feedback.append("Такого пользователя не существует");
            } else if (optionalUser.get().getPassword().equals(user1.getPassword())) {
                feedback.append("Вы успешно авторизовались");
                result = result.setRequestType(RequestType.USER_OK).setUser(optionalUser.get());
                logger.debug("request1={}", result);
            } else {
                feedback.append("Неверный пароль");
            }
        } else {
            ObjWithFeedback<Integer> o = userDao.save(user1, null);
            long id = o.object();
            List<String> l = o.feedback();
            if (id > 0 && l.isEmpty()) {
                feedback.append("Вы успешно зарегистрировались");
                ObjWithFeedback<User> u = userDao.findById(id);
                User user2 = u.object();
                List<String> lu = u.feedback();
                if (!lu.isEmpty()) {
                    lu.forEach(s -> feedback.append(s));
                } else {
                    result = result.setRequestType(RequestType.USER_OK).setUser(user2);
                }
            } else {
                feedback.append("Произошла ошибка при добавлении пользователя");
                l.forEach(s -> feedback.append("\n").append(s));
            }
        }

        result = result.setFeedback(feedback.toString());
        logger.debug("после фидбека request1={}", result);
        return result;
    }

    private void submitSend(Request request) {
        senderPool.submit(() -> {
            try {
                sendAndWait(request.setRunnerId(request.runnerId()));
            } catch (Exception e) {
                logger.warn("Ошибка отправки: {}", e.getMessage());
            }
        });
    }

    private void consoleLoop(boolean isScript, String path) {
        while (isRunning) {
            try {
                Thread.sleep(5);
                if (!br.ready()) continue;

                String input = br.readLine();

                if (isScript) {
                    if (input == null) {
                        logger.info("Файл {} обработан полностью", path);
                        break;
                    }
                    if (input.trim().isEmpty()) continue;
                    logger.info(input);
                }

                Request request = invoker.defineCommand(input, isScript).execute(user);
                if (isRunning && SOCKET != null && !SOCKET.isClosed() && request != null) {
                    sendAndWait(request.setRunnerId(runnerId));
                }
                if (!isScript && isRunning) {
                    showUser();
                    System.out.flush();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (SocketException e) {
                if (!isRunning) break;
                logger.warn("Ошибка сокета: {}", e.getMessage());
            } catch (NoSuchEntityException | RecursionLimitReached | XmlUtilException | IOException e) {
                logger.warn("{}", e.getMessage());
                if (!isScript && isRunning) showUser();
            }
        }
    }

    private void shutdownPools() {
        receiverPool.shutdown();
        processorPool.shutdown();
        senderPool.shutdown();
        try {
            if (!receiverPool.awaitTermination(5, TimeUnit.SECONDS)) receiverPool.shutdownNow();
            if (!processorPool.awaitTermination(5, TimeUnit.SECONDS)) processorPool.shutdownNow();
            if (!senderPool.awaitTermination(5, TimeUnit.SECONDS)) senderPool.shutdownNow();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            receiverPool.shutdownNow();
            processorPool.shutdownNow();
            senderPool.shutdownNow();
        }
    }

    @Override
    public Logger getLogger() { return logger; }

    public Invoker getInvoker() { return invoker; }

    @Override
    public Closeable getTunnel() { return SOCKET; }

    @Override
    public void setRunning(boolean condition) { isRunning = false; }

    @Override
    public Invoker getInvokerFather() { return invoker; }

    public BufferedReader getBr() { return br; }

    @Override
    public String toString() { return "UdpServer"; }

    @Override
    public UUID getRunnerId() { return runnerId; }
}