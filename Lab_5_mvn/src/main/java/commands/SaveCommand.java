package commands;

import exceptions.InvalidInput;
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
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class SaveCommand extends Command{
    private int counter = 0;
    private static final Logger logger = LogManager.getLogger(SaveCommand.class);
    private boolean silent = false;

    public SaveCommand(String name, Invoker invoker) {
        super(name,invoker,ArgumentType.NO_ARGUMENTS);
    }

    @Override
    public void execute() {
        try {
            Validator.isValidArgument(this);

            if (getInvokerFather().getRunner() instanceof UdpClient){
                createRequest();
                return;
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
            if (!silent){
                logger.info(t);
            }else {
                logger.info("\n{}", t);
            }
        }catch (InvalidInput i){
            logger.warn(i);
        }finally {
            if (isRequest() && !(getInvokerFather().getRunner() instanceof UdpClient)) {
                String response = XmlUtil.writeListToFile((ArrayList<Organization>) getInvokerFather().getContainer().getAll(), "collection" + counter + ".xml");
                createResponse(response);
            }
        }
    }

    @Override
    public String describe() {
        return "save : сохранить коллекцию в файл";
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    public boolean isSilent() {
        return silent;
    }

    public SaveCommand setSilent(boolean silent) {
        this.silent = silent;
        return this;
    }
}
