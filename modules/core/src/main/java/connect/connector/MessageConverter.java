package connect.connector;

import connect.message.Message;

/**
 * A message converter converts a message from a given type to another.
 */
public interface MessageConverter {
    Message transform(Message m) throws ConversionException;

    boolean isTriggered(Message m);
}
