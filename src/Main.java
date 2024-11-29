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
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Properties properties = new Properties();
        int port = -1;

        try {
            FileInputStream inputStream = new FileInputStream("config.properties");
            properties.load(inputStream);
            port = Integer.parseInt(properties.getProperty("PORT"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (port == -1) {
            System.out.println("Numéro de port incorrect");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        int choice;

        do {

            System.out.println("\nChoisissez une fonctionnalité a lancer :");
            System.out.println("0 - Quitter");
            System.out.println("1 - Triple DES");
            System.out.println("2 - AES Diffie-Hellman");
            System.out.println("3 - SHA-1");
            System.out.println("4 - HMAC-MD5");
            System.out.println("5 - Signature SHA1 avec RSA");
            System.out.println("6 - RSA avec Keystore");
            System.out.println("7 - 4 principes cryptographiques");
            System.out.println("8 - Dénis plausible");
            System.out.print("Votre choix : ");

            choice = scanner.nextInt();

            switch (choice) {
                case 0:
                    break;
                case 1:
                    startDESHardCodedKey(port, "Hello DES");
                    break;
                case 2:
                    startAESDiffieHellman(port, "Hello AES");
                    break;
                case 3:
                    startSHA1(port, "Hello SHA1");
                    break;
                case 4:
                    startHMACMD5(port, "Hello HMAC-MD5");
                    break;
                case 5:
                    startSignSHA1RSA(port, "Hello SHA and RSA");
                    break;
                case 6:
                    startRSAKeyStore(port, "Hello RSA KeyStore!");
                    break;
                case 7:
                    startAllCryptoPrinciples(port, "Coucou");
                    break;
                case 8:
                    startRepudiation(port, "Coucou");
                    break;
                default:
                    System.out.println("Choix invalide, veuillez entrer un nombre entre 1 et 8.");
                    break;
            }
        }
        while (choice != 0);


        scanner.close();
    }

    private static void startDESHardCodedKey(int port, String message) {
        CryptoAlgorithm cryptoAlgorithm = new TripleDES();
        ServerFeature serverFeature = new TripleDESServerFeature(cryptoAlgorithm);

        Server server = new Server(port, serverFeature);
        ClientFeature clientFeature = new TripleDESClientFeature(cryptoAlgorithm, message);

        Client client = new Client(port, clientFeature);

        server.start();
        client.start();

        try
        {
            server.join();
            client.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void startAESDiffieHellman(int port, String message) {
        CryptoAlgorithm cryptoAlgorithm = new AES();
        ServerFeature serverFeature = new AESDiffieHellmanServerFeature(cryptoAlgorithm);

        Server server = new Server(port, serverFeature);
        ClientFeature clientFeature = new AESDiffieHellmanClientFeature(cryptoAlgorithm, message);

        Client client = new Client(port, clientFeature);

        server.start();
        client.start();

        try
        {
            server.join();
            client.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void startSHA1(int port, String message) {
        Hash hash = new SHA1();
        ServerFeature serverFeature = new HashSHA1ServerFeature();

        Server server = new Server(port, serverFeature);
        ClientFeature clientFeature = new HashSHA1ClientFeature(hash, message);

        Client client = new Client(port, clientFeature);

        server.start();
        client.start();

        try
        {
            server.join();
            client.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void startHMACMD5(int port, String message) {
        HMAC hmac = new MD5();
        ServerFeature serverFeature = new HMACMD5ServerFeature(hmac);

        Server server = new Server(port, serverFeature);
        ClientFeature clientFeature = new HMACMD5ClientFeature(message, hmac);

        Client client = new Client(port, clientFeature);

        server.start();
        client.start();

        try
        {
            server.join();
            client.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void startRSAKeyStore(int port, String message) {
        ServerFeature serverFeature = new RSAKeyStoreServerFeature();

        Server server = new Server(port, serverFeature);
        ClientFeature clientFeature = new RSAKeyStoreClientFeature(message);

        Client client = new Client(port, clientFeature);

        server.start();
        client.start();

        try
        {
            server.join();
            client.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void startSignSHA1RSA(int port, String message) {
        Hash hash = new SHA1();
        ServerFeature serverFeature = new SignSHA1RSAServerFeature();

        Server server = new Server(port, serverFeature);
        ClientFeature clientFeature = new SignSHA1RSAClientFeature(hash, message);

        Client client = new Client(port, clientFeature);

        server.start();
        client.start();

        try
        {
            server.join();
            client.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void startAllCryptoPrinciples(int port, String message) {
        ServerFeature serverFeature = new AllCryptoPrinciplesServerFeature();

        Server server = new Server(port, serverFeature);
        ClientFeature clientFeature = new AllCryptoPrinciplesClientFeature(message);

        Client client = new Client(port, clientFeature);

        server.start();
        client.start();

        try
        {
            server.join();
            client.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private static void startRepudiation(int port, String message) {
        HMAC hmac = new MD5();
        CryptoAlgorithm cryptoAlgorithm = new AES();

        ServerFeature serverFeature = new RepudiationServerFeature(hmac, cryptoAlgorithm);

        Server server = new Server(port, serverFeature);
        ClientFeature clientFeature = new RepudiationClientFeature(message, cryptoAlgorithm, hmac);

        Client client = new Client(port, clientFeature);

        server.start();
        client.start();

        try
        {
            server.join();
            client.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
