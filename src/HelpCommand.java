public class HelpCommand implements ICommand{
    private String name; // непонятно зачем нужно
    private int id; // для отслеживания последовательности команд


    public HelpCommand(String name){
        this.name = name;
        
    }

    @Override
    public void execute(){
        System.out.println("Help done");
    }

    @Override
    public String getName(){
        return name;
    }
}
