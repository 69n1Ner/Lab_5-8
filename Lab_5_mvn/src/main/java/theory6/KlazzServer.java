package theory6;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;

public class KlazzServer {
    public static void main(String[] args) {
        try {
            DatagramChannel channel = DatagramChannel.open();
            channel.configureBlocking(true);
            SocketAddress sa = new InetSocketAddress(9090);
            channel.bind(sa);

            while (true) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(1);
                channel.receive(byteBuffer);
                byteBuffer.flip();
                byte[] data = new byte[1];
                byteBuffer.get(data);
                System.out.println(Arrays.toString(data));
                while (true){

                }
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
