package theory6.method;

public class PalmTree implements Tree{
    private String leaves;

    @Override
    public void type() {
        System.out.println(leaves);
    }
    @Override
    public String getInfo() {
        return leaves;
    }

    public void setLeaves(String leaves) {
        this.leaves = leaves;
    }
}
