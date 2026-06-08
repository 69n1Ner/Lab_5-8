package net;

import db.OrganizationDao;
import exceptions.*;
import io.ByteUtil;
import io.InputManager;
import main.Invoker;
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

public class UdpClient extends Runner {
    private DatagramChannel CHANNEL;
    private final Deque<Request> cachedMessages = new ArrayDeque<>();
    private static final Logger logger = LogManager.getLogger(UdpClient.class);


    public UdpClient(Invoker invoker, int port,boolean isLab7) {
        super(port, invoker,isLab7);
        super.invoker.setRunner(this);
    }

    public static void main(String[] args) {
        Invoker invoker = new Invoker();
        UdpClient client = new UdpClient(invoker, 9898,true);
        OrganizationDao.setRunner(client);

        client.applyParams(false);
        client.connect();

        User user1 = null;
        while (user1 == null){
            user1 = client.authorize();
        }
        client.setUser(user1);

        client.run();
    }

    public User authorize(){
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
        if (input == null || input.isEmpty() || input.equals("y") || input.equals("Y")){
            logger.info("===== Авторизация =====");
            isRegistration = false;
            user = InputManager.inputUser(br, false);
        } else {
            logger.info("===== Регистрация =====");
            isRegistration = true;
            user = InputManager.inputUser(br,true);
        }

        Request request = Request.build()
                .setRequestType(RequestType.USER)
                .setUser(user)
                .setRegistration(isRegistration)
                .setRunnerId(runnerId);

        Request response = null;
        while (response == null){
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
            Thread.sleep(100);

            logger.info("Клиент запущен и готов отправлять данные");
        } catch (IOException e) {
            String t = "Клиент не смог подключиться к сети по порту "+ port;
            logger.error(t,e);
            throw new RuntimeException(t);
        } catch (InterruptedException e) {
            logger.warn("interrupt");
        }
    }

    @Override
    public void sendMessage(Request request) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(ByteUtil.toByteArray(request, ARRAY_SIZE));
            SocketAddress address = new InetSocketAddress(IP_ADDRESS, port);
            CHANNEL.send(buffer, address);
            isUnreachable = false;
        }catch (PortUnreachableException e){
//            logger.debug("Client unreachable set to true");
            isUnreachable = true;
        } catch (IOException e) {
            logger.warn(e);
        }
    }


    @Override
    public Request receiveMessage() {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(ARRAY_SIZE);
            SocketAddress address = CHANNEL.receive(buffer);

            if (address == null) {
                return null;
            }

            buffer.flip();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            Request request = ByteUtil.fromBytesTo(data, Request.class);
//            logger.debug("получен реквест {}",request.requestType());
            if (request.requestType() != RequestType.PING) {
                logger.info("Сообщение получено от сервера #{}", address);
//                logger.debug("request={}",request);
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
                logger.warn(e.getMessage());
                return;
            }
        } else {
            br = new BufferedReader(new InputStreamReader(System.in));
        }

        while (isRunning) {
//            logger.debug("cycle started");
            try {
//                ping(Request.build().setRequestId(UUID.randomUUID()).setRunnerId(runnerId));

                if (br.ready()) {
                    String input = br.readLine();

                    if (isScript) {
                        if (input == null) {
                            logger.info("Файл {} обработан полностью", path);
                            break;
                        }

                        if (input.trim().isEmpty()) {
                            logger.debug("пустой инпут");
                            continue;
                        }
                        //showing what command was
                        logger.info(input);
                    }

                    Thread.sleep(100);

                    //sending
                    Request request = invoker.defineCommand(input, isScript).execute(user);
                    if (isRunning && CHANNEL != null && request != null) {
                        Request response = null;
                        while (response == null){
                            response = sendAndWait(request.setRunnerId(runnerId));
                        }
                        cachedMessages.addFirst(response);

                    }
                    if (!isScript && isRunning) {
                        System.out.print("$"+this.getUser()+": ");
                        System.out.flush();
                    }
                }

                //receiving
                if (isRunning && CHANNEL != null) {
//                    logger.debug("before receiveMessage");
                    Request request1 = null;
                    if (!cachedMessages.isEmpty()){
                         request1 = cachedMessages.removeFirst();
                    }
//                    logger.debug("after receiveMessage");


//                    logger.debug("request1={}",request1);
//                    logger.debug("request1 != null={}",request1 != null);
//                    if (request1 != null) {
//                        logger.debug("request1.requestType() != RequestType.PING={}", request1.requestType() != RequestType.PING);
//                    }

                    if (request1 != null && request1.requestType() != RequestType.PING) {
                        logger.info(request1.feedback());
                        if (!isScript && isRunning) {
                            System.out.print("$"+this.getUser()+": ");
                            System.out.flush();
                        }
                    }

                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn(e.getMessage());
            } catch (NoSuchEntityException | RecursionLimitReached | XmlUtilException |
                     IOException e) {
                logger.warn("{}", e.getMessage());
                if (!isScript && isRunning) {
                    System.out.print("$"+this.getUser()+": ");
                    System.out.flush();
                }

            }
//            logger.debug("cycle ended");
        }
    }

    @Override
    public Closeable getTunnel() {
        return CHANNEL;
    }


    @Override
    public void setRunning(boolean condition) {
        isRunning = condition;
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
}
