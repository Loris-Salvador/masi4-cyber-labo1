package client.features;

import algorithms.hash.Hash;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class SHA1 implements Feature {

    private final String message = "Hello SHA-1 :)";

    private final Hash hashAlgorithm;


    public SHA1(Hash hashAlgorithm)
    {
        this.hashAlgorithm = hashAlgorithm;
    }

    @Override
    public void execute(Socket serverSocket) throws IOException {
        PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);

        String hashMessage = hashAlgorithm.hash(message.getBytes());

        out.println(hashMessage);

        out.close();
    }
}
