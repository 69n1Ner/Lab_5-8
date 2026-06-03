package theory6.method;

public class Main {
    public static void main(String[] args) {
        TreeFabricMethod fabric = new PalmFabric();
        System.out.println(fabric.create().getInfo());

        fabric = new PineFabric();
        System.out.println(fabric.create().getInfo());
    }
}
