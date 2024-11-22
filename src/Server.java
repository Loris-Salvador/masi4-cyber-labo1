import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final int port;


    public Server(int port)
    {
        this.port = port;
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

            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error server : " + e.getMessage());
        }
    }
}
