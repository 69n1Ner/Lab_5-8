package net;

import commands.Command;
import commands.ExitCommand;
import commands.SaveCommand;
import db.OrganizationDao;
import db.UserDao;
import exceptions.*;
import io.ByteUtil;
import io.InputManager;
import io.XmlUtil;
import main.Invoker;
import main.OrganizationContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import security.User;
import sorts.SortById;

import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class UdpServer extends Runner {
    private DatagramSocket SOCKET;
    private final HashMap<UUID,SocketAddress> socketAddressMap = new HashMap<>();
    private static final Logger logger = LogManager.getLogger(UdpServer.class);


    public UdpServer(Invoker invoker, int port,boolean isLab7) {
        super(port, invoker,isLab7);
        invoker.setRunner(this);

    }

    public static void main(String[] args) {

        Invoker invoker = new Invoker();
        UdpServer server = new UdpServer(invoker, 9898,true);
        OrganizationDao.setRunner(server);
        try {
            server.setUser(UserDao.getInstance().findByUserName("server"));
        } catch (NoSuchEntityException e) {
            logger.fatal("Сервер не может быть запущен из-за отсутствия пользователя server в базе данных");
            return;
        }

        if (!server.isLab7){
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
            //todo добавть селектор
        } catch (SocketException e) {
            String t = "Сервер не смог подключиться к сети по порту "+ port;
            logger.error(t,e);
            throw new RuntimeException(t);
        }
    }

    @Override
    public void sendMessage(Request request) {
        try {
            byte[] buf = ByteUtil.toByteArray(request, ARRAY_SIZE);
            SocketAddress address = socketAddressMap.get(request.runnerId());
//            logger.debug("address={}",address);
            isUnreachable = false;
            if (address == null){
                isUnreachable = true;
//                logger.debug("клиент не достижим");
                return;
            }
            DatagramPacket toClient = new DatagramPacket(buf, buf.length, address);
//            logger.debug("создан пакет");
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

            Request request;
            request = ByteUtil.fromBytesTo(fromClient.getData(), Request.class);
            socketAddressMap.put(request.runnerId(),fromClient.getSocketAddress());
//            logger.debug("map={}",socketAddressMap);

            SocketAddress address = socketAddressMap.get(request.runnerId());
            if (request.requestType() != RequestType.PING) {
                System.out.println();
                logger.info("Сообщение получено от клиента #{}#{}", address,request.user());
                logger.debug("request={}",request);
            }
            return request;
        } catch (SocketTimeoutException | PortUnreachableException e) {
            return null;
        } catch (IOException | ClassNotFoundException e) {
            logger.debug("recieveMsg{}", e,e);
            return null;
        }
    }

    @Override
    public void run() {
        run(false, "",isLab7);
    }

    @Override
    public void run(boolean isScript, String path,boolean isLab7) {
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

//        if (!isScript) {
//            System.out.print("$user: ");
//            System.out.flush();
//        }

        while (isRunning) {
            try {


                Thread.sleep(50);
                if (!isScript && initialShowUser) {
                    System.out.print("$"+this.getUser()+": ");
                    initialShowUser = false;
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
                        //shows what command was
                        logger.info(input);
                    }
//                    logger.debug("---------1----");

                    //sending
                    logger.debug("user={}", user);
                    Request request = invoker.defineCommand(input, isScript).execute(user);
                    if (isRunning && SOCKET != null && !SOCKET.isClosed() && request != null) {
                        sendAndWait(request.setRunnerId(runnerId));
                    }
                    if (!isScript && isRunning) {
                        System.out.print("$"+this.getUser()+": ");
                        System.out.flush();
                    }
                    continue;
                }

                //receiving
                if (isRunning && SOCKET != null && !SOCKET.isClosed()) {
                    Request request = receiveMessage();
                    if (request != null && request.requestType() != RequestType.PING) {
                        Request request1;

                        /// authorization/registration user response
                        if (request.requestType() == RequestType.USER){
                            boolean isRegistration = request.isScript();
                            UserDao userDao = UserDao.getInstance();
                            User user1 = request.user();
                            String feedback = "";
                            request1 = Request.build().setRequestType(RequestType.USER_WRONG);

                            if (!isRegistration) {
                                Optional<User> optionalUser = userDao
                                        .findAll()
                                        .stream()
                                        .filter(u -> u.getUserName().equals(user1.getUserName()))
                                        .findFirst();
                                if (optionalUser.isEmpty()) {
                                    feedback = "Такого пользователя не существует";
                                } else if (optionalUser.get().getPassword().equals(user1.getPassword())) {
                                    feedback = "Вы успешно авторизовались";
                                    request1 = request1.setRequestType(RequestType.USER_OK).setUser(optionalUser.get());
                                    logger.debug("request1={}",request1);
                                } else {
                                    feedback = "Неверный пароль";
                                }

                            } else {
                                long id = userDao.save(user1, null);
                                if (id > 0){
                                    feedback = "Вы успешно зарегистрировались";
                                    request1 = request1.setRequestType(RequestType.USER_OK).setUser(userDao.findById(id));
                                } else {
//                                    throw new RuntimeException();
                                    feedback = "Произошла ошибка при добавлении пользователя";
                                }
                            }
                            request1 = request1.setFeedback(feedback);
//                            logger.debug("после фидбека request1={}",request1);

                        /// command response
                        } else if (request.requestType() == RequestType.COMMAND) {
                            Command command = request.command();
                            logger.info(command);
                            //todo можно добавить проверку на корректный реквест
//                        logger.debug("---------2----");
//                            logger.debug("{} \n-- req", request);
                            request1 = request.command().setInvokerFather(invoker).execute(request.user());

                        /// undefined request type response
                        }else {
                            request1 = Request.build().setFeedback("Неизвестный тип реквеста").setRequestType(RequestType.FEEDBACK);
                        }

                        /// sending
                        if (request1 != null){
                            request1 = request1.setRunnerId(request.runnerId());
                            sendAndWait(request1);
                            if (!isScript && isRunning) {
                                System.out.print("$"+this.getUser()+": ");
                                System.out.flush();
                            }
                        }
                    }
                }

            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
                logger.warn(e);
            } catch (SocketException e) {
                if (!isRunning) break;
                logger.warn("Ошибка сокета: {}", e.getMessage());

            } catch (NoSuchEntityException | RecursionLimitReached | XmlUtilException | IOException e) {
                logger.warn("{}", e.getMessage());
                if (!isScript && isRunning) {
                    System.out.print("$"+this.getUser()+": ");
                    System.out.flush();
                }
            }catch (NullPointerException e){
                logger.debug("{}", e,e);
            }
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

    @Override
    public void setRunning(boolean condition) {
        isRunning = false;
    }

    @Override
    public Invoker getInvokerFather() {
        return invoker;
    }

    public void setInvokerFather(Invoker invoker){
        super.invoker = invoker;
    }

    public BufferedReader getBr() {
        return br;
    }

    @Override
    public String toString() {
        return "UdpServer";
    }


    @Override
    public UUID getRunnerId() {
        return runnerId;
    }
}
