package net;

import io.ByteUtil;
import io.ContainerWrapper;
import organization.Address;
import organization.Coordinates;
import organization.Organization;
import organization.OrganizationType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.UUID;

public class UdpClient implements Messagable,Connectable{
    private static final String IP_ADDRESS = "localhost";
    private static final int PORT = 9898;
    private static final int ARRAY_SIZE = 1500;
    private  DatagramChannel CHANNEL;

    public UdpClient() throws IOException {
    }

    public static void main(String[] args) throws IOException {
        UdpClient client = new UdpClient();
        client.connect();


        Organization organization = new Organization("name",
                123,
                new Coordinates(),
                12432L,
                new Address(),
                OrganizationType.OPEN_JOINT_STOCK_COMPANY);


        Request request = Request.build(UUID.randomUUID())
                .setRequestType(RequestType.NON_ARGUMENT_COMMAND)
                .setCommandName("add")
                .setOrganizations(new ContainerWrapper(organization,organization));


        client.sendMessage(request);



        client.CHANNEL.close();
        System.out.println("Закрыто");
    }

    @Override
    public void connect() throws IOException {
        CHANNEL = DatagramChannel.open();
        CHANNEL.configureBlocking(false);
        InetSocketAddress socketAddress = new InetSocketAddress(IP_ADDRESS,PORT);
        CHANNEL.connect(socketAddress);
        System.out.println("Клиент запущен и готов отправлять данные");
    }

    @Override
    public void reconnect() {

    }

    @Override
    public void sendMessage(Request request) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(ByteUtil.toByteArray(request,ARRAY_SIZE));
        InetSocketAddress address = new InetSocketAddress(IP_ADDRESS,PORT);
        CHANNEL.send(buffer,address);
        System.out.println("Сообщение направлено на сервер #"+address);
    }

    @Override
    public Request receiveMessage() throws IOException, ClassNotFoundException {
        ByteBuffer buffer = ByteBuffer.allocate(ARRAY_SIZE);
        SocketAddress address = CHANNEL.receive(buffer);
        System.out.println("Сообщение получено от сервера #"+address);
        return ByteUtil.fromBytesTo(buffer.array(), Request.class);
    }
}
