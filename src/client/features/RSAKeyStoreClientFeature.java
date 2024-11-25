package client.features;

import javax.crypto.Cipher;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Base64;

public class RSAKeyStoreClientFeature implements ClientFeature {

    private final String message;

    public RSAKeyStoreClientFeature(String message) {
        this.message = message;
    }

    @Override
    public void execute(Socket serverSocket) throws IOException {
        try {
            PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);

            KeyStore keystore = KeyStore.getInstance("JKS");
            FileInputStream fis = new FileInputStream("C:\\Users\\loris\\MASI4-git\\CyberSecurite\\masi4-cyber-labo1\\mykeystore");
            keystore.load(fis, "P@ssw0rd".toCharArray());

            Certificate cert = keystore.getCertificate("mykey");
            PublicKey publicKey = cert.getPublicKey();

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] encryptedMessage = cipher.doFinal(message.getBytes());
            String encodedMessage = Base64.getEncoder().encodeToString(encryptedMessage);

            out.println(encodedMessage);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
