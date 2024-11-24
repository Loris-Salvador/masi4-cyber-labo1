package algorithms.crypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class TripleDES implements CryptoAlgorithm {

    @Override
    public String encrypt(String message, byte[] key) {

        try {
            SecretKey secretKey = new SecretKeySpec(key, "DESede");

            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(message.getBytes());

            return Base64.getEncoder().encodeToString(encryptedBytes);
        }
        catch (Exception e) {
            throw new RuntimeException("Error while encrypting", e);
        }
    }

    @Override
    public String decrypt(String message, byte[] key) {

        SecretKey secretKey = new SecretKeySpec(key, "DESede");

        try {
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(message));

            return new String(decryptedBytes);
        }
        catch (Exception e) {
            throw new RuntimeException("Error while decrypting", e);
        }
    }
}
