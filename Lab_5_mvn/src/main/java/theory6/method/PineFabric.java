package theory6.method;

public class PineFabric extends TreeFabricMethod{


    @Override
    protected Tree create() {
        PineTree tree = new PineTree();
        tree.setNeedles("колючие иголки");

        return tree;
    }
}
