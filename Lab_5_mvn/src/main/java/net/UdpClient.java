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

public class UdpClient extends Runner {
    private DatagramChannel CHANNEL;

    public UdpClient(Invoker invoker, int port) {
        super(port, invoker);
        super.invoker.setRunner(this);
        logger = LogManager.getLogger(UdpClient.class);
    }

    public static void main(String[] args) throws IOException {
        Invoker invoker = new Invoker(null);
        UdpClient client = new UdpClient(invoker, 9898);

        client.applyParams();

        client.run();
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
            logger.error("Клиент не смог подключиться к сети по порту {}", port, e);
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
            if (request.requestType() != RequestType.PING) {
                logger.info("Сообщение получено от сервера #{}", address);
                logger.debug(request);
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
                logger.warn(e.getMessage());
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
//            logger.debug("cycle started");
            try {
                ping(runnerId);
                Thread.sleep(10);

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

                    //sending
                    Request request = invoker.defineCommand(input, isScript).execute();
                    if (isRunning && CHANNEL != null && request != null) {
                        sendAndWait(request.setRunnerId(runnerId));
                    }
                    if (!isScript && isRunning) {
                        System.out.print("$user: ");
                        System.out.flush();
                    }
                }

                //receiving
                if (isRunning && CHANNEL != null) {
                    Request request1 = receiveMessage();

                    logger.debug("request1={}", request1);
                    logger.debug("request1 != null={}",request1 != null);
                    if (request1 != null) {
                        logger.debug("request1.requestType() != RequestType.PING={}", request1.requestType() != RequestType.PING);
                    }

                    if (request1 != null && request1.requestType() != RequestType.PING) {
                        if (!isScript && isRunning) {
                            System.out.print("$user: ");
                            System.out.flush();
                        }
                        logger.info(request1.feedback());
                    }

                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn(e.getMessage());
            } catch (NoSuchCommandException | RecursionLimitReached | EmptyContainerException | XmlUtilException |
                     IOException e) {
                logger.warn("{}", e.getMessage());

                if (!isScript && isRunning) {
                    System.out.print("$user: ");
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
