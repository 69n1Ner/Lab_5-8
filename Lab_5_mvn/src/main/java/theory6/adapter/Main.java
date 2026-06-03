package theory6.adapter;

public class Main {
    public static void main(String[] args) {
        OldCharger oldCharger = new ImplOldCharger();
        Charger charger = new Adapter(oldCharger);
        charger.charge();
        Charger charger1 = new ImplCharger();
        charger1.charge();
    }
}
