package commands;

import exceptions.EmptyContainerException;
import exceptions.InvalidInput;
import io.InputManager;
import io.XmlUtil;
import main.*;
import organization.Address;
import organization.Organization;

import java.io.IOException;
import java.util.NoSuchElementException;

public class FilterGreaterThanPostalAddress extends Command{

    public FilterGreaterThanPostalAddress(String name, Invoker invoker) {
        this.setName(name);
        setInvokerFather(invoker);
    }

    @Override
    public boolean isNotValidForScript(InputManager inputManager) throws InvalidInput {
        if (inputManager.getMainArgument() == null){
            if (inputManager.isScript()){
                if (inputManager.getXmlArgument() != null) {
                    return false;
                } throw new InvalidInput("Команда "+ this.getName() +" должна иметь XML строку при исполнении скрипта");
            } return true;
        } throw new InvalidInput("Команда "+ this.getName() +" не должна иметь параметров");
    }

    @Override
    public void execute() throws IOException{
        Invoker invokerFather = getInvokerFather();
        Container<Organization> container = invokerFather.getContainer();
        InputManager inputManager = invokerFather.getInputManager();

        try {
            if (!container.getAll().isEmpty()) {
                Address address;
                if (isNotValidForScript(inputManager)) {

                    address = inputManager.inputAddress();
                }else {
                    address = XmlUtil.readAddressFromString(inputManager.getXmlArgument());
                }
//                address = container.generateAddress(address);

                Organization organization = new Organization();
                organization.setPostalAddress(address);
                Address addr1 = organization.getPostalAddress();

                var iterator = container.getAll().iterator();
                int showedCount = 0;

                boolean hatFlag = true;
                boolean printThis = false;
                boolean printAll = addr1.getZipCode() == null;

                while (iterator.hasNext()) {
                    Organization org = iterator.next();
                    Address addr = org.getPostalAddress();
                    if (addr != null && addr.getZipCode() != null && addr1.getZipCode() != null && addr.compareTo(addr1) > 0) {
                        printThis = true;
                    }
                    if (printThis || printAll) {
                        printThis = false;
                        if (hatFlag) {
                            System.out.println("____-____-____-____-____-____-____-____-____-____-____-____-____-____-____-____-____");
                            hatFlag = false;
                        }
                        System.out.println(org);
                        System.out.println("____-____-____-____-____-____-____-____-____-____-____-____-____-____-____-____-____");
                        showedCount++;
                    }

                }


                if (showedCount == 0) throw new NoSuchElementException("Нет организаций, с большим адресом");
            }else throw new EmptyContainerException("Список пуст, не с чем сравнивать");
        }catch (InvalidInput e){
            System.err.println("!! "+e.getMessage()+" !!");
        }
    }

    @Override
    public String describe() {
        return "filter_greater_than_postal_address {postalAddress} : вывести элементы, значение поля postalAddress которых больше заданного. Сравнение идет по почтовому индексу";
    }
}
