package server.features;

import algorithms.hmac.HMAC;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class HMACMD5ServerFeature implements ServerFeature {

    private final HMAC hmac;

    private final String key = "d1f8a4b3c6e2a7f9c8d3e6b5f2a9c7e1";

    public HMACMD5ServerFeature(HMAC hmac)
    {
        this.hmac = hmac;
    }

    @Override
    public void execute(Socket clientSocket) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())))
        {
            String inputLine;
            inputLine = in.readLine();

            JSONObject jsonObject = new JSONObject(inputLine);
            String message = jsonObject.getString("message");
            String signature = jsonObject.getString("signature");

            String HMACRecalculate = hmac.calculate(message, key.getBytes());

            System.out.println("SERVER : HMAC recalculate : " + HMACRecalculate);

            if(HMACRecalculate.equals(signature))
                System.out.println("SERVER : HMAC recalculate OK");
            else
                System.out.println("SERVER : HMAC recalculate FAIL");


        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            System.err.println("SERVER : Error : " + e.getMessage());
        }
    }
}
