package io;

import java.util.List;

public record ObjWithFeedback<T>(
        T object,
        List<String> feedback
) {
    public ObjWithFeedback<T> setObject(T t){
        return new ObjWithFeedback<>(t,feedback);
    }
}
