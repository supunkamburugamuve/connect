package connect.error;

/**
 * Representation of an error
 */
public class ConnectError {
    /**
     * Cause of the error
     */
    private Object cause;

    /**
     * Message in the error
     */
    private String message;

    /**
     * Type of the error. The error types are defined in the <code>ErrorTypes</code> class.
     */
    private int type;

    public ConnectError(int type) {
        this.type = type;
    }

    public ConnectError(int type, String message, Object cause) {
        this.type = type;
        this.message = message;
        this.cause = cause;
    }

    public ConnectError(int type, String message) {
        this.type = type;
        this.message = message;
    }

    public ConnectError(Object cause, int type) {
        this.cause = cause;
        this.type = type;
    }

    public void setCause(Object cause) {
        this.cause = cause;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getCause() {
        return cause;
    }

    public String getMessage() {
        return message;
    }

    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Error msg: " + message + (cause !=null ? "Cause: " + cause.toString() : "");
    }
}
