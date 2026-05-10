package commands;

import exceptions.InvalidInput;
import io.InputManager;
import io.Validator;
import io.XmlUtil;
import main.Invoker;
import net.UdpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import organization.Organization;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class SaveCommand extends Command{
    private static int counter = 0;
    private static final Logger logger = LogManager.getLogger(SaveCommand.class);

    public SaveCommand(String name, Invoker invoker) {
        super(name,invoker,ArgumentType.NO_ARGUMENTS);
    }

    @Override
    public void execute() {
        try {
            Validator.isValidArgument(this);

            if (getInvokerFather().getRunner() instanceof UdpClient){
                createRequest();
            }

            Path dir = Paths.get(System.getProperty("user.dir"));

            try (Stream<Path> stream = Files.list(dir)) {
                List<String> lst =  stream
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .filter(name -> name.equals("collection"+counter+".xml"))
                        .sorted()
                        .toList();

                if (!lst.isEmpty()) {
                    String biggestName = lst.get(lst.size() - 1);
                    counter = Integer.parseInt(biggestName.replaceAll("\\D+", ""));
                }

                String response = XmlUtil.writeListToFile((ArrayList<Organization>) getInvokerFather().getContainer().getAll(), "collection" + getCounter() + ".xml");
                counter++;
                createResponse(response);
            } catch (IOException e) {
                logger.warn(e);
            }




            counter+=1;
            String t = "Коллекция сохранена в collection" + getCounter() + ".xml";
            logger.info(t);

        }catch (InvalidInput i){
            logger.warn(i);
        }
    }

    @Override
    public String describe() {
        return "save : сохранить коллекцию в файл";
    }

    public static int getCounter(){
        return counter;
    }


    @Override
    public Logger getLogger() {
        return logger;
    }
}
