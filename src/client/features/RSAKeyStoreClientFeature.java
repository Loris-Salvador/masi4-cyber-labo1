package client.features;

import javax.crypto.Cipher;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Base64;
import java.util.Properties;

public class RSAKeyStoreClientFeature implements ClientFeature {

    private final String message;

    public RSAKeyStoreClientFeature(String message) {
        this.message = message;
    }

    @Override
    public void execute(Socket serverSocket) throws IOException {
        try {
            PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);

            Properties properties = new Properties();

            FileInputStream inputStream = new FileInputStream("passwords.properties");
            properties.load(inputStream);
            String keystorePassword = properties.getProperty("KEYSTORE_PASSWORD");

            inputStream = new FileInputStream("config.properties");
            properties.load(inputStream);
            String keystorePath = properties.getProperty("KEYSTORE_PATH");
            String keyAlias = properties.getProperty("RSA_KEYSTORE_FEATURE_ALIAS");

            KeyStore keystore = KeyStore.getInstance("JKS");
            inputStream = new FileInputStream(keystorePath);
            keystore.load(inputStream, keystorePassword.toCharArray());

            Certificate cert = keystore.getCertificate(keyAlias);
            PublicKey publicKey = cert.getPublicKey();

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] encryptedMessage = cipher.doFinal(message.getBytes());
            String encodedMessage = Base64.getEncoder().encodeToString(encryptedMessage);

            System.out.println("CLIENT : Message before encryption : " + message);

            out.println(encodedMessage);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
