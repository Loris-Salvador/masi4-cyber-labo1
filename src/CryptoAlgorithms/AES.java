package CryptoAlgorithms;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;



public class AES implements CryptoAlgorithm {

    @Override
    public String encrypt(String message, SecretKey secretKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivParams = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParams);
            byte[] encryptedBytes = cipher.doFinal(message.getBytes());
            byte[] encryptedMessageWithIv = new byte[iv.length + encryptedBytes.length];
            System.arraycopy(iv, 0, encryptedMessageWithIv, 0, iv.length);
            System.arraycopy(encryptedBytes, 0, encryptedMessageWithIv, iv.length, encryptedBytes.length);
            return Base64.getEncoder().encodeToString(encryptedMessageWithIv);
        } catch (Exception e) {
            throw new RuntimeException("Error while encrypting", e);
        }
    }

    @Override
    public String decrypt(String message, SecretKey secretKey) {
        try {
            byte[] encryptedMessageWithIv = Base64.getDecoder().decode(message);
            byte[] iv = Arrays.copyOfRange(encryptedMessageWithIv, 0, 16);
            byte[] encryptedBytes = Arrays.copyOfRange(encryptedMessageWithIv, 16, encryptedMessageWithIv.length);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivParams = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParams);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error while decrypting", e);
        }
    }

    public static void main(String[] args) {
        byte[] SECRET_KEY_BYTES = Arrays.copyOf("bot".getBytes(StandardCharsets.UTF_8), 16);
        SecretKey SECRET_KEY = new SecretKeySpec(SECRET_KEY_BYTES, "AES");

        AES aes = new AES();
        String message = "Hello World AES!";
        System.out.println("Original message: " + message);
        String encryptedMessage = aes.encrypt(message, SECRET_KEY);
        System.out.println("Encrypted message: " + encryptedMessage);
        String decryptedMessage = aes.decrypt(encryptedMessage, SECRET_KEY);
        System.out.println("Decrypted message: " + decryptedMessage);
    }
}
