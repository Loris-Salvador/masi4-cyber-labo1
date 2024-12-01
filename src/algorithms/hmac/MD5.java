package algorithms.hmac;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MD5 implements HMAC{
    @Override
    public String calculate(String message, byte[] key) throws InvalidKeyException, NoSuchAlgorithmException {

        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "HmacMD5");
        Mac mac = Mac.getInstance("HmacMD5");
        mac.init(secretKeySpec);
        byte[] hmacBytes = mac.doFinal(message.getBytes());
        StringBuilder hmac = new StringBuilder();

        for (byte b : hmacBytes) {
            hmac.append(String.format("%02x", b));
        }

        return hmac.toString();
    }
}
