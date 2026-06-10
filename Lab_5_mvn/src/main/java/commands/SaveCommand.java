package commands;

import exceptions.InvalidInput;
import io.Validator;
import main.Invoker;
import net.Request;
import net.UdpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import security.User;
import thread.ThreadClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class SaveCommand extends Command{
    private int counter = 0;
    private static final Logger logger = LogManager.getLogger(SaveCommand.class);

    public SaveCommand(String name, Invoker invoker) {
        super(name,invoker,ArgumentType.NO_ARGUMENTS);
    }

    @Override
    public Request execute(User user) {
        try {
            Validator.isValidArgument(this);

            if (getInvokerFather().getRunner() instanceof UdpClient || getInvokerFather().getRunner() instanceof ThreadClient){

                return createRequest(this);
            }

            Path dir = Paths.get(System.getProperty("user.dir"));

            try (Stream<Path> stream = Files.list(dir)) {
                List<Integer> lst = stream
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .filter(name -> name.matches("collection\\d*\\.xml"))
                        .map(s -> Integer.parseInt(s.replaceAll("\\D+", "")))
                        .sorted()
                        .toList();

                logger.debug("Список#{}",lst);
                if (!lst.isEmpty()) {
                    int i = 0;
                    while (lst.contains(i)){
                        i++;
                    }
                    counter = i;
                }

            } catch (IOException e) {
                logger.warn(e);
            }

            String t = "Коллекция сохранена в collection" + counter + ".xml";
                logger.info(t);
        }catch (InvalidInput i){
            logger.warn(i);
        }
//        String response = XmlUtil.writeListToFile((ArrayList<Organization>) getInvokerFather().getContainer().getAll(), "collection" + counter + ".xml");
//        if (isRequest() && !(getInvokerFather().getRunner() instanceof UdpClient)) {
//             return createRequest(response);
//        }
        return null;
    }

    @Override
    public String describe() {
        return "save : сохранить коллекцию в файл";
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

}
