package theory6.strategy;

public class Main {
    public static void main(String[] args) {
        AirplaneFabric fabric = new AirplaneFabric(100,new Panam());
        System.out.println(fabric.getCost());
        AirplaneFabric fabric1 = new AirplaneFabric(100,new  AmericanAirlines());
        System.out.println(fabric1.getCost());
    }
}
