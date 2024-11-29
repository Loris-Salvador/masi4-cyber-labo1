package client.features;

import algorithms.crypto.CryptoAlgorithm;
import algorithms.hmac.HMAC;
import org.json.JSONObject;

import javax.crypto.KeyAgreement;
import javax.crypto.spec.DHParameterSpec;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;

public class RepudiationClientFeature implements ClientFeature{

    private final CryptoAlgorithm cryptoAlgorithm;

    private final HMAC hmac;

    private final String message;

    public RepudiationClientFeature(String message, CryptoAlgorithm cryptoAlgorithm, HMAC hmac)
    {
        this.message = message;
        this.cryptoAlgorithm = cryptoAlgorithm;
        this.hmac = hmac;
    }

    @Override
    public void execute(Socket serverSocket) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(serverSocket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(serverSocket.getInputStream()))
        {
            Properties properties = new Properties();

            FileInputStream inputStream = new FileInputStream("passwords.properties");
            properties.load(inputStream);
            String keystorePassword = properties.getProperty("KEYSTORE_PASSWORD");
            String keyPassword = properties.getProperty("KEYS_PASSWORDS");

            KeyStore keystore = KeyStore.getInstance("JKS");
            FileInputStream fis = new FileInputStream("./keystore.jks");
            keystore.load(fis, keystorePassword.toCharArray());

            PrivateKey privateKey = (PrivateKey) keystore.getKey("clientkey", keyPassword.toCharArray());



            BigInteger p = BigInteger.probablePrime(2048, new SecureRandom());
            BigInteger g = BigInteger.valueOf(2);


            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initSign(privateKey);
            signature.update(p.toString().getBytes());
            signature.update(g.toString().getBytes());
            byte[] digitalSignature = signature.sign();


            JSONObject jsonObjectDH = new JSONObject();
            jsonObjectDH.put("p", p);
            jsonObjectDH.put("g", g);
            jsonObjectDH.put("signature", Base64.getEncoder().encodeToString(digitalSignature));

            PrintWriter outLine = new PrintWriter(serverSocket.getOutputStream(), true);

            outLine.println(jsonObjectDH);

            DHParameterSpec dhParamSpec = new DHParameterSpec(p, g);

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
            keyPairGenerator.initialize(dhParamSpec);
            KeyPair serverKeyPair = keyPairGenerator.generateKeyPair();

            KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
            keyAgreement.init(serverKeyPair.getPrivate());

            out.writeObject(serverKeyPair.getPublic());

            PublicKey clientPublicKey = (PublicKey) in.readObject();

            keyAgreement.doPhase(clientPublicKey, true);
            byte[] sharedSecret = keyAgreement.generateSecret();
            byte[] symKey = Arrays.copyOf(sharedSecret, 16);

            String encryptedMessage = cryptoAlgorithm.encrypt(message, symKey);

            String signatureMessage = hmac.calculate(message, symKey);

            JSONObject jsonObjectMessage = new JSONObject();
            jsonObjectMessage.put("message", encryptedMessage);
            jsonObjectMessage.put("signature", signatureMessage);

            System.out.println("Message chiffré coté Client : " + jsonObjectMessage);

            outLine.println(jsonObjectMessage);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
