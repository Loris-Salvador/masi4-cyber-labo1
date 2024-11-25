package server.features;

import algorithms.hmac.HMAC;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class HMACMD5ServerFeature implements ServerFeature {

    private final HMAC hmac;

    private final String key = "d1f8a4b3c6e2a7f9c8d3e6b5f2a9c7e1";

    public HMACMD5ServerFeature(HMAC hmac)
    {
        this.hmac = hmac;
    }

    @Override
    public void execute(Socket clientSocket) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())))
        {
            String combinedMessage;
            combinedMessage = in.readLine();

            String[] parts = combinedMessage.split("\\|"); // Split the message and HMAC
            String message = parts[0];
            String receivedHMAC = parts[1];

            System.out.println("Message Reçu : " + message);
            System.out.println("HMAC recalculé coté server : " + hmac.calculate(message, key.getBytes()));


        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            System.err.println("Erreur de communication : " + e.getMessage());
        }
    }
}
