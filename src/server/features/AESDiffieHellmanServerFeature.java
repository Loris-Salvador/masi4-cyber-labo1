package server.features;

import algorithms.crypto.CryptoAlgorithm;

import javax.crypto.KeyAgreement;
import javax.crypto.spec.DHParameterSpec;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.*;
import java.util.Arrays;

public class AESDiffieHellmanServerFeature implements ServerFeature {

    private final CryptoAlgorithm cryptoAlgorithm;

    public AESDiffieHellmanServerFeature(CryptoAlgorithm cryptoAlgorithm) {
        this.cryptoAlgorithm = cryptoAlgorithm;
    }

    @Override
    public void execute(Socket clientSocket) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

            BigInteger p = BigInteger.probablePrime(2048, new SecureRandom());
            BigInteger g = BigInteger.valueOf(2);

            out.writeObject(p);
            out.writeObject(g);

            DHParameterSpec dhParamSpec = new DHParameterSpec(p, g);

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
            keyPairGenerator.initialize(dhParamSpec);
            KeyPair serverKeyPair = keyPairGenerator.generateKeyPair();

            KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
            keyAgreement.init(serverKeyPair.getPrivate());

            out.writeObject(serverKeyPair.getPublic());

            PublicKey clientPublicKey = (PublicKey) in.readObject();

            keyAgreement.doPhase(clientPublicKey, true);
            byte[] sharedSecret = keyAgreement.generateSecret();
            byte[] aesKey = Arrays.copyOf(sharedSecret, 16);

            String message = (String) in.readObject();
            System.out.println("Message reçu : " + cryptoAlgorithm.decrypt(message, aesKey));

        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException |
                 ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}