import algorithms.crypto.CryptoAlgorithm;
import algorithms.crypto.TripleDES;
import client.Client;
import server.Server;
import server.features.Feature;
import server.features.TripleDESHardCodedKeys;

public class Main {
    public static void main(String[] args) {

        CryptoAlgorithm cryptoAlgorithm = new TripleDES();
        Feature serverFeature = new TripleDESHardCodedKeys(cryptoAlgorithm);

        Server server = new Server(50000, serverFeature);

        client.features.Feature clientfeature = new client.features.TripleDESHardCodedKeys(cryptoAlgorithm);

        Client client = new Client(50000, clientfeature);

        server.start();
        client.start();
    }

}