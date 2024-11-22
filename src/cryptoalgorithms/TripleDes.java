package cryptoalgorithms;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.util.Base64;

public class TripleDes implements CryptoAlgorithm {

    @Override
    public String encrypt(String message, SecretKey secretKey) {

        try {
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
    public String decrypt(String message, SecretKey secretKey) {

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
