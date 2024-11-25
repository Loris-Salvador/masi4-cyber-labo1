package server.features;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class SHA1ServerFeature implements ServerFeature {
    @Override
    public void execute(Socket clientSocket) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())))
        {
            String message;
            message = in.readLine();

            System.out.println("Message hashé : " + message);

        } catch (IOException e) {
            System.err.println("Erreur de communication : " + e.getMessage());
        }
    }
}
