package client.features;

import algorithms.hash.Hash;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class HashSHA1ClientFeature implements ClientFeature {

    private final String message;

    private final Hash hashAlgorithm;


    public HashSHA1ClientFeature(Hash hashAlgorithm, String message)
    {
        this.hashAlgorithm = hashAlgorithm;
        this.message = message;
    }

    @Override
    public void execute(Socket serverSocket) throws IOException {
        PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);

        String hashMessage = hashAlgorithm.hash(message.getBytes());

        System.out.println("CLIENT : Message before hash: " + message);

        out.println(hashMessage);

        out.close();
    }
}
