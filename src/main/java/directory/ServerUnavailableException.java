package directory;

import java.net.ConnectException;

/**
 * Thrown if the server files are unavailble
 */
public class ServerUnavailableException extends ConnectException {

    public ServerUnavailableException(String msg) {
        super(msg);
    }

    public ServerUnavailableException() {

    }
}
