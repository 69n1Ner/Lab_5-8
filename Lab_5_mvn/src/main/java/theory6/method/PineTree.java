package theory6.method;

public class PineTree implements Tree{
    private String needles;

    @Override
    public void type() {
        System.out.println("ель");
    }

    @Override
    public String getInfo() {
        return needles;
    }


    public void setNeedles(String needles) {
        this.needles = needles;
    }
}
