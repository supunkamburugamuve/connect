package connect.connector;

import connect.message.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This bridge connects two entities without any message conversions
 */
public class PassThroughBridge extends AbstractBridge {
    private static Log log = LogFactory.getLog(ConversionBridge.class);

    public PassThroughBridge(String name) {
        super(log, name);
    }

    @Override
    protected void onInMessage(Message m) {
        executor.execute(new MessageProcessor(m));
    }

    private class MessageProcessor implements Runnable {
        final Message message;
        private MessageProcessor(Message m) {
            this.message = m;
        }

        public void run() {
            forwardMessage(message);
        }
    }

    private class PassThroughWorker implements Runnable {
        private Message message;

        private PassThroughWorker(Message message) {
            this.message = message;
        }

        public void run() {
            nextQueue.offer(message);
        }
    }
}
