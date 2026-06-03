package theory6.strategy;

public class AmericanAirlines implements Airplane{
    @Override
    public double cost(double discount) {
        return discount * 0.5;
    }
}
