import algorithms.crypto.AES;
import algorithms.crypto.CryptoAlgorithm;
import algorithms.crypto.TripleDES;
import algorithms.hash.Hash;
import algorithms.hash.SHA1;
import algorithms.hmac.HMAC;
import algorithms.hmac.MD5;
import client.Client;
import client.features.*;
import server.Server;
import server.features.*;

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

        //startDESHardCodedKey(port, "Hello DES");
        //startAESDiffieHellman(port, "Hello AES");
        //startSHA1(port, "Hello SHA1");
        //startHMACMD5(port, "Hello HMAC-MD5");
        startSignSHA1RSA(port, "Hello SHA and RSA");
    }

    private static void startDESHardCodedKey(int port, String message)
    {
        CryptoAlgorithm cryptoAlgorithm = new TripleDES();
        ServerFeature serverFeature = new TripleDESServerFeature(cryptoAlgorithm);

        Server server = new Server(port, serverFeature);

        ClientFeature clientfeature = new TripleDESClientFeature(cryptoAlgorithm, message);

        Client client = new Client(port, clientfeature);

        server.start();
        client.start();
    }

    private static void startAESDiffieHellman(int port, String message)
    {
        CryptoAlgorithm cryptoAlgorithm = new AES();
        ServerFeature serverFeature = new AESDiffieHellmanServerFeature(cryptoAlgorithm);

        Server server = new Server(port, serverFeature);

        ClientFeature clientfeature = new AESDiffieHellmanClientFeature(cryptoAlgorithm, message);

        Client client = new Client(port, clientfeature);

        server.start();
        client.start();
    }

    private static void startSHA1(int port, String message)
    {
        Hash hash = new SHA1();
        ServerFeature serverFeature = new HashSHA1ServerFeature();

        Server server = new Server(port, serverFeature);

        ClientFeature clientFeature = new HashSHA1ClientFeature(hash, message);

        Client client = new Client(port, clientFeature);

        server.start();
        client.start();
    }

    private static void startHMACMD5(int port, String message)
    {
        HMAC hmac = new MD5();
        ServerFeature serverFeature = new HMACMD5ServerFeature(hmac);

        Server server = new Server(port, serverFeature);

        ClientFeature clientFeature = new HMACMD5ClientFeature(message, hmac);

        Client client = new Client(port, clientFeature);

        server.start();
        client.start();
    }

    private static void startSignSHA1RSA(int port, String message)
    {
        Hash hash = new SHA1();
        ServerFeature serverFeature = new SignSHA1RSAServerFeature();

        Server server = new Server(port, serverFeature);

        ClientFeature clientFeature = new SignSHA1RSAClientFeature(hash, message);

        Client client = new Client(port, clientFeature);

        server.start();
        client.start();
    }

}