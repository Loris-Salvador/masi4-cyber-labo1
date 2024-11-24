import algorithms.crypto.AES;
import algorithms.crypto.CryptoAlgorithm;
import algorithms.crypto.TripleDES;
import algorithms.hash.Hash;
import algorithms.hash.SHA1;
import client.Client;
import server.Server;
import server.features.AESDiffieHellman;
import server.features.Feature;
import server.features.TripleDESHardCodedKeys;

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
        Feature serverFeature = new TripleDESHardCodedKeys(cryptoAlgorithm);

        Server server = new Server(port, serverFeature);

        client.features.Feature clientfeature = new client.features.TripleDESHardCodedKeys(cryptoAlgorithm);

        Client client = new Client(port, clientfeature);

        server.start();
        client.start();
    }

    private static void startAESDiffieHellman(int port)
    {
        CryptoAlgorithm cryptoAlgorithm = new AES();
        Feature serverFeature = new AESDiffieHellman(cryptoAlgorithm);

        Server server = new Server(port, serverFeature);

        client.features.Feature clientfeature = new client.features.AESDiffieHellman(cryptoAlgorithm);

        Client client = new Client(port, clientfeature);

        server.start();
        client.start();
    }

    private static void startSHA1(int port)
    {
        Hash hash = new SHA1();
        Feature serverFeature = new server.features.SHA1();

        Server server = new Server(port, serverFeature);

        client.features.Feature clientFeature = new client.features.SHA1(hash);

        Client client = new Client(port, clientFeature);

        server.start();
        client.start();
    }

}