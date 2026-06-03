package theory6.adapter;

public class Adapter implements Charger{
    private OldCharger oldCharger;

    public Adapter(OldCharger oldCharger){
        this.oldCharger = oldCharger;
    }

    @Override
    public void charge() {
        oldCharger.connect();
    }
}
