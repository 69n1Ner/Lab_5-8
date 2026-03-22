package Commands;

import Exceptions.InvalidInput;
import IO.InputManager;
import IO.XmlUtil;
import MainProg.*;
import OrganizationObject.Address;
import OrganizationObject.Organization;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class FilterGreaterThanPostalAddress extends Command{

    public FilterGreaterThanPostalAddress(String name, Invoker invoker) {
        this.setName(name);
        setInvokerFather(invoker);
    }

    @Override
    public boolean isValidForScript(InputManager inputManager) throws InvalidInput {
        if (inputManager.getMainArgument() == null){
            if (inputManager.isScript()){
                if (inputManager.getXmlArgument() != null) {
                    return true;
                } throw new InvalidInput("Команда "+ this.getName() +" должна иметь XML строку при исполнении скрипта");
            } return false;
        } throw new InvalidInput("Команда "+ this.getName() +" не должна иметь параметров");
    }

    @Override
    public void execute() throws IOException{
        Invoker invokerFather = getInvokerFather();
        Container container = invokerFather.getContainer();
        InputManager inputManager = invokerFather.getInputManager();

        try {
            if (!container.getAll().isEmpty()) {
                Address address;
                if (!isValidForScript(inputManager)) {

                    address = inputManager.inputAddress();
                }else {
                    address = XmlUtil.readAddressFromString(inputManager.getXmlArgument());
                }
                address = container.generateAddress(address);

                Organization organization = new Organization();
                organization.setPostalAddress(address);

                Iterator<Organization> iterator = container.getAll().iterator();
                int showedCount = 0;

                boolean capFlag = true;
                while (iterator.hasNext()) {
                    Organization org = iterator.next();
                    Address addr = org.getPostalAddress();
                    if (addr.compareTo(organization.getPostalAddress()) > 0) {
                        if (capFlag){
                            System.out.println("____-____-____-____-____-____-____-____-____-____-____-____-____-____-____-____-____");
                            capFlag = false;
                        }
                        System.out.println(org);
                        System.out.println("____-____-____-____-____-____-____-____-____-____-____-____-____-____-____-____-____");
                        showedCount++;
                    }
                }


                if (showedCount == 0) {
                    throw new NoSuchElementException("Нет организаций, с большим адресом");
                }
            }else throw new NullPointerException("Список пуст, не с чем сравнивать");
        }catch (InvalidInput e){
            System.err.println(e.getMessage());
        }
    }

    @Override
    public String describe() {
        return "filter_greater_than_postal_address {postalAddress} : вывести элементы, значение поля postalAddress которых больше заданного. Сравнение идет по почтовому индексу";
    }
}
