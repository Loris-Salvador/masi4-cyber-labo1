package client.features;

import algorithms.crypto.CryptoAlgorithm;

import javax.crypto.KeyAgreement;
import javax.crypto.spec.DHParameterSpec;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.*;
import java.util.Arrays;

public class AESDiffieHellmanClientFeature implements ClientFeature {

    private final String message;

    private final CryptoAlgorithm cryptoAlgorithm;

    public AESDiffieHellmanClientFeature(CryptoAlgorithm cryptoAlgorithm, String message) {
        this.cryptoAlgorithm = cryptoAlgorithm;
        this.message = message;
    }

    @Override
    public void execute(Socket serverSocket) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(serverSocket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(serverSocket.getInputStream())) {

            BigInteger p = (BigInteger) in.readObject();
            BigInteger g = (BigInteger) in.readObject();

            DHParameterSpec dhParamSpec = new DHParameterSpec(p, g);

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
            keyPairGenerator.initialize(dhParamSpec);
            KeyPair clientKeyPair = keyPairGenerator.generateKeyPair();

            KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
            keyAgreement.init(clientKeyPair.getPrivate());

            out.writeObject(clientKeyPair.getPublic());

            PublicKey serverPublicKey = (PublicKey) in.readObject();

            keyAgreement.doPhase(serverPublicKey, true);
            byte[] sharedSecret = keyAgreement.generateSecret();
            byte[] aesKey = Arrays.copyOf(sharedSecret, 16); // For AES-128

            System.out.println("CLIENT : Message before encryption : " + message);

            String encryptedMessage = cryptoAlgorithm.encrypt(message, aesKey);

            System.out.println("CLIENT : Message after encryption : " + encryptedMessage);

            out.writeObject(encryptedMessage);

        } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
