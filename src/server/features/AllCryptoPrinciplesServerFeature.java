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

            String inputLine = in.readLine();

            JSONObject jsonObject = new JSONObject(inputLine);

            KeyStore keystore = KeyStore.getInstance("JKS");
            FileInputStream fis = new FileInputStream("./keystore.jks");
            keystore.load(fis, keystorePassword.toCharArray());

            PrivateKey privateKey = (PrivateKey) keystore.getKey("serverkey", keyPassword.toCharArray());

            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            inputStream = new FileInputStream("./clientcert.crt");
            Certificate certificate = certificateFactory.generateCertificate(inputStream);
            inputStream.close();

            PublicKey publicKey = certificate.getPublicKey();

            String base64EncryptedMessage = jsonObject.getString("message");
            String base64Signature = jsonObject.getString("signature");

            byte[] encryptedMessage = Base64.getDecoder().decode(base64EncryptedMessage);
            byte[] digitalSignature = Base64.getDecoder().decode(base64Signature);


            Cipher decryptCipher = Cipher.getInstance("RSA");
            decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedMessage = decryptCipher.doFinal(encryptedMessage);
            String decryptedMessageStr = new String(decryptedMessage);

            System.out.println("Decrypted message : " + decryptedMessageStr);

            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initVerify(publicKey);
            signature.update(decryptedMessage);
            boolean isVerified = signature.verify(digitalSignature);

            System.out.println("Signature verified : " + isVerified);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
