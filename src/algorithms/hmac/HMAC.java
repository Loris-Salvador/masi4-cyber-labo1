package algorithms.hmac;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface HMAC {
    String calculate(String message, byte[] key) throws InvalidKeyException, NoSuchAlgorithmException;
}
