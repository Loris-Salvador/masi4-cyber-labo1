package algorithms.crypto;

import javax.crypto.SecretKey;

public interface CryptoAlgorithm {
    String encrypt(String message, SecretKey secretKey);

    String decrypt(String message, SecretKey secretKey);
}
