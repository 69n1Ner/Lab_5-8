package io;

import java.util.List;

public record ObjWithFeedback<T>(
        T object,
        List<String> feedback
) {
    public ObjWithFeedback<T> setObject(T object){
        return new ObjWithFeedback<>(object,feedback);
    }
    public ObjWithFeedback<T> setFeedback(List<String> feedback){
        return new ObjWithFeedback<>(object,feedback);
    }
    public ObjWithFeedback<T> addFeedback(String  feedback1){
        feedback.add(feedback1);
        return new ObjWithFeedback<>(object,feedback);
    }
}
