package net;

import exceptions.*;
import io.ByteUtil;
import main.Invoker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.UUID;

public class UdpClient implements Runner {
    private static final String IP_ADDRESS = "localhost";
    private static final int ARRAY_SIZE = 1500;
    private final int port;
    private DatagramChannel CHANNEL;
    private static Logger logger;
    private final Invoker invoker;
    private BufferedReader br;
    private boolean isRunning;
    private final UUID uuid = UUID.randomUUID();

    private UdpClient(Invoker invoker, int port){
        this.invoker = invoker;
        this.port = port;
        this.invoker.setRunner(this);
        logger = LogManager.getLogger(UdpClient.class);
    }

    public static void main(String[] args) throws IOException {
        Invoker invoker = new Invoker(null);
        UdpClient client = new UdpClient(invoker,9898);

        client.applyParams();

        client.run();
    }
    public void connect() {
        try {
            CHANNEL = DatagramChannel.open();
            CHANNEL.configureBlocking(false);
            SocketAddress socketAddress = new InetSocketAddress(IP_ADDRESS,port);
            CHANNEL.connect(socketAddress);
            Request ping = Request.build(uuid).setRequestType(RequestType.PING);
            sendMessage(ping);

            logger.info("Клиент запущен и готов отправлять данные");
        } catch (IOException e) {
            logger.error("Сервер не смог подключиться к сети по порту {}",port,e);
            logger.warn("Сервер не подключен к сети");
        }
    }

    @Override
    public void sendMessage(Request request) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(ByteUtil.toByteArray(request, ARRAY_SIZE));
            InetSocketAddress address = new InetSocketAddress(IP_ADDRESS, port);
            CHANNEL.send(buffer, address);
            if (request.requestType() != RequestType.PING) logger.info("Сообщение направлено на сервер #{}",address);
        }catch (PortUnreachableException e){
            logger.warn("Сервер не подключен к сети");
            connect();
        }catch (IOException e){
            logger.warn(e);
        }
    }

    @Override
    public  Request receiveMessage() {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(ARRAY_SIZE);
            SocketAddress address = CHANNEL.receive(buffer);

            if (address == null) {
                return null;
            }

            buffer.flip();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            logger.info("Сообщение получено от сервера #{}", address);
            return ByteUtil.fromBytesTo(data, Request.class);
        }catch (SocketTimeoutException e) {
            return null;
        }catch (PortUnreachableException e){
            logger.warn("Сервер не подключен к сети");
            return Request.build(uuid);
        }catch (IOException | ClassNotFoundException e){
            logger.warn(Arrays.toString(e.getStackTrace()).replace(",","\n"),e);
            return null;
        }
    }

    @Override
    public void run(){
        run(false,"");
    }
    @Override
    public void run(boolean isScript, String path){
        isRunning = true;
        Path path1 = Path.of(path);
        if (isScript){
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(path1.toFile())));
            } catch (FileNotFoundException e) {
                logger.warn(e);
                System.out.println(e.getMessage());
                return;
            }
        } else {
            connect();
            br = new BufferedReader(new InputStreamReader(System.in));
        }

        if (!isScript) {
            System.out.print("$user: ");
        }

        while (isRunning) {
            try {
                Thread.sleep(300);

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
                        //showing what command was
                        logger.info(input);
                    }

                    invoker.defineCommand(input, isScript,uuid).execute();
                    if (!isScript && isRunning) {
                        System.out.print("$user: ");
                        System.out.flush();
                    }
                }

                if (isRunning && CHANNEL != null) {
                    Request request1 = receiveMessage();

                    if (request1 != null) {
                        logger.debug(request1);
                        if (request1.requestType() == null){
                            if (!isScript && isRunning) {
                                System.out.print("$user: ");
                                System.out.flush();
                            }
                            continue;
                        }

                        logger.info(request1.feedback());
                        if (!isScript && isRunning) {
                            System.out.print("$user: ");
                            System.out.flush();
                        }
                    }

                }
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
                logger.warn(e);
            } catch (NoSuchCommandException | RecursionLimitReached | EmptyContainerException | XmlUtilException | IOException e) {
                logger.warn("{}",e.getMessage(),e);

                if (!isScript && isRunning) {
                    System.out.print("$user: ");
                    System.out.flush();
                }
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
    public UUID getUuid() {
        return uuid;
    }
}
