package server.features;

import org.json.JSONObject;

import javax.crypto.Cipher;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Base64;
import java.util.Properties;

public class AllCryptoPrinciplesServerFeature implements ServerFeature {

    @Override
    public void execute(Socket clientSocket) throws IOException {

        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())))
        {
            Properties properties = new Properties();

            FileInputStream inputStream = new FileInputStream("passwords.properties");
            properties.load(inputStream);
            String keystorePassword = properties.getProperty("KEYSTORE_PASSWORD");
            String keyPassword = properties.getProperty("KEYS_PASSWORDS");

            inputStream = new FileInputStream("config.properties");
            properties.load(inputStream);
            String keystorePath = properties.getProperty("KEYSTORE_PATH");
            String keyAlias = properties.getProperty("KEYSTORE_SERVER_ALIAS");
            String clientCrtPath = properties.getProperty("CLIENT_CRT_PATH");

            String inputLine = in.readLine();

            JSONObject jsonObject = new JSONObject(inputLine);

            KeyStore keystore = KeyStore.getInstance("JKS");
            FileInputStream fis = new FileInputStream(keystorePath);
            keystore.load(fis, keystorePassword.toCharArray());

            PrivateKey serverPrivateKEy = (PrivateKey) keystore.getKey(keyAlias, keyPassword.toCharArray());

            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            inputStream = new FileInputStream(clientCrtPath);
            Certificate certificate = certificateFactory.generateCertificate(inputStream);
            inputStream.close();

            PublicKey clientPublicKey = certificate.getPublicKey();

            String base64EncryptedMessage = jsonObject.getString("message");
            String base64Signature = jsonObject.getString("signature");

            byte[] encryptedMessage = Base64.getDecoder().decode(base64EncryptedMessage);
            byte[] digitalSignature = Base64.getDecoder().decode(base64Signature);


            Cipher decryptCipher = Cipher.getInstance("RSA");
            decryptCipher.init(Cipher.DECRYPT_MODE, serverPrivateKEy);
            byte[] decryptedMessage = decryptCipher.doFinal(encryptedMessage);
            String decryptedMessageStr = new String(decryptedMessage);

            System.out.println("SERVER : Decrypted message : " + decryptedMessageStr);

            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initVerify(clientPublicKey);
            signature.update(decryptedMessage);
            boolean isVerified = signature.verify(digitalSignature);

            if(isVerified)
                System.out.println("SERVER : SIGNATURE OK");
            else
                System.out.println("SERVER : SIGNATURE FAILED");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
