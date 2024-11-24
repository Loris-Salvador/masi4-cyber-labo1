package client.features;

import algorithms.crypto.CryptoAlgorithm;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;


public class TripleDESHardCodedKeys implements Feature {

    private final String key = "123456789012345678901234";

    private final String message = "Hello World!";

    private final CryptoAlgorithm cryptoAlgorithm;

    public TripleDESHardCodedKeys(CryptoAlgorithm cryptoAlgorithm)
    {
        this.cryptoAlgorithm = cryptoAlgorithm;
    }

    @Override
    public void execute(Socket serverSocket) throws IOException {

        PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);

        String encryptedMessage = cryptoAlgorithm.encrypt(message, key);

        out.println(encryptedMessage);
    }
}
