package client;

import client.features.ClientFeature;

import java.io.IOException;
import java.net.Socket;

public class Client extends Thread {

    private final int port;

    private final ClientFeature clientFeature;

    public Client(int port, ClientFeature clientFeature)
    {
        this.port = port;
        this.clientFeature = clientFeature;
    }

    public void run()
    {
        try {
            Socket serverSocket = new Socket("localhost", port);

            clientFeature.execute(serverSocket);

            serverSocket.close();
        } catch (IOException e) {
            System.err.println("Erreur du client : " + e.getMessage());
        }
    }

}
