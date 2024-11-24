package algorithms.crypto;

public interface CryptoAlgorithm {
    String encrypt(String message, String keyString);

    String decrypt(String message, String keyString);
}
