package Commands;

import IO.InputManager;
import IO.XmlUtil;
import MainProg.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeMap;

public class FilterGreaterThanPostalAddress extends Command{

    public FilterGreaterThanPostalAddress(String name, Invoker invoker) {
        this.setName(name);
        setInvokerFather(invoker);
    }

    @Override
    public void execute() throws InvalidInput {
        Invoker invokerFather = getInvokerFather();
        Container container = invokerFather.getContainer();
        InputManager inputManager = invokerFather.getInputManager();

        if (isValid(inputManager)) {
            if (!container.getAll().isEmpty()) {
                Address address;
                try {
                    address = inputManager.inputAddress();
                    address = container.generateAddress(address);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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
            } else throw new NullPointerException("Список пуст, не с чем сравнивать");
        }
    }

    @Override
    public String describe() {
        return "filter_greater_than_postal_address";
    }
}
