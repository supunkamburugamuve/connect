package connect.message;

import connect.ConnectConstants;
import connect.error.ConnectError;

import java.net.ConnectException;

public class ErrorMessage extends AbstractMessage {
    private ConnectError error;

    private Message originalMessage;

    public ErrorMessage(ConnectError error, Message originalMessage) {
        super(originalMessage.environment(), ConnectConstants.MessageTypes.ERROR, originalMessage.getTrace());
        this.error = error;
        this.originalMessage = originalMessage;
    }

    public ErrorMessage(ConnectError error) {
        super(null, ConnectConstants.MessageTypes.ERROR, null);
        this.error = error;
    }

    public Object getPayload() {
        return originalMessage.getPayload();
    }

    public void setPayload(Object msg) throws ConnectException {
        throw new UnsupportedOperationException("Error objects payload cannot be changed");
    }

    public ConnectError getError() {
        return error;
    }

    public Message getOriginalMessage() {
        return originalMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ErrorMessage message = (ErrorMessage) o;

        if (error != null ? !error.equals(message.error) : message.error != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return error != null ? error.hashCode() : 0;
    }
}
