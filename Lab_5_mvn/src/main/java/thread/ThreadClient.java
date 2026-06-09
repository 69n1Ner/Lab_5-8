package thread;

import db.OrganizationDao;
import io.ByteUtil;
import io.InputManager;
import main.Invoker;
import net.Request;
import net.RequestType;
import net.Runner;
import net.UdpClient;
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
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadClient extends Runner {
    private DatagramChannel CHANNEL;
    private final Deque<Request> cachedMessages = new ArrayDeque<>();
    private static final Logger logger = LogManager.getLogger(ThreadClient.class);

    // ПУЛЫ ПОТОКОВ
    private final ExecutorService readPool;          // Чтение ответов от сервера
    private final ExecutorService processPool;       // Обработка полученных ответов
    private final ForkJoinPool sendPool;             // Отправка запросов на сервер
    private final ExecutorService consoleReaderPool; // Чтение команд с консоли
    private final ExecutorService fileReaderPool;    // Чтение команд из файла

    // Очередь команд от читателей (консоль/файл)
    private final BlockingQueue<CommandInput> commandQueue = new LinkedBlockingQueue<>(100);

    // Объект для синхронизации доступа к общим ресурсам
    private final Object channelLock = new Object();
    private final Object cacheLock = new Object();

    public ThreadClient(Invoker invoker, int port, boolean isLab7) {
        super(port, invoker, isLab7);
        super.invoker.setRunner(this);

        // Инициализируем пулы с кастомными именами потоков
        this.readPool = Executors.newFixedThreadPool(2, new CustomThreadFactory("READER"));
        this.processPool = Executors.newFixedThreadPool(5, new CustomThreadFactory("PROCESSOR"));
        this.sendPool = new ForkJoinPool(4);
        this.consoleReaderPool = Executors.newFixedThreadPool(1, new CustomThreadFactory("CONSOLE-READER"));
        this.fileReaderPool = Executors.newFixedThreadPool(1, new CustomThreadFactory("FILE-READER"));
    }

    public static void main(String[] args) {
        Invoker invoker = new Invoker();
        UdpClient client = new UdpClient(invoker, 9898, true);
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
        User user;

        boolean isRegistration;
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
            SocketAddress socketAddress = new InetSocketAddress(IP_ADDRESS, port);
            CHANNEL.connect(socketAddress);

            logger.info("Клиент запущен и готов отправлять данные");
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
            SocketAddress address = new InetSocketAddress(IP_ADDRESS, port);

            // Синхронизация отправки через канал
            synchronized (channelLock) {
                CHANNEL.send(buffer, address);
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

            // Синхронизация чтения из канала
            synchronized (channelLock) {
                address = CHANNEL.receive(buffer);
            }

            if (address == null) {
                return null;
            }

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
        isRunning = true;
        Path path1 = Path.of(path);

        // ЗАПУСКАЕМ ПОТОК-ЧИТАТЕЛЬ ОТВЕТОВ ОТ СЕРВЕРА (Reader)
        readPool.submit(this::readLoop);

        // ЗАПУСКАЕМ ЧТЕНИЕ КОМАНД ИЗ КОНСОЛИ (всегда)
        consoleReaderPool.submit(this::consoleLoop);

        // ЗАПУСКАЕМ ЧТЕНИЕ КОМАНД ИЗ ФАЙЛА (если скрипт)
        if (isScript) {
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(path1.toFile())));
                fileReaderPool.submit(() -> fileLoop(path));
            } catch (FileNotFoundException e) {
                logger.warn(e.getMessage());
                isRunning = false;
                return;
            }
        }

        // ГЛАВНЫЙ ЦИКЛ КЛИЕНТА
        // Обрабатывает отправку команд и обработку полученных ответов
        while (isRunning) {
            try {
                Thread.sleep(5);

                if (!isScript && initialRunShowUser) {
                    showUser();
                    initialRunShowUser = false;
                }

                // Пинг для проверки связи
                ping(Request.build().setRequestId(UUID.randomUUID()).setRunnerId(runnerId));

                // Читаем команды из очереди (от консоли или файла)
                CommandInput commandInput = commandQueue.poll();
                if (commandInput != null) {
                    processCommandInput(commandInput);
                }

                // Обработка полученных ответов (из кеша)
                processCachedMessages(isScript);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn(e.getMessage());
            } catch (Exception e) {
                logger.warn("{}", e.getMessage());
                if (!isScript && isRunning) {
                    showUser();
                }
            }
        }
    }

    /**
     * ПОТОК-ЧИТАТЕЛЬ ОТВЕТОВ ОТ СЕРВЕРА (Reader)
     * Работает в readPool, постоянно читает ответы от сервера
     */
    private void readLoop() {
        logger.info("Поток-читатель ответов запущен");

        while (isRunning) {
            try {
                Request response = receiveMessage();

                if (response != null) {
                    if (response.requestType() == RequestType.PING) {
                        // PING обрабатываем сразу (быстрый ответ)
                        sendPool.submit(() -> {
                            Request pingResponse = Request.build()
                                    .setRunnerId(response.runnerId())
                                    .setRequestType(RequestType.PING);
                            sendMessage(pingResponse);
                        });
                    } else {
                        // Все остальные ответы передаем в processPool
                        processPool.submit(() -> processServerResponse(response));
                    }
                }

                Thread.sleep(10); // Небольшая пауза для неблокирующего канала

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                if (isRunning) {
                    logger.error("Ошибка в потоке-читателе ответов", e);
                }
            }
        }

        logger.info("Поток-читатель ответов остановлен");
    }

    /**
     * ПОТОК-ЧИТАТЕЛЬ КОНСОЛИ
     * Работает в consoleReaderPool, читает команды с консоли
     */
    private void consoleLoop() {
        logger.info("Поток-читатель консоли запущен");
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

        while (isRunning) {
            try {
                if (consoleReader.ready()) {
                    String input = consoleReader.readLine();
                    if (input != null) {
                        // Передаем команду в очередь
                        commandQueue.put(new CommandInput(input, false, ""));
                    }
                }
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (IOException e) {
                logger.error("Ошибка чтения консоли", e);
            }
        }

        logger.info("Поток-читатель консоли остановлен");
    }

    /**
     * ПОТОК-ЧИТАТЕЛЬ ФАЙЛА
     * Работает в fileReaderPool, читает команды из файла
     */
    private void fileLoop(String path) {
        logger.info("Поток-читатель файла запущен: {}", path);

        try {
            String input;
            while (isRunning && (input = br.readLine()) != null) {
                if (input.trim().isEmpty()) {
                    continue;
                }
                logger.info("[СКРИПТ] {}", input);
                // Передаем команду в очередь
                commandQueue.put(new CommandInput(input, true, path));
                Thread.sleep(10); // Небольшая пауза между командами скрипта
            }

            if (isRunning) {
                logger.info("Файл {} обработан полностью", path);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            logger.error("Ошибка чтения файла", e);
        }

        logger.info("Поток-читатель файла остановлен");
    }

    /**
     * Обработка команды из очереди (от консоли или файла)
     */
    private void processCommandInput(CommandInput commandInput) {
        try {
            String input = commandInput.input();
            boolean isScript = commandInput.isScript();

            // Создаем запрос
            Request request = invoker.defineCommand(input, isScript).execute(user);

            if (isRunning && CHANNEL != null && request != null) {
                // Отправляем запрос в sendPool
                sendPool.submit(() -> {
                    Request response = null;
                    int attempts = 0;
                    while (response == null && attempts < 10 && isRunning) {
                        response = sendAndWait(request.setRunnerId(runnerId));
                        attempts++;
                    }

                    if (response != null) {
                        // Кладем ответ в кеш
                        synchronized (cacheLock) {
                            cachedMessages.addFirst(response);
                        }
                    }
                });
            }

        } catch (Exception e) {
            logger.warn("Ошибка обработки команды: {}", e.getMessage());
            if (!commandInput.isScript() && isRunning) {
                showUser();
            }
        }
    }

    /**
     * ПОТОК-ОБРАБОТЧИК ОТВЕТОВ ОТ СЕРВЕРА (Processor)
     * Работает в processPool, обрабатывает полученные ответы
     */
    private void processServerResponse(Request response) {
        try {
            // Кладем ответ в кеш
            synchronized (cacheLock) {
                cachedMessages.addFirst(response);
            }
        } catch (Exception e) {
            logger.error("Ошибка обработки ответа от сервера", e);
        }
    }

    /**
     * Обработка сообщений из кеша
     */
    private void processCachedMessages(boolean isScript) {
        Request response = null;

        synchronized (cacheLock) {
            if (!cachedMessages.isEmpty()) {
                response = cachedMessages.removeFirst();
            }
        }

        if (response != null && response.requestType() != RequestType.PING) {
            logger.info(response.feedback());
            if (!isScript && isRunning) {
                showUser();
            }
        }
    }

    @Override
    public Closeable getTunnel() {
        return CHANNEL;
    }

    @Override
    public void setRunning(boolean condition) {
        isRunning = condition;

        if (!condition) {
            logger.info("Остановка клиента...");

            // Корректное завершение всех пулов
            readPool.shutdown();
            processPool.shutdown();
            sendPool.shutdown();
            consoleReaderPool.shutdown();
            fileReaderPool.shutdown();

            try {
                if (!readPool.awaitTermination(5, TimeUnit.SECONDS)) {
                    readPool.shutdownNow();
                }
                if (!processPool.awaitTermination(5, TimeUnit.SECONDS)) {
                    processPool.shutdownNow();
                }
                if (!sendPool.awaitTermination(5, TimeUnit.SECONDS)) {
                    sendPool.shutdownNow();
                }
                if (!consoleReaderPool.awaitTermination(5, TimeUnit.SECONDS)) {
                    consoleReaderPool.shutdownNow();
                }
                if (!fileReaderPool.awaitTermination(5, TimeUnit.SECONDS)) {
                    fileReaderPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                readPool.shutdownNow();
                processPool.shutdownNow();
                sendPool.shutdownNow();
                consoleReaderPool.shutdownNow();
                fileReaderPool.shutdownNow();
                Thread.currentThread().interrupt();
            }

            try {
                if (CHANNEL != null && CHANNEL.isOpen()) {
                    CHANNEL.close();
                }
            } catch (IOException e) {
                logger.error("Ошибка закрытия канала", e);
            }

            logger.info("Клиент остановлен");
        }
    }

    @Override
    public Invoker getInvokerFather() {
        return invoker;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    public int getPort() {
        return port;
    }

    public Invoker getInvoker() {
        return invoker;
    }

    @Override
    public String toString() {
        return "UdpClient";
    }

    @Override
    public UUID getRunnerId() {
        return runnerId;
    }

    /**
     * Внутренний класс для хранения команды из очереди
     */
    private record CommandInput(String input, boolean isScript, String path) {
    }

    /**
     * Фабрика потоков с кастомными именами для логирования
     */
    private static class CustomThreadFactory implements ThreadFactory {
        private final String poolName;
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        public CustomThreadFactory(String poolName) {
            this.poolName = poolName;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, poolName + "-thread-" + threadNumber.getAndIncrement());
            t.setDaemon(true);
            return t;
        }
    }
}