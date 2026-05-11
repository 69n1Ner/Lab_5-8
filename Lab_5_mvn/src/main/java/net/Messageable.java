package net;

import commands.GetLoggerable;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Messageable extends GetLoggerable {
    Request receiveMessage() ;
    void sendMessage(Request request) ;
    default boolean sendAndWait(Request request, UUID uuid){
            long start = System.currentTimeMillis();
            long timeout = 300;
            sendMessage(request);
            while (System.currentTimeMillis() - start < timeout) {
                try {
                    Request response = receiveMessage();
                    if (response != null && uuid.equals(response.id()) && response.requestType() == RequestType.OK) {
                        return true;
                    }else {
                        Thread.sleep(30);
                        sendMessage(request);
                    }


                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        Pattern pattern = Pattern.compile("\\.(\\w+)");
        Matcher matcher = pattern.matcher(getLogger().getName());
        getLogger().warn("Не получен ответ от {}", matcher.find());
        return false;
    };
}
