package theory6;

public class Main {
    public static void main(String[] args) {
        TableFactory tableFactory = new TableFactory();
        Table t3 = tableFactory.createTable(Color.BLUE);
        Table t2 = tableFactory.createTable(Color.RED);
        Table table = tableFactory.createTable(Color.RED);
        System.out.println(""+table.color()+t2.color()+t3.color());
        System.out.println(tableFactory.getMap());

    }
}
