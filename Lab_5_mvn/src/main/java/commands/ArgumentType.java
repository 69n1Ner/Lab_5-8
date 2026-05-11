package commands;

import java.io.Serializable;

public enum ArgumentType implements Serializable {
    NO_ARGUMENT,
    NO_ARGUMENTS,
    FILE,
    ID,
    ID_ONLY
}