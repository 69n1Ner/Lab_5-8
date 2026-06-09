package security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD2Hash {

    private MD2Hash() {
    }

    public static String hashWithMD2(String data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD2");
        byte[] hashBytes = digest.digest(data.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
