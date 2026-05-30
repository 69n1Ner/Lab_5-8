package theory6.proxy;

public abstract class Car implements Ridable{
    private final String name;

    public Car(String name){
        this.name = name;
    }


    public String getName() {
        return name;
    }
}
