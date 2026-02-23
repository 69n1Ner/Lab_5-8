public abstract class ICommand {
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
        if (!inputManager.getArguments().isEmpty()){
            throw new InvalidInput("Команда "+ this.getName() +"не должна иметь параметров");
        }
        return true;
    }


//    void unexecute();
}
