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
             ObjectInputStream in = new ObjectInputStream(serverSocket.getInputStream());
            PrintWriter outLine = new PrintWriter(serverSocket.getOutputStream(), true))
        {
            Properties properties = new Properties();

            FileInputStream inputStream = new FileInputStream("passwords.properties");
            properties.load(inputStream);
            String keystorePassword = properties.getProperty("KEYSTORE_PASSWORD");
            String keyPassword = properties.getProperty("KEYS_PASSWORDS");


            inputStream = new FileInputStream("config.properties");
            properties.load(inputStream);
            String keystorePath = properties.getProperty("KEYSTORE_PATH");
            String keyAlias = properties.getProperty("KEYSTORE_CLIENT_ALIAS");

            KeyStore keystore = KeyStore.getInstance("JKS");
            FileInputStream fis = new FileInputStream(keystorePath);
            keystore.load(fis, keystorePassword.toCharArray());


            BigInteger p = BigInteger.probablePrime(2048, new SecureRandom());
            BigInteger g = BigInteger.valueOf(2);


            PrivateKey clientPrivateKey = (PrivateKey) keystore.getKey(keyAlias, keyPassword.toCharArray());

            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initSign(clientPrivateKey);
            signature.update(p.toString().getBytes());
            signature.update(g.toString().getBytes());
            byte[] digitalSignature = signature.sign();


            JSONObject jsonObjectDH = new JSONObject();
            jsonObjectDH.put("p", p);
            jsonObjectDH.put("g", g);
            jsonObjectDH.put("signature", Base64.getEncoder().encodeToString(digitalSignature));

            System.out.println("CLIENT : DIFFIE HELLMAN parameters send");

            outLine.println(jsonObjectDH);

            PublicKey serverPublicValue = (PublicKey) in.readObject();

            System.out.println("CLIENT : Public value for DIFFIE HELLMAN received");


            DHParameterSpec dhParamSpec = new DHParameterSpec(p, g);

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
            keyPairGenerator.initialize(dhParamSpec);
            KeyPair clientKeyPair = keyPairGenerator.generateKeyPair();

            KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
            keyAgreement.init(clientKeyPair.getPrivate());

            System.out.println("CLIENT : Public value for DIFFIE HELLMAN send");

            out.writeObject(clientKeyPair.getPublic());

            keyAgreement.doPhase(serverPublicValue, true);
            byte[] sharedSecret = keyAgreement.generateSecret();
            byte[] symKeyAES = Arrays.copyOf(sharedSecret, 16);

            String encryptedMessage = cryptoAlgorithm.encrypt(message, symKeyAES);

            String signatureMessage = hmac.calculate(message, symKeyAES);

            JSONObject jsonObjectMessage = new JSONObject();
            jsonObjectMessage.put("message", encryptedMessage);
            jsonObjectMessage.put("signature", signatureMessage);

            System.out.println("CLIENT : Message before encryption : " + message);

            System.out.println("CLIENT : Message send : " + jsonObjectMessage);

            outLine.println(jsonObjectMessage);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
