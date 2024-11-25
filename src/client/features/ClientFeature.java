package client.features;

import java.io.IOException;
import java.net.Socket;

public interface ClientFeature {

    void execute(Socket serverSocket) throws IOException;
}
