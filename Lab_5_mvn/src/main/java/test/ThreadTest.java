package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.*;

public class ThreadTest {
    public static void main(String[] args) {
        BlockingDeque<String> deque = new LinkedBlockingDeque<>();
        Data data = new Data();
        Data data1 = new Data();


        ExecutorService read = Executors.newFixedThreadPool(2);
        ExecutorService send = Executors.newFixedThreadPool(2);
        read.submit(new Receiver(data));
        read.submit(new Receiver1(deque));
        send.submit(new Sender(data));
        send.submit(new Sender1(deque));

    }

    private static class Data {
        String packet;
        boolean transfer = true;

        public Data(){};

        public synchronized void send(String string){
            while (!transfer){
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("inter");
                }
            }
            transfer = false;

            this.packet = string;
            notifyAll();
        }

        public synchronized String receive(){
            while (transfer){
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("inter1");
                }
            }
            transfer = true;

            String retPacket = packet;
            notifyAll();
            return retPacket;
        }
    }

    private static class Sender implements Runnable{
        private Data data;

        public Sender(Data data){
            this.data = data;
        }

        @Override
        public void run() {
            String[] lst = {
                    "S1",
                    "S2",
                    "S3",
                    "end"
            };
            for (String packet : lst){
                data.send(packet);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Thread Interrupted");
                }
            }
        }
    }

    private static class Receiver implements Runnable{
        private Data data;

        public Receiver(Data data){
            this.data = data;
        }

        @Override
        public void run() {
            String msg = "";
            while (!msg.equals("end")){
                msg = data.receive();
                System.out.println(msg+" "+Thread.currentThread().getName());

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Thread Interrupted");
                }
            }
        }
    }
    private static class Sender1 implements Runnable{
        private BlockingDeque<String> deque;

        public Sender1(BlockingDeque<String> deque){
            this.deque = deque;
        }

        @Override
        public void run() {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while (true){
                try {
                    if (br.ready()){
                        String string = br.readLine();
                        deque.put(string);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Thread Interrupted");
                }
            }
        }
    }

    private static class Receiver1 implements Runnable{
        private BlockingDeque<String> deque;

        public Receiver1(BlockingDeque<String> deque) {
            this.deque = deque;
        }

        @Override
        public void run() {
            while (true){
                try {
                    String  response = deque.poll(15,TimeUnit.MILLISECONDS);
                    if (response == null) continue;
                    System.out.println(response);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Thread Interrupted");
                }
            }
        }
    }

}
