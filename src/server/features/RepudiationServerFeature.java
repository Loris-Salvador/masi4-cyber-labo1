package server.features;

import algorithms.crypto.CryptoAlgorithm;
import algorithms.hmac.HMAC;
import org.json.JSONObject;

import javax.crypto.KeyAgreement;
import javax.crypto.spec.DHParameterSpec;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import java.util.Properties;

public class RepudiationServerFeature implements ServerFeature {

    private final CryptoAlgorithm cryptoAlgorithm;

    private HMAC hmac;

    public RepudiationServerFeature(HMAC hmac, CryptoAlgorithm cryptoAlgorithm)
    {
        this.hmac = hmac;
        this.cryptoAlgorithm = cryptoAlgorithm;
    }
    @Override
    public void execute(Socket clientSocket) throws IOException {

        try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
             BufferedReader inLine = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream()))
        {

            Properties properties = new Properties();

            FileInputStream inputStream = new FileInputStream("passwords.properties");
            properties.load(inputStream);
            String keystorePassword = properties.getProperty("KEYSTORE_PASSWORD");

            inputStream = new FileInputStream("config.properties");
            properties.load(inputStream);
            String keystorePath = properties.getProperty("KEYSTORE_PATH");
            String clientCrtPath = properties.getProperty("CLIENT_CRT_PATH");


            KeyStore keystore = KeyStore.getInstance("JKS");
            FileInputStream fis = new FileInputStream(keystorePath);
            keystore.load(fis, keystorePassword.toCharArray());


            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            inputStream = new FileInputStream(clientCrtPath);
            Certificate certificate = certificateFactory.generateCertificate(inputStream);
            inputStream.close();

            PublicKey clientPublicKey = certificate.getPublicKey();

            String inputLine = inLine.readLine();

            JSONObject jsonObject = new JSONObject(inputLine);

            System.out.println("SERVER : DIFFIE HELLMAN parameters received");

            BigInteger p = jsonObject.getBigInteger("p");
            BigInteger g = jsonObject.getBigInteger("g");
            String signatureString = jsonObject.getString("signature");

            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initVerify(clientPublicKey);
            signature.update(p.toString().getBytes());
            signature.update(g.toString().getBytes());
            boolean isVerified = signature.verify(Base64.getDecoder().decode(signatureString));

            if(!isVerified)
            {
                System.out.println("SERVER : Signature verification failed");
                System.exit(1);
            }
            else
            {
                System.out.println("SERVER : Signature verification for Diffie Hellman param OK");
            }

            DHParameterSpec dhParamSpec = new DHParameterSpec(p, g);

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
            keyPairGenerator.initialize(dhParamSpec);
            KeyPair clientKeyPair = keyPairGenerator.generateKeyPair();

            KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
            keyAgreement.init(clientKeyPair.getPrivate());

            out.writeObject(clientKeyPair.getPublic());

            System.out.println("SERVER : Public value send");


            PublicKey serverPublicKey = (PublicKey) in.readObject();

            System.out.println("SERVER : Client public value received");


            keyAgreement.doPhase(serverPublicKey, true);
            byte[] sharedSecret = keyAgreement.generateSecret();
            byte[] symKey = Arrays.copyOf(sharedSecret, 16);

            String json = inLine.readLine();

            jsonObject = new JSONObject(json);

            String base64EncryptedMessage = jsonObject.getString("message");
            String base64Signature = jsonObject.getString("signature");

            String decryptedMessage = cryptoAlgorithm.decrypt(base64EncryptedMessage, symKey);

            signatureString = hmac.calculate(decryptedMessage, symKey);

            System.out.println("SERVER : Decrypted message : " + decryptedMessage);


            if(!Objects.equals(signatureString, base64Signature))
                System.out.println("SERVER : Signature Failed");
            else
                System.out.println("SERVER : Signature OK");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
