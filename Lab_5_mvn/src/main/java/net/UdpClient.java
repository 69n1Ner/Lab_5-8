package net;

import commands.Command;
import commands.GetLoggerable;
import commands.HelpCommand;
import exceptions.*;
import io.ByteUtil;
import io.Validator;
import main.Invoker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.file.Path;
import java.util.NoSuchElementException;

public class UdpClient implements GetLoggerable,Runner {
    private static final String IP_ADDRESS = "localhost";
    private static final int ARRAY_SIZE = 1500;
    private final int port;
    private DatagramChannel CHANNEL;
    private static final Logger logger = LogManager.getLogger(UdpClient.class);
    private final Invoker invoker;

    private UdpClient(Invoker invoker, int port){
        this.invoker = invoker;
        this.port = port;
    }

    public static void main(String[] args) throws IOException {
        Invoker invoker = new Invoker(null);
        UdpClient client = new UdpClient(invoker,9898);
        client.run();

        client.CHANNEL.close();
        System.out.println("Закрыто");
    }
    public void connect() {
        try {
            CHANNEL = DatagramChannel.open();
            CHANNEL.configureBlocking(false);
            //todo добавть селектор
            InetSocketAddress socketAddress = new InetSocketAddress(IP_ADDRESS,port);
            CHANNEL.connect(socketAddress);
            System.out.println("Клиент запущен и готов отправлять данные");
        } catch (IOException e) {
            logger.error("Сервер не смог подключиться к сети по порту {}",port,e);
            System.out.println("Сервер не подключен к сети");
        }
    }

    @Override
    public void sendMessage(Request request) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(ByteUtil.toByteArray(request, ARRAY_SIZE));
            InetSocketAddress address = new InetSocketAddress(IP_ADDRESS, port);
            CHANNEL.send(buffer, address);
            logger.info("Сообщение направлено на сервер #{}", address);
        }catch (IOException e){
            logger.warn(e);
        }
    }

    @Override
    public  Request receiveMessage() {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(ARRAY_SIZE);
            SocketAddress address = CHANNEL.receive(buffer);
            logger.info("Сообщение получено от сервера #{}", address);
            return ByteUtil.fromBytesTo(buffer.array(), Request.class);
        }catch (IOException | ClassNotFoundException e){
            logger.warn(e);
        }
        return null;
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
                System.out.println(e.getMessage());
                return;
            }
        } else {
            connect();
            br = new BufferedReader(new InputStreamReader(System.in));
        }

        while (true) {
            if (!isScript) {
                System.out.print("$user: ");
            }

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
                        //showing what command was
                        logger.info(input);
                    }

                    //todo мб переопределить логику
                    if (CHANNEL == null) {
                        connect();
                    }

                    invoker.defineCommand(input,isScript).execute();

                    Request request1 = receiveMessage();
                    logger.info(request1.feedback());

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

    public int getPort() {
        return port;
    }

    public Invoker getInvoker() {
        return invoker;
    }
}
