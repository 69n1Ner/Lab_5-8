package commands;

import exceptions.EmptyContainerException;
import exceptions.InvalidInput;
import io.Validator;
import main.Invoker;
import net.UdpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import organization.Organization;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class SumOfEmployeesCountCommand extends Command implements Serializable {
    private static final Logger logger = LogManager.getLogger(SumOfEmployeesCountCommand.class);

    public SumOfEmployeesCountCommand(String name, Invoker invoker){
        super(name,invoker,ArgumentType.NO_ARGUMENTS);
    }

    @Override
    public void execute() {
        String r = "непредвиденная";

        try{
            Validator.isValidArgument(this);

            if (getInvokerFather().getRunner() instanceof UdpClient){
                createRequest();
                return;
            }

            List<Organization> container = getInvokerFather().getContainer().getAll();
            AtomicLong employees = new AtomicLong(0);
            if (!container.isEmpty()) {
                container.forEach(o -> employees.addAndGet(o.getEmployeesCount()));
                String t = "Количество сотрудников во всех организациях: "+ employees;
                logger.info(t);
                r= t;
            } else {
                EmptyContainerException ec = new EmptyContainerException();
                logger.warn(ec);
                r= ec.getMessage();
            }

        } catch (InvalidInput i){
            logger.warn(i);
            r= i.getMessage();
        }finally {
            if (isRequest() && !(getInvokerFather().getRunner() instanceof UdpClient)){
                createResponse(r);
            }
        }

    }

    @Override
    public String describe() {
        return "sum_of_employees_count : вывести сумму значений поля employeesCount для всех элементов коллекции";
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
