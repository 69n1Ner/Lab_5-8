package theory6.adapter;

public class ImplOldCharger implements OldCharger{
    @Override
    public void connect() {
        System.out.println("Заряжается через старый зарядник");
    }
}
