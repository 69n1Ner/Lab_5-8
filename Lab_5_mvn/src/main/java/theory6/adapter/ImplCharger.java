package theory6.adapter;

public class ImplCharger implements Charger{
    @Override
    public void charge() {
        System.out.println("Заряжается через новую зарядку");
    }
}
