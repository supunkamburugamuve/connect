package connect;

public class ConnectRunningException extends RuntimeException {
    public ConnectRunningException(String message) {
        super(message);
    }

    public ConnectRunningException(String message, Throwable cause) {
        super(message, cause);
    }
}
