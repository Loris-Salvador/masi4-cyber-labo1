package server;

import server.implentations.Implementation;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final int port;

    private final Implementation Implementation;

    public Server(int port, Implementation Implementation)
    {
        this.port = port;
        this.Implementation = Implementation;
    }

    public void start() {

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Waiting on port" + port);

            Socket clientSocket = serverSocket.accept();
            System.out.println("Connection established with : " + clientSocket.getInetAddress());

            InputStream input = clientSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));



            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println("Message received : " + message);
            }

            reader.close();
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error server : " + e.getMessage());
        }
    }
}
