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
import main.Container;
import main.Invoker;
import main.OrganizationContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import organization.Organization;
import security.MD2Hash;
import security.User;
import sorts.SortById;

import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.UUID;

public class UdpServer extends Runner {
    private DatagramSocket SOCKET;
    private final HashMap<UUID,SocketAddress> socketAddressMap = new HashMap<>();

    public UdpServer(Invoker invoker, int port,boolean isLab7) {
        super(port, invoker,isLab7);
        invoker.setRunner(this);
        logger = LogManager.getLogger(UdpServer.class);

    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {

        Invoker invoker = new Invoker();
        UdpServer server = new UdpServer(invoker, 9898,true);
        OrganizationDao.setRunner(server);
        try {
            server.setUser(UserDao.getInstance().findByUserName("server"));
        } catch (NoSuchEntityException e) {
            logger.fatal("Сервер не может быть запущен из-за отсутствия пользователя server в базе данных");
        }

        if (!server.isLab7){
            OrganizationContainer container = new OrganizationContainer(new SortById<>());
            invoker.setCommand(new SaveCommand("save", invoker));
            String filePath = System.getenv("LAB5_8");
            Path path = InputManager.parseInitCollection(filePath);
            if (path != null) container.addList(XmlUtil.readListFromFile(path));
        }


        server.applyParams();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> ((ExitCommand) server
                .getInvokerFather()
                .getAllCommands()
                .get("exit"))
                .setInterrupt(true)
                .execute(null)));

        server.run();
    }

    public void connect() {
        try {
            SOCKET = new DatagramSocket(port);
            SOCKET.setSoTimeout(35);
            logger.info("Сервер подключился к сети");
            //todo добавть селектор
        } catch (SocketException e) {
            logger.error("Сервер не смог подключиться к сети по порту {}", port, e);
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
            ping(request);

            SocketAddress address = socketAddressMap.get(request.runnerId());
            if (request.requestType() != RequestType.PING) {
                System.out.println();
                logger.info("Сообщение получено от клиента #{}#{}", address,request.user());
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
            connect();
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
                        Command command = request.command();
                        logger.info(command);
                        //todo можно добавить проверку на корректный реквест
//                        logger.debug("---------2----");
                        logger.debug("{} \n-- req", request);
                        Request request1 = request.command().setInvokerFather(invoker).execute(request.user());
                        if (request1 != null){
                            logger.debug("отправил");
                            sendAndWait(request1.setRunnerId(request.runnerId()));
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
