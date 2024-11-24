package client;

import client.features.Feature;

import java.io.IOException;
import java.net.Socket;

public class Client extends Thread {

    private final int port;

    private final Feature feature;

    public Client(int port, Feature feature)
    {
        this.port = port;
        this.feature = feature;
    }

    public void run()
    {
        try {
            Socket serverSocket = new Socket("localhost", port);

            feature.execute(serverSocket);

            serverSocket.close();
        } catch (IOException e) {
            System.err.println("Erreur du client : " + e.getMessage());
        }
    }

}
