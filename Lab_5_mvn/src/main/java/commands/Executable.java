package commands;

import net.Request;
import security.User;

public interface Executable {
    Request execute(User user);
}
