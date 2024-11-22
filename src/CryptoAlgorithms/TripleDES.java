package CryptoAlgorithms;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

public class TripleDES implements CryptoAlgorithm {

    @Override
    public String encrypt(String message, SecretKey secretKey) {
        try {
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(message.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
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
        } catch (Exception e) {
            throw new RuntimeException("Error while decrypting", e);
        }
    }



    public static void main(String[] args) {
        byte[] SECRET_KEY_BYTES = Arrays.copyOf("bot".getBytes(StandardCharsets.UTF_8), 24);
        SecretKey SECRET_KEY = new SecretKeySpec(SECRET_KEY_BYTES, "DESede");

        TripleDES tripleDES = new TripleDES();
        String message = "Hello World DES!";
        System.out.println("Original message: " + message);
        String encryptedMessage = tripleDES.encrypt(message, SECRET_KEY);
        System.out.println("Encrypted message: " + encryptedMessage);
        String decryptedMessage = tripleDES.decrypt(encryptedMessage, SECRET_KEY);
        System.out.println("Decrypted message: " + decryptedMessage);
    }


}
