package net;

import io.ByteUtil;
import io.ContainerWrapper;
import organization.Address;
import organization.Coordinates;
import organization.Organization;
import organization.OrganizationType;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.UUID;

public class UdpServer implements Connectable,Messagable{
    private static final  String IP_ADDRESS = "localhost";
    private static final  int PORT = 9898;
    private static final int ARRAY_SIZE = 1500;
    private DatagramSocket SOCKET;



    public static void main(String[] args) throws IOException, ClassNotFoundException {
        UdpServer server = new UdpServer();
        server.connect();

        System.out.println(server.receiveMessage());



        server.SOCKET.close();
        System.out.println("Закрыто");
    }

    @Override
    public void connect() throws IOException {
        SOCKET = new DatagramSocket(PORT);
        System.out.println("Сервер запущен и готов к работе");
    }

    @Override
    public void reconnect() {

    }

    @Override
    public void sendMessage(Request request) throws IOException{
        byte[] buf = ByteUtil.toByteArray(request,ARRAY_SIZE);
        InetSocketAddress address = new InetSocketAddress(IP_ADDRESS,PORT);
        DatagramPacket toClient = new DatagramPacket(buf,ARRAY_SIZE,address);
        SOCKET.send(toClient);
        System.out.println("Сообщение отправлено клиенту #"+address+"#"+request.clientId());
    }

    @Override
    public Request receiveMessage() throws IOException, ClassNotFoundException {
        byte[] buf = new byte[ARRAY_SIZE];
        DatagramPacket fromClient = new DatagramPacket(buf,ARRAY_SIZE);
        SOCKET.receive(fromClient);
        SocketAddress address =  fromClient.getSocketAddress();
        Request request = ByteUtil.fromBytesTo(fromClient.getData(),Request.class);
        System.out.println("Сообщение получено от клиента #"+address + "#"+request.clientId());
        return request;
    }
}
