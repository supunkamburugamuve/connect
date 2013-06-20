package connect.message;

import connect.error.ConnectError;
import connect.error.ErrorFactory;

/**
 * Factory methods for creating errors
 */
public class ErrorMessageFactory {
    public static ErrorMessage create(Message m, Exception e, String msg) {
        ConnectError error = ErrorFactory.create(e, msg);

        return new ErrorMessage(error, m);
    }

    public static ErrorMessage create(Message m, ConnectError error) {
        return new ErrorMessage(error, m);
    }
}
