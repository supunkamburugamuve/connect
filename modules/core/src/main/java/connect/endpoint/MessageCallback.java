package connect.endpoint;

import connect.message.Message;

/**
 * This is the return message interface.
 */
public interface MessageCallback {
    void onMessage(Message m);
}
