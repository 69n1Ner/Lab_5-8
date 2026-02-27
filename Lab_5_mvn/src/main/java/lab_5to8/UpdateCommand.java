package lab_5to8;

public class UpdateCommand extends Command {
    public UpdateCommand(String name, Invoker invoker) {
        this.setName(name);
        setInvokerFather(invoker);
    }

    @Override
    public boolean isValid(InputManager inputManager) throws NumberFormatException {
        try {
            Long tmp = Long.valueOf(inputManager.getMainArgument());
            return isXmlValid(inputManager);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Неверно задан id");
        }

    }

    //todo реализацию работы с входными данными можно вынести в отдельный класс+-
    @Override
    public void execute(){
        System.out.println(getInvokerFather().lastCall().getXmlArgument());
        if (isValid(getInvokerFather().lastCall())){
            //todo где то здесь сделать развилку, где выбирать уже готовый объект от xml
            while (true){

            }
        }


    }
}
