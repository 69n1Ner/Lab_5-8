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
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadServer extends Runner {
    private DatagramSocket SOCKET;
    private final HashMap<UUID, SocketAddress> socketAddressMap = new HashMap<>();
    private static final Logger logger = LogManager.getLogger(ThreadServer.class);


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

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.setRunning(false);
            ((ExitCommand) server.getInvokerFather().getAllCommands().get("exit"))
                    .setInterrupt(true)
                    .execute(null);
        }));

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
        try {
            byte[] buf = ByteUtil.toByteArray(request, ARRAY_SIZE);
            SocketAddress address;

                address = socketAddressMap.get(request.runnerId());

            isUnreachable = false;
            if (address == null) {
                isUnreachable = true;
                return;
            }

            DatagramPacket toClient = new DatagramPacket(buf, buf.length, address);

                SOCKET.send(toClient);
        } catch (IOException e) {
            logger.warn("{} {}", this.getClass().getSimpleName(), e);
        }
    }

    @Override
    public Request receiveMessage() {
        try {
            byte[] buf = new byte[ARRAY_SIZE];
            DatagramPacket fromClient = new DatagramPacket(buf, ARRAY_SIZE);

            SOCKET.receive(fromClient);

            Request request = ByteUtil.fromBytesTo(fromClient.getData(), Request.class);

                socketAddressMap.put(request.runnerId(), fromClient.getSocketAddress());

            if (request.requestType() != RequestType.PING) {
                System.out.println();
                logger.info("Сообщение получено от клиента #{}#{}",
                        fromClient.getSocketAddress(), request.user());
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
        Path path1 = Path.of(path);

        if (isScript) {
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(path1.toFile())));
            } catch (FileNotFoundException e) {
                logger.warn(e);
                return;
            }
        } else {
            br = new BufferedReader(new InputStreamReader(System.in));
        }

        readPool.submit(()->readLoop(isScript));

        while (isRunning) {
            try {
                Thread.sleep(50);

                if (!isScript && initialOnlineShowUser) {
                    showUser();
                    initialOnlineShowUser = false;
                }

                if (br.ready()) {
                    String input = br.readLine();

                    if (isScript) {
                        if (input == null) {
                            logger.info("Файл {} обработан полностью", path);
                            break;
                        }
                        if (input.trim().isEmpty()) {
                            continue;
                        }
                        logger.info(input);
                    }

                    // Отправка команд от сервера клиентам
                    Request request = invoker.defineCommand(input, isScript).execute(user);
                    if (isRunning && SOCKET != null && !SOCKET.isClosed() && request != null) {
                        sendAndWait(request.setRunnerId(runnerId));
                    }

                    if (!isScript && isRunning) {
                        showUser();
                        System.out.flush();
                    }
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn(e);
            } catch (SocketException e) {
                if (!isRunning) break;
                logger.warn("Ошибка сокета: {}", e.getMessage());
            } catch (NoSuchEntityException | RecursionLimitReached | XmlUtilException | IOException e) {
                logger.warn("{}", e.getMessage());
                if (!isScript && isRunning) {
                    showUser();
                }
            }
        }
    }

    /// reading
    private void readLoop(boolean isScript) {

        while (isRunning) {
            try {
                Request request = receiveMessage();

                if (request != null) {
                    if (request.requestType() != RequestType.PING) {
                        processPool.submit(() -> processClientRequest(request,isScript));
                    } else {
                        sendPool.submit(() -> {
                            Request response = Request.build()
                                    .setRunnerId(request.runnerId())
                                    .setRequestType(RequestType.PING);
                            sendAndWait(response);
                        });
                    }
                }
            } catch (Exception e) {
                if (isRunning) {
                    logger.error("Ошибка в потоке-читателе", e);
                }
            }
        }
    }

    /// processing
    private void processClientRequest(Request request,boolean isScript) {
        try {
            Request response;

            /// authorization/registration user response with thread
            if (request.requestType() == RequestType.USER) {
                response = handleUserRequest(request);
            }

            /// command response with thread
            else if (request.requestType() == RequestType.COMMAND) {
                response = handleCommandRequest(request);
            }

            /// undefined request type response
            else {
                response = Request.build()
                        .setFeedback("Неизвестный тип реквеста")
                        .setRequestType(RequestType.FEEDBACK);
            }
            /// sending with thread
            if (response != null) {
                sendPool.submit(() -> sendResponse(response, request,isScript));
            }

        } catch (Exception e) {
            logger.error("Ошибка обработки запроса от {}", request.user(), e);
        }
    }

    /// authorization/registration user response
    private Request handleUserRequest(Request request) {
        boolean isRegistration = request.isScript();
        UserDao userDao = UserDao.getInstance();
        User user1 = request.user();
        StringBuilder feedback = new StringBuilder();
        Request response = Request.build().setRequestType(RequestType.USER_WRONG);

        /// authorization
        if (!isRegistration) {
            Optional<User> optionalUser = userDao.findAll().stream()
                    .filter(u -> u.getUserName().equals(user1.getUserName()))
                    .findFirst();

            if (optionalUser.isEmpty()) {
                feedback.append("Такого пользователя не существует");
            } else if (optionalUser.get().getPassword().equals(user1.getPassword())) {
                feedback.append("Вы успешно авторизовались");
                response = response.setRequestType(RequestType.USER_OK).setUser(optionalUser.get());
                logger.debug("request1={}", response);
            } else {
                feedback.append("Неверный пароль");
            }
        } else {
            /// registration
            synchronized (userDao) {
                ObjWithFeedback<Integer> o = userDao.save(user1, null);
                long id = o.object();
                List<String> l = o.feedback();

                if (id > 0 && l.isEmpty()) {
                    feedback.append("Вы успешно зарегистрировались");
                    ObjWithFeedback<User> u = userDao.findById(id);
                    User user2 = u.object();
                    List<String> lu = u.feedback();

                    if (!lu.isEmpty()) {
                        for (String s1 : lu) {
                            feedback.append(s1);
                        }
                    } else {
                        response = response.setRequestType(RequestType.USER_OK).setUser(user2);
                    }
                } else {
                    feedback.append("Произошла ошибка при добавлении пользователя");
                    for (String s : l) {
                        feedback.append("\n").append(s);
                    }
                }
            }
        }
        response = response.setFeedback(feedback.toString());
        logger.debug("после фидбека response={}", response);

        return response;
    }

    /// processing
    private Request handleCommandRequest(Request request) {
        Command command = request.command();
        logger.info("Получена команда: {}", command);

        // Синхронизация доступа к invoker и коллекции
        synchronized (OrganizationDao.getInstance()) {
            return command.setInvokerFather(invoker).execute(request.user());
        }
    }
    /// sending
    private void sendResponse(Request response, Request originalRequest,boolean isScript) {
        try {
            response = response.setRunnerId(originalRequest.runnerId());
            sendAndWait(response);

            if (!isScript && isRunning) {
                showUser();
            }
        } catch (Exception e) {
            logger.error("Ошибка отправки ответа", e);
        }
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    public Invoker getInvoker() {
        return invoker;
    }

    @Override
    public Closeable getTunnel() {
        return SOCKET;
    }

    public void setRunning(boolean condition) {

    }

    @Override
    public Invoker getInvokerFather() {
        return invoker;
    }

    public BufferedReader getBr() {
        return br;
    }

    @Override
    public String toString() {
        return "ThreadServer";
    }

    @Override
    public UUID getRunnerId() {
        return runnerId;
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