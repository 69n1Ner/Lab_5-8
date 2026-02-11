import java.util.ArrayList;
import java.util.Map;

public interface Command {
    @Override
    void execute(){

    }

}
class HelpCommand implements Command{
    private String name;
    private Map[] commands;

    public HelpCommand(String name, Map[] commands){
        this.name = name;
    }

    @Override
    public void execute(){
        System.out.println("executing");
    }

    @Override
    public String toString(){
        return name;
    }
}


class StringToCollectManager{
    private String input;

    StringToCollectManager(String input){
        this.input = input;
    }

    public Organization parse(){
        Organization org = new Organization(this.input);
        return org;
    }
}

class CollectionManager {
    private ArrayList<Organization> studyGroups;

    public void addItem(Organization studyGroup){
        this.studyGroups = studyGroups;
    }
    //удаление
    //поиск
}

class AddCommand implements Command {
    Organization input;
    CollectionManager collectionManager;

    AddCommand(CollectionManager collectionManager,Organization input){
        this.input = input;
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(){
        collectionManager.addItem(input);
    }
}
