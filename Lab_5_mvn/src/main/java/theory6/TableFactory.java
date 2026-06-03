package theory6;

import java.util.HashMap;

public class TableFactory {
    private static final HashMap<Color,Table> map = new HashMap<>();

    public HashMap<Color,Table> getMap(){
        return map;
    }

    public Table createTable(Color color){
        return map.computeIfAbsent(color, RoundTable::new);
    }
}
