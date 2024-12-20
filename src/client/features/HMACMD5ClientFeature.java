package client.features;

import algorithms.hmac.HMAC;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class HMACMD5ClientFeature implements ClientFeature {

    private String message;

    private final HMAC hmac;

    private final String key = "d1f8a4b3c6e2a7f9c8d3e6b5f2a9c7e1";

    public HMACMD5ClientFeature(String message, HMAC hmac)
    {
        this.message = message;
        this.hmac = hmac;
    }

    @Override
    public void execute(Socket serverSocket) throws IOException {
        try
        {
            PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);

            String signatureHMAC = hmac.calculate(message, key.getBytes());


            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", message);
            jsonObject.put("signature", signatureHMAC);

            System.out.println("CLIENT : Message : " + jsonObject);

            out.println(jsonObject);

            out.close();
        }
        catch (NoSuchAlgorithmException | InvalidKeyException e)
        {
            throw new RuntimeException(e);
        }


    }
}
