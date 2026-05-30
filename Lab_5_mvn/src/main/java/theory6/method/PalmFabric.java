package theory6.method;

public class PalmFabric extends TreeFabricMethod{

    @Override
    protected Tree create() {
        PalmTree tree = new PalmTree();
        tree.setLeaves("широкие листья");
        return tree;
    }

}
