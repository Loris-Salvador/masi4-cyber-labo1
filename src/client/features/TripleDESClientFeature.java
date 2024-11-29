package client.features;

import algorithms.crypto.CryptoAlgorithm;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;


public class TripleDESClientFeature implements ClientFeature {

    private final String key = "123456789012345678901234";

    private final String message;

    private final CryptoAlgorithm cryptoAlgorithm;

    public TripleDESClientFeature(CryptoAlgorithm cryptoAlgorithm, String message)
    {
        this.cryptoAlgorithm = cryptoAlgorithm;
        this.message = message;
    }

    @Override
    public void execute(Socket serverSocket) throws IOException {

        PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);

        System.out.println("CLIENT : Message before encryption : " + message);

        String encryptedMessage = cryptoAlgorithm.encrypt(message, key.getBytes());

        System.out.println("CLIENT : Message after encryption : " + encryptedMessage);

        out.println(encryptedMessage);

        out.close();
    }
}
