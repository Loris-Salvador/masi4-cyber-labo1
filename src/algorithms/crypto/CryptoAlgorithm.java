package algorithms.crypto;

public interface CryptoAlgorithm {
    String encrypt(String message, byte[] key);

    String decrypt(String message, byte[] key);
}
