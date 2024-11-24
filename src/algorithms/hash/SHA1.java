package algorithms.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA1 implements Hash {

    public String hash(byte[] message)
    {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");

            byte[] hashBytes = sha1.digest(message);

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors du hachage avec SHA-1", e);
        }
    }
}
