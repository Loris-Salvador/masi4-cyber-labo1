package client.features;

import org.json.JSONObject;

import javax.crypto.Cipher;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Base64;
import java.util.Properties;

public class AllCryptoPrinciplesClientFeature implements ClientFeature {

    private final String message;

    public AllCryptoPrinciplesClientFeature(String message)
    {
        this.message = message;
    }

    @Override
    public void execute(Socket serverSocket) throws IOException {

        try {
            Properties properties = new Properties();

            FileInputStream inputStream = new FileInputStream("passwords.properties");
            properties.load(inputStream);
            inputStream = new FileInputStream("config.properties");
            String keystorePassword = properties.getProperty("KEYSTORE_PASSWORD");
            String keyPassword = properties.getProperty("KEYS_PASSWORDS");
            properties.load(inputStream);
            String keystorePath = properties.getProperty("KEYSTORE_PATH");
            String keyAlias = properties.getProperty("KEYSTORE_CLIENT_ALIAS");
            String serverCrtPath = properties.getProperty("SERVER_CRT_PATH");


            KeyStore keystore = KeyStore.getInstance("JKS");
            FileInputStream fis = new FileInputStream(keystorePath);
            keystore.load(fis, keystorePassword.toCharArray());

            PrivateKey privateKey = (PrivateKey) keystore.getKey(keyAlias, keyPassword.toCharArray());

            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            inputStream = new FileInputStream(serverCrtPath);
            Certificate certificate = certificateFactory.generateCertificate(inputStream);
            inputStream.close();

            PublicKey publicKey = certificate.getPublicKey();

            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedMessage = encryptCipher.doFinal(message.getBytes());

            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initSign(privateKey);
            signature.update(message.getBytes());
            byte[] digitalSignature = signature.sign();

            PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);

            String base64EncryptedMessage = Base64.getEncoder().encodeToString(encryptedMessage);
            String base64Signature = Base64.getEncoder().encodeToString(digitalSignature);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", base64EncryptedMessage);
            jsonObject.put("signature", base64Signature);

            out.println(jsonObject);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
