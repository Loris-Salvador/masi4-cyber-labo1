package server.features;

import algorithms.crypto.CryptoAlgorithm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class TripleDESHardCodedKeys implements Feature {

    private final String key = "123456789012345678901234";

    private final CryptoAlgorithm cryptoAlgorithm;

    public TripleDESHardCodedKeys(CryptoAlgorithm cryptoAlgorithm)
    {
        this.cryptoAlgorithm = cryptoAlgorithm;
    }

    @Override
    public void execute(Socket clientSocket) throws IOException {

        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())))
        {
            String message;
            message = in.readLine();

            String decryptedMessage = cryptoAlgorithm.decrypt(message, key.getBytes());

            System.out.println("Message décrypté : " + decryptedMessage);

        } catch (IOException e) {
            System.err.println("Erreur de communication : " + e.getMessage());
        }
    }
}