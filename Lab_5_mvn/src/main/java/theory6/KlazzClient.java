package theory6;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;

public class KlazzClient {
    public static void main(String[] args) {
        DatagramChannel channel;


        try {
            channel = DatagramChannel.open();
            SocketAddress socketAddress = new InetSocketAddress("localhost",9090);
            channel.connect(socketAddress);
            byte[] num = new byte[]{2};
            ByteBuffer byteBuffer = ByteBuffer.wrap(num,0,1);
            System.out.println("sended: "+ Arrays.toString(num));
            channel.send(byteBuffer,socketAddress);
            while (true){

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}
