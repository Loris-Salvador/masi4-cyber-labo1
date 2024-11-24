package server;

import server.features.Feature;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {

    private final int port;

    private final Feature feature;

    public Server(int port, Feature feature)
    {
        this.port = port;
        this.feature = feature;
    }

    public void run() {

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Waiting on port " + port);

            Socket clientSocket = serverSocket.accept();
            System.out.println("Connection established with : " + clientSocket.getInetAddress());

            feature.execute(clientSocket);

            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error server : " + e.getMessage());
        }
    }
}
