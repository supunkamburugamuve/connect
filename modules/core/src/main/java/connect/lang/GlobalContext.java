package connect.lang;

import connect.message.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This is the context we parse for one message
 */
public class GlobalContext extends AbstractExecutionContext {
    private static Log log = LogFactory.getLog(GlobalContext.class);

    private Message message;

    protected GlobalContext(Message message) {
        super(log, null);
        this.message = message;
    }

    /**
     * The global context is not going to have any entities
     * @param name name of the object
     * @return object
     * @throws RunningException
     */
    public Object getEntity(Reference name) throws RunningException {
        throw new IllegalStateException();
    }

    /**
     * The global context is not going to have any entities
     * @param name name of the object
     * @return object
     * @throws RunningException
     */
    public Object getEntity(Reference name, String messageType) throws RunningException {
        throw new IllegalStateException();
    }

    /**
     * Get the message
     * @return message
     */
    public Message getMessage() {
        return message;
    }
}
