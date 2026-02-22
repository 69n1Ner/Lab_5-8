//import java.util.ArrayList;
//import java.util.Map;
//
//public interface Command1 {
//    @Override
//    void execute1(){
//
//    }
//
//}
//class HelpCommand1 implements Command1{
//    private String name;
//    private Map[] commands;
//
//    public HelpCommand(String name, Map[] commands){
//        this.name = name;
//    }
//
//    @Override
//    public void execute(){
//        System.out.println("executing");
//    }
//
//    @Override
//    public String toString(){
//        return name;
//    }
//}
//
//
//class StringToCollectManager{
//    private String input;
//
//    StringToCollectManager(String input){
//        this.input = input;
//    }
//
//    public Organization parse(){
//        Organization org = new Organization(this.input);
//        return org;
//    }
//}
//
//class CollectionManager {
//    private ArrayList<Organization> studyGroups;
//
//    public void addItem(Organization studyGroup){
//        this.studyGroups.add(studyGroup);
//    }
//    //удаление
//    //поиск
//}
//
//class AddCommand1 implements Command1 {
//    private String input;
//    private CollectionManager collectionManager;
//
//    AddCommand(String input, CollectionManager collectionManager){
//        this.input = input;
//        this.collectionManager = collectionManager;
//    }
//
//    //использование парсера
//
//    @Override
//    public void execute(){
//        collectionManager.addItem(input);
//    } // вот поэтому должно парсится
//}
