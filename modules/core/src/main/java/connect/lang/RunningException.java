package connect.lang;

/**
 * This exception is thrown when an error happened while executing a message flow
 */
public class RunningException extends Exception {
    public RunningException(String message) {
        super(message);
    }

    public RunningException(String message, Throwable cause) {
        super(message, cause);
    }
}
