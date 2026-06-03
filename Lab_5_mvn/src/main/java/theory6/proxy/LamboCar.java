package theory6.proxy;

public class LamboCar extends Car {
    public LamboCar(String name){
        super(name);
    }

    @Override
    public void ride() {
        System.out.println(getName() + " едет");
    }
}
