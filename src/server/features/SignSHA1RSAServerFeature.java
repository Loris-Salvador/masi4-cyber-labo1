package server.features;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class SignSHA1RSAServerFeature implements ServerFeature {

    private final String publicKeyBase64 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsxx8XRfRxq/j/1qheixJtIJPsr8Isp2yJL8PGmom+mySjIJkivqabEVshM/hTx88POyNfkEuF2+njbv7sc3xdn56liz9AbMK607kMzOPQojuG1uYPTvCJo91RWaip44VFCirz2aWv6dlp7Jz5ukzzWXoM8+f+stc8BxzSdTQ4HEO+SFc9QriUwFJd+lFZUnSV3zGzelN0pXFsC9SlMJlkaUDg4HMz5LX7LYj85zjTsEdCUH3aCJbJXp1g4rztPpBwuNz7Nkp5PknLacLcocyk1/BGN9h46KgyJoNSkQJ2g+WFZYrjmHGUJRauGwIOPEP/4tyZeDnfPW2qONGogLXxwIDAQAB";

    @Override
    public void execute(Socket clientSocket) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())))
        {

            String data = in.readLine();

            JSONObject jsonObject = new JSONObject(data);
            String message = jsonObject.getString("message");
            String signature = jsonObject.getString("signature");

            boolean isVerified = verify(message, signature, getPublicKey());

            if(isVerified)
                System.out.println("SERVER : VERIFICATION OK");
            else
                System.out.println("SERVER : VERIFICATION FAILED");

        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private PublicKey getPublicKey() throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyBase64);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    private boolean verify(String data, String signature, PublicKey publicKey) throws Exception {
        Signature rsa = Signature.getInstance("SHA1withRSA");
        rsa.initVerify(publicKey);
        rsa.update(data.getBytes());
        return rsa.verify(Base64.getDecoder().decode(signature));
    }
}
