package client.features;

import algorithms.hash.Hash;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class SignSHA1RSAClientFeature implements ClientFeature {

    private final String message;

    private final String privateKeyBase64 = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCzHHxdF9HGr+P/WqF6LEm0gk+yvwiynbIkvw8aaib6bJKMgmSK+ppsRWyEz+FPHzw87I1+QS4Xb6eNu/uxzfF2fnqWLP0BswrrTuQzM49CiO4bW5g9O8Imj3VFZqKnjhUUKKvPZpa/p2WnsnPm6TPNZegzz5/6y1zwHHNJ1NDgcQ75IVz1CuJTAUl36UVlSdJXfMbN6U3SlcWwL1KUwmWRpQODgczPktfstiPznONOwR0JQfdoIlslenWDivO0+kHC43Ps2Snk+SctpwtyhzKTX8EY32HjoqDImg1KRAnaD5YVliuOYcZQlFq4bAg48Q//i3Jl4Od89bao40aiAtfHAgMBAAECggEAEaNgqd2SdnGpebg9RlCTMjf8949xdf7VOrd9RMQnB3/mpKNN7xPDs52DP3Gl8J/qU4jrXCoOCxfJ6nQUfWDYV6ttO1m90UuGQUBDymOtnByWRNJtzmXn9i/SxB1Z2jdB4yTS2o9HPCNqKCwI1uNTNcsZz/Rb1EZE5jkJr2p05rfWQKpBJfgZxYI85hB7FrUfEPpWs/CVdvjIqEbbKS2WAjpiFV4g8hdkCloFT4vpt4mYVF3Rs0NGlsMxJuVWEPBrhY63+cijsMcTDQ9xIe8LCIL22h0n0Pn9xXiI6qYRDazFpsi/V9ltOaGi02BKye0zoTXdRuj8V8fH2jv/2Iou4QKBgQD2xOVjXfj1Tji8rPo1g/PKrfIVe+pz8PpziQca0Wkteac6xd7/Y+spxlhAcPONwuNhaxLtqKlIH1ngm+/JfdRLMIOwUA9sBFGl4LgHyY8KWla0OOLsCvWCI603+165NoDbRqU43xBji96N7aba37Vr7XyA8Ns34U/jlBPMNwLdoQKBgQC5z6+430k0X8VhXavJ14Dx1dHd4zqlMg9fdxj0ktOntX9a6h61KZV+oGeKgMga1DLV8CRJLYPUbByNZBzV8KDxkW6wyLh30R1LY3v3tlEVcfn7hI/B6iMXP3rB3UkPrPjUs58oVGlp/6PYlfF1RKjknHYLC1PRklkDEgNkQ0QsZwKBgQCfRDEw0uPtnxCrZZEPnRxpwZ6vEw0cy3k5vETjoCib+xpdqnvkpV6P1b37yWrIIfKTW4IZ9XNcYy+k8b/vcdDkvmkgEGwDsT3pK4JbNFGnwuqW4uhCpEgUOGaB7TETpQPBgnT8oat4NDvtqma4eQ4knGBw8ALsq/Td0yJ7+T1WQQKBgAle0OTI1d8/SSvs1mH6bTVE7C19bRKxoJbW4bpLwUK+42pJTj0imLhnDHGupui7dMjXMG1ET1aiERqMRuv/E467H9LofG0GbazEmpbWaeZj9hBaEgncbeKZP+6Q40NYHMBVQpx9DUfcLX4ZqbCZQs8smMNb0eFKuRRvjx46SjDXAoGBAI0Zd+L+ntme/uX30tBIPWHIxgzv7G6BheD+VnwpXmQwgxrO1H0eFcyyCHI96np4AZeDnXFytxbBtO/duATmBZDlb1XpWNC0ym6SGNOpgr9vvWy2/Q4P0Dtl6mLZyA8oOc9ymPO0J34glDcjlODo/tcfbpH9PAWXBMH1/JZDjKF6";

    public SignSHA1RSAClientFeature(Hash hashAlgorithm, String message)
    {
        this.message = message;
    }

    @Override
    public void execute(Socket serverSocket) throws IOException {

        try {
            PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);

            String signature = sign(message, getPrivateKey());

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", message);
            jsonObject.put("signature", signature);

            System.out.println("CLIENT : Message : " + jsonObject);

            out.println(jsonObject);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private PrivateKey getPrivateKey() throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyBase64);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    private String sign(String data, PrivateKey privateKey) throws Exception {
        Signature rsa = Signature.getInstance("SHA1withRSA");
        rsa.initSign(privateKey);
        rsa.update(data.getBytes());
        return Base64.getEncoder().encodeToString(rsa.sign());
    }
}
