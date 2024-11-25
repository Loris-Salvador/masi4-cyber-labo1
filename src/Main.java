import algorithms.crypto.AES;
import algorithms.crypto.CryptoAlgorithm;
import algorithms.crypto.TripleDES;
import algorithms.hash.Hash;
import algorithms.hash.SHA1;
import client.Client;
import client.features.AESDiffieHellmanClientFeature;
import client.features.ClientFeature;
import client.features.SHA1ClientFeature;
import client.features.TripleDESClientFeature;
import server.Server;
import server.features.AESDiffieHellmanServerFeature;
import server.features.SHA1ServerFeature;
import server.features.ServerFeature;
import server.features.TripleDESServerFeature;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {
        Properties properties = new Properties();
        int port = -1;

        try {
            FileInputStream inputStream = new FileInputStream("config.properties");
            properties.load(inputStream);

            port = Integer.parseInt(properties.getProperty("PORT"));
        } catch (IOException e) {
            e.setStackTrace(e.getStackTrace());
        }

        if (port == -1)
        {
            System.out.println("Numéro de port incorrect");
            return;
        }

        //startDESHardCodedKey(port);
        //startAESDiffieHellman(port);
        startSHA1(port);
    }

    private static void startDESHardCodedKey(int port)
    {
        CryptoAlgorithm cryptoAlgorithm = new TripleDES();
        ServerFeature serverFeature = new TripleDESServerFeature(cryptoAlgorithm);

        Server server = new Server(port, serverFeature);

        ClientFeature clientfeature = new TripleDESClientFeature(cryptoAlgorithm);

        Client client = new Client(port, clientfeature);

        server.start();
        client.start();
    }

    private static void startAESDiffieHellman(int port)
    {
        CryptoAlgorithm cryptoAlgorithm = new AES();
        ServerFeature serverFeature = new AESDiffieHellmanServerFeature(cryptoAlgorithm);

        Server server = new Server(port, serverFeature);

        ClientFeature clientfeature = new AESDiffieHellmanClientFeature(cryptoAlgorithm);

        Client client = new Client(port, clientfeature);

        server.start();
        client.start();
    }

    private static void startSHA1(int port)
    {
        Hash hash = new SHA1();
        ServerFeature serverFeature = new SHA1ServerFeature();

        Server server = new Server(port, serverFeature);

        ClientFeature clientFeature = new SHA1ClientFeature(hash);

        Client client = new Client(port, clientFeature);

        server.start();
        client.start();
    }

}