package io.update;

import java.net.ConnectException;

/**
 * Thrown if the server io are unavailable
 */
class ServerUnavailableException extends ConnectException {

    public ServerUnavailableException(String msg) {
        super(msg);
    }

    public ServerUnavailableException() {

    }
}
