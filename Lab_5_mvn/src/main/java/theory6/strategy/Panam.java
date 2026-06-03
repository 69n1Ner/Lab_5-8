package theory6.strategy;

public class Panam implements Airplane{

    @Override
    public double cost(double discount) {
        return discount * 0.9;
    }
}
