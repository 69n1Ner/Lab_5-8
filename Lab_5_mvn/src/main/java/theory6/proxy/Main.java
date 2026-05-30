package theory6.proxy;

public class Main {
    public static void main(String[] args) {
        Ridable proxy = new CarProxy(LamboCar.class,"#1");
        proxy.ride();
    }
}
