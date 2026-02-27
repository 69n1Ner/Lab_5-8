package lab_5to8;

public abstract class Command {
    private Invoker invokerFather;
    private String name; // непонятно зачем нужно
    private int id; // для отслеживания последовательности команд

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public Invoker getInvokerFather() {
        return invokerFather;
    }

    public void setInvokerFather(Invoker invokerFather) {
        this.invokerFather = invokerFather;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void execute() {
    }

    public boolean isValid(InputManager inputManager) throws InvalidInput{
        if (inputManager.getMainArgument() != null || inputManager.getXmlArgument() != null){
            throw new InvalidInput("Команда "+ this.getName() +" не должна иметь параметров");
        }
        return true;
    }

    public boolean isXmlValid(InputManager inputManager){
        if ((inputManager.getXmlArgument() == null || !inputManager.getXmlArgument().equals("ERR"))) {
            return true;
        } else {
            throw new InvalidInput("Часть xml задана неверно");
        }
    }


//    void unexecute();
}
