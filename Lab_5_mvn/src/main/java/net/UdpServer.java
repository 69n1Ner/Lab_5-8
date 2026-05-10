package net;

import commands.Command;
import commands.GetLoggerable;
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
import java.util.NoSuchElementException;

public class UdpServer implements GetLoggerable,Runner {
    private static final  String IP_ADDRESS = "localhost";
    private static final int ARRAY_SIZE = 1500;
    private final int port;
    private DatagramSocket SOCKET ;
    private static final Logger logger = LogManager.getLogger(UdpServer.class);
    private final Invoker invoker;

    private UdpServer(Invoker invoker, int port){
        this.invoker = invoker;
        this.port = port;
        invoker.setRunner(this);
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Container<Organization> container = new Container<>();
        Invoker invoker = new Invoker(container);
        UdpServer server = new UdpServer(invoker,9898);
        server.run();


        server.SOCKET.close();
        logger.info("Закрыто"); //temp or ...
    }
    public void connect() {
        try {
            SOCKET = new DatagramSocket(port);
            SOCKET.setSoTimeout(20);
            //todo добавть селектор
        } catch (SocketException e) {
            logger.error("Сервер не смог подключиться к сети по порту {}",port,e);
        }
        logger.info("Сервер подключился к сети");
    }

    @Override
    public void sendMessage(Request request) {
        try {
            byte[] buf = ByteUtil.toByteArray(request, ARRAY_SIZE);
            InetSocketAddress address = new InetSocketAddress(IP_ADDRESS, port);
            DatagramPacket toClient = new DatagramPacket(buf, buf.length, address);
            SOCKET.send(toClient);
            logger.info("Сообщение отправлено клиенту #{}#{}", address, request.id());
        }catch (IOException e){
            logger.warn(e);
        }
    }

    @Override
    public Request receiveMessage() {
        try {
            byte[] buf = new byte[ARRAY_SIZE];
            DatagramPacket fromClient = new DatagramPacket(buf, ARRAY_SIZE);
            SOCKET.receive(fromClient);
            SocketAddress address = fromClient.getSocketAddress();
            Request request;
            request = ByteUtil.fromBytesTo(fromClient.getData(), Request.class);
            logger.info("Сообщение получено от клиента #{}#{}", address, request.id());
            return request;
        }catch (SocketTimeoutException t){
            return null;
        }catch (IOException | ClassNotFoundException e){
            logger.warn(e);
            return null;
        }
    }

    @Override
    public void run(){
        run(false,"");
    }

    @Override
    public void run(boolean isScript, String path){
        BufferedReader br;
        Path path1 = Path.of(path);
        if (isScript){
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
        }

        while (true){

            try {
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

                    invoker.defineCommand(input, isScript).execute();
                    if (!isScript) {
                        System.out.print("$user: ");
                    }
                    continue;
                }


                Request request = receiveMessage();
                if(request != null) {
                    logger.info(request.command());
                    //todo можно добавить проверку на корректный реквест
                    invoker.defineCommand(request.command().toString(), request.isScript()).execute();

                }

            } catch (NoSuchCommandException | RecursionLimitReached | EmptyContainerException | XmlUtilException | IOException e) {
                logger.warn(e);
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
}
