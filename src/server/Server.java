package server;

import client.features.Feature;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final int port;

    private final Feature feature;

    public Server(int port, Feature feature)
    {
        this.port = port;
        this.feature = feature;
    }

    public void start() {

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Waiting on port" + port);

            Socket clientSocket = serverSocket.accept();
            System.out.println("Connection established with : " + clientSocket.getInetAddress());

            InputStream input = clientSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            feature.execute(reader);

            reader.close();
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error server : " + e.getMessage());
        }
    }
}
