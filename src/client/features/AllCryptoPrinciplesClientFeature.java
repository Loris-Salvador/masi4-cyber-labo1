package client.features;

import org.json.JSONObject;

import javax.crypto.Cipher;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Base64;

public class AllCryptoPrinciplesClientFeature implements ClientFeature {

    private final String message;

    public AllCryptoPrinciplesClientFeature(String message)
    {
        this.message = message;
    }

    @Override
    public void execute(Socket serverSocket) throws IOException {

        try {
            KeyStore keystore = KeyStore.getInstance("JKS");
            FileInputStream fis = new FileInputStream("./keystore.jks");
            keystore.load(fis, "P@ssw0rd".toCharArray());

            PrivateKey privateKey = (PrivateKey) keystore.getKey("clientkey", "P@ssw0rd".toCharArray());

            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            InputStream inputStream = new FileInputStream("./servercert.crt");
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
