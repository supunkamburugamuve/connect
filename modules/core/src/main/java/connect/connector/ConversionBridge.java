package connect.connector;

import connect.message.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A connector will connect a flow to another flow. A connector can transform a message from one type to another.
 * The only way to transform a message from one type to another is using a connector.
 */
public class ConversionBridge extends AbstractBridge {
    private static Log log = LogFactory.getLog(ConversionBridge.class);
    /** Set of message converters this connector can use */
    private List<MessageConverter> messageConverters = new ArrayList<MessageConverter>();

    protected ConversionBridge(String name) {
        super(log, name);
    }

    public void start() {
        inListener.start();
    }

    @Override
    protected void onInMessage(Message m) {
        executor.execute(new ConvertingWorker(m));
    }

    private class ConvertingWorker implements Runnable {
        private final Message message;

        private ConvertingWorker(Message message) {
            this.message = message;
        }

        public void run() {
            for (MessageConverter converter : messageConverters) {
                if (converter.isTriggered(message)) {
                    Message newMessage;
                    try {
                        newMessage = converter.transform(message);
                        forwardMessage(newMessage);
                    } catch (ConversionException e) {
                        // parse an Error message and inject to outflow
                        ErrorMessage errorMessage = ErrorMessageFactory.create(message, e,
                                "Failed to transform the message: " + message.getId());

                        sendError(message, errorMessage);
                    }
                }
            }
        }
    }

    /**
     * Add a message converter
     * @param converter message converter
     */
    public void addMessageConverter(MessageConverter converter) {
        messageConverters.add(converter);
    }

    /**
     * Remove a message converter
     *
     * @param converter remove a converter
     */
    public void removeMessageConverter(MessageConverter converter) {
        messageConverters.remove(converter);
    }
}
