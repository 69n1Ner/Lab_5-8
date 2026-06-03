package theory6.strategy;

public class AirplaneFabric {
    private double discount;
    private Airplane airplane;

    public AirplaneFabric(double discount, Airplane airplane){
        this.airplane = airplane;
        this.discount = discount;
    }

    public double getCost(){
        return airplane.cost(discount);
    }
}
