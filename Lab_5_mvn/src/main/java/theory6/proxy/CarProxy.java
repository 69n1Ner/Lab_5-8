package theory6.proxy;

import java.lang.reflect.InvocationTargetException;

public class CarProxy implements Ridable{
    private final Car car;

    public <T> CarProxy(Class<T> carClass, String name){
        try {
            try {
                this.car = (Car) carClass.getDeclaredConstructor(String.class).newInstance(name);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void ride() {
        System.out.println(this.getClass().getSimpleName() + " делает что то важное");

        car.ride();
    }
}
