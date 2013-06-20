package connect.queue;

import connect.Manageable;
import connect.lang.RunningException;
import connect.message.Message;
import connect.message.MessageListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class QueueListener implements Manageable {
    private static Log log = LogFactory.getLog(QueueListener.class);

    /** The message listener to be called */
    private final MessageListener listener;

    /** Weather we are running */
    private volatile boolean running = false;

    /** This is the name of the parent using the listener */
    private final String name;

    /** The queue we are listening to */
    private LinkQueue queue;

    private Thread handlerThread = null;

    public QueueListener(String name, LinkQueue queue, MessageListener listener) {
        this.listener = listener;
        this.name = name;
        this.queue = queue;

        handlerThread = new Thread(new MessageRunner());
    }

    public void start() {
        handlerThread = new Thread(new MessageRunner());
        running = true;

        handlerThread.start();
    }

    public void stop() {
        running = false;
    }

    /**
     * Listen to the queue and run the message
     */
    private class MessageRunner implements Runnable {
        public void run() {
            while (running) {
                try {
                    Message message = (Message) queue.take();
                    listener.onMessage(message);
                } catch (Throwable t) {
                    log.fatal("Uncaught exception: " + name, t);
                }
            }
        }
    }
}
