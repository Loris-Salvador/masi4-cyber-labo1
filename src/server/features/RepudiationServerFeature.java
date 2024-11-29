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
             ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream()))
        {
            BufferedReader inLine = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            Properties properties = new Properties();

            FileInputStream inputStream = new FileInputStream("passwords.properties");
            properties.load(inputStream);
            String keystorePassword = properties.getProperty("KEYSTORE_PASSWORD");
            String keyPassword = properties.getProperty("KEYS_PASSWORDS");


            KeyStore keystore = KeyStore.getInstance("JKS");
            FileInputStream fis = new FileInputStream("./keystore.jks");
            keystore.load(fis, keystorePassword.toCharArray());

            PrivateKey privateKey = (PrivateKey) keystore.getKey("serverkey", keyPassword.toCharArray());

            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            inputStream = new FileInputStream("./clientcert.crt");
            Certificate certificate = certificateFactory.generateCertificate(inputStream);
            inputStream.close();

            PublicKey publicKey = certificate.getPublicKey();


            String inputLine = inLine.readLine();

            JSONObject jsonObject = new JSONObject(inputLine);

            BigInteger p = jsonObject.getBigInteger("p");
            BigInteger g = jsonObject.getBigInteger("g");
            String signatureString = jsonObject.getString("signature");

            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initVerify(publicKey);
            signature.update(p.toString().getBytes());
            signature.update(g.toString().getBytes());
            boolean isVerified = signature.verify(Base64.getDecoder().decode(signatureString));

            if(!isVerified)
            {
                System.out.println("Signature verification failed");
                System.exit(1);
            }

            DHParameterSpec dhParamSpec = new DHParameterSpec(p, g);

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
            keyPairGenerator.initialize(dhParamSpec);
            KeyPair clientKeyPair = keyPairGenerator.generateKeyPair();

            KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
            keyAgreement.init(clientKeyPair.getPrivate());

            out.writeObject(clientKeyPair.getPublic());

            PublicKey serverPublicKey = (PublicKey) in.readObject();

            keyAgreement.doPhase(serverPublicKey, true);
            byte[] sharedSecret = keyAgreement.generateSecret();
            byte[] symKey = Arrays.copyOf(sharedSecret, 16); // For AES-128

            String json = inLine.readLine();

            jsonObject = new JSONObject(json);

            String base64EncryptedMessage = jsonObject.getString("message");
            String base64Signature = jsonObject.getString("signature");

            String decryptedMessage = cryptoAlgorithm.decrypt(base64EncryptedMessage, symKey);

            signatureString = hmac.calculate(decryptedMessage, symKey);

            if(!Objects.equals(signatureString, base64Signature))
                System.out.println("Signature echouée");
            else
                System.out.println("Signature OK");

            System.out.println("Message Dechiffré : " + decryptedMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
