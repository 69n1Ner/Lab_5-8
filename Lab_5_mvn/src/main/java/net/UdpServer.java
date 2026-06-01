package net;

import commands.Command;
import commands.ExitCommand;
import commands.SaveCommand;
import exceptions.*;
import io.ByteUtil;
import main.Container;
import main.Invoker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import organization.Organization;

import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class UdpServer extends Runner {
    private static final int ARRAY_SIZE = 65000;
    private final int port;
    private static final Logger logger = LogManager.getLogger(UdpServer.class);
    private Invoker invoker;
    private volatile boolean isRunning;
    private DatagramSocket SOCKET;
    private final HashMap<UUID,SocketAddress> socketAddressMap = new HashMap<>();

    private UdpServer(Invoker invoker, int port) {
        super(port, invoker);
        this.invoker = invoker;
        this.port = port;
        invoker.setRunner(this);

    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {


        Container<Organization> container = new Container<>();
        Invoker invoker = new Invoker(container);
        invoker.setCommand(new SaveCommand("save", invoker));
        UdpServer server = new UdpServer(invoker, 9898);

        server.applyParams();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> ((ExitCommand) server
                .getInvokerFather()
                .getAllCommands()
                .get("exit"))
                .setInterrupt(true)
                .execute()));

        server.run();
    }

    public void connect() {
        try {
            SOCKET = new DatagramSocket(port);
            SOCKET.setSoTimeout(20);
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
            logger.debug("принят буфер");
            SocketAddress address = socketAddressMap.get(request.runnerId());
            DatagramPacket toClient = new DatagramPacket(buf, buf.length, address);
            logger.debug("создан пакет");
            SOCKET.send(toClient);
            logger.info("Сообщение отправлено клиенту #{}#{}", address, request.runnerId());
        } catch (IOException e) {
            logger.warn(e);
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
            ping();

            socketAddressMap.put(request.runnerId(),fromClient.getSocketAddress());
            SocketAddress address = socketAddressMap.get(request.runnerId());
            if (request.requestType() != RequestType.PING) logger.info("Сообщение получено от клиента #{}#{}", address,request.runnerId());
            return request;
        } catch (IOException | ClassNotFoundException e) {
            logger.warn(e);
            return null;
        }
    }

    @Override
    public void run() {
        run(false, "");
    }

    @Override
    public void run(boolean isScript, String path) {
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

        if (!isScript) {
            System.out.print("$user: ");
            System.out.flush();
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
                        //shows what command was
                        logger.info(input);
                    }
                    logger.debug("---------1----");

                    //sending
                    invoker.defineCommand(input, isScript, null).execute();
                    Request request = invoker.defineCommand(input, isScript, runnerId).execute();
                    if (isRunning && SOCKET != null && !SOCKET.isClosed() && request != null) {
                        request.setRunnerId(runnerId);
                        sendMessage(request);
                    }
                    if (!isScript && isRunning) {
                        System.out.print("$user: ");
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
                        logger.debug("---------2----");
                        logger.debug("{} -- req", request);

                        invoker.defineCommand(command.toString(), request.isScript(), request.runnerId()).execute();
                    }
                }

            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
                logger.warn(e);
            } catch (SocketException e) {
                if (!isRunning) break;
                logger.warn("Ошибка сокета: {}", e.getMessage());

            } catch (NoSuchCommandException | RecursionLimitReached | XmlUtilException | IOException e) {
                logger.warn("{}", e.getMessage());
                if (!isScript && isRunning) {
                    System.out.print("$user: ");
                    System.out.flush();
                }
            }catch (NullPointerException e){
                logger.debug(Arrays.toString(e.getStackTrace()).replace(",","\n"));
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
        this.invoker = invoker;
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
