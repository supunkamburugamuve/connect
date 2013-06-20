package connect.error;

public class ErrorFactory {
    /**
     * A util function for creating an error from an exception raised
     * @param e the actual exception
     * @return an Error
     */
    public static ConnectError create(Exception e) {
        ConnectError error = new ConnectError(ErrorTypes.EXCEPTION);

        error.setCause(e);

        return error;
    }

    /**
     * A util function for creating an error from an exception raised
     * @param e the actual exception
     * @param message the message
     * @return an Error
     */
    public static ConnectError create(Exception e, String message) {
        ConnectError error = new ConnectError(ErrorTypes.EXCEPTION);

        error.setCause(e);
        error.setMessage(message);

        return error;
    }

    /**
     * A util function for creating an error from an exception raised
     * @param e the actual exception
     * @param message the message
     * @return an Error
     */
    public static ConnectError create(Exception e, int type, String message) {
        ConnectError error = new ConnectError(type);

        error.setCause(e);
        error.setMessage(message);

        return error;
    }

    /**
     * A util function for creating an error from an exception type and message
     * @param type the exception type
     * @param message the message
     * @return an Error
     */
    public static ConnectError create(int type, String message) {
        ConnectError error = new ConnectError(type);

        error.setMessage(message);

        return error;
    }
}
