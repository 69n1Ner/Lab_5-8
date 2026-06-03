package io;

import java.io.*;

public class ByteUtil {
    public static byte[] toByteArray(Serializable request, int arrSize) throws IOException {
        try(ByteArrayOutputStream bout = new ByteArrayOutputStream(arrSize)){
            ObjectOutputStream outputStream = new ObjectOutputStream(bout);
            outputStream.writeObject(request);
            return bout.toByteArray();
        }
    }

    public static <T extends Serializable> T fromBytesTo(byte[] data, Class<T> type) throws IOException, ClassNotFoundException {
        try(ByteArrayInputStream bin = new ByteArrayInputStream(data)) {
            ObjectInputStream inputStream = new ObjectInputStream(bin);
            Object request =  inputStream.readObject();
            return type.cast(request);
        }
    }
}
