package server.features;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Base64;

public class RSAKeyStoreServerFeature implements ServerFeature {

    @Override
    public void execute(Socket clientSocket) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String encodedMessage = in.readLine();

            KeyStore keystore = KeyStore.getInstance("JKS");
            FileInputStream fis = new FileInputStream("C:\\Users\\loris\\MASI4-git\\CyberSecurite\\masi4-cyber-labo1\\mykeystore");
            keystore.load(fis, "P@ssw0rd".toCharArray());

            PrivateKey privateKey = (PrivateKey) keystore.getKey("mykey", "P@ssw0rd".toCharArray());

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] encryptedMessage = Base64.getDecoder().decode(encodedMessage);
            byte[] decryptedMessage = cipher.doFinal(encryptedMessage);

            System.out.println("Message déchiffré : " + new String(decryptedMessage));
        } catch (IOException e) {
            System.err.println("Erreur de communication : " + e.getMessage());
        } catch (CertificateException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException |
                 NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }
}
