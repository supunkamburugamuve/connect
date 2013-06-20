package connect.endpoint;

import connect.Manageable;
import connect.QualifiedName;
import connect.State;
import connect.lang.ModuleContext;
import connect.lang.Reference;
import connect.lang.RunningException;
import connect.message.Message;
import connect.message.MessageListener;
import connect.queue.LinkQueue;
import connect.queue.QueueListener;
import org.apache.commons.logging.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a basic implementation of the <code>Endpoint<code/>.
 */
public abstract class AbstractEndpoint implements Endpoint, Manageable {
    private Log log = null;
    /** Name of the endpoint */
    private String name;
    /** Set of properties defined for this endpoint */
    protected Map<String, Object> properties = new HashMap<String, Object>();

    /** The configuration */
    protected ModuleContext moduleContext;

    /**
     * All the queues are configured statically. We don't expect a dynamic configuration at this point.
     * If a dynamic configuration is required we may have to modify here. But it is for later.
     */
    /** This is where we get messages */
    protected Reference inputQueueName;

    /** This is the next queue that we are going to put the message */
    protected Reference nextQueueName;

    /** We send the reply or an error to this queue */
    protected Reference invokerReceivingQueueName;

    /** The input queue */
    protected LinkQueue inputQueue;

    /** The receiving queue */
    protected LinkQueue nextQueue;

    /** The listener used for in queue */
    protected QueueListener inputQueueListener;

    protected State state = State.CREATED;

    protected AbstractEndpoint(Log log, String name) {
        this.log = log;
        this.name = name;
    }

    public void init(ModuleContext context) throws RunningException {
        if (state != State.CREATED) {
            throw new IllegalStateException(
                    "Cannot initialized an Endpoint which is already initialized..");
        }

        this.moduleContext = context;

        Object o = context.getEntity(inputQueueName);
        if (!(o instanceof LinkQueue)) {
            // raise an error
            String msg = "A Queue is expected as the incoming queue for endpoint: " + name;

            log.error(msg);
            throw new RunningException(msg);
        }
        inputQueue = (LinkQueue) o;

        o = context.getEntity(nextQueueName);
        if (!(o instanceof LinkQueue)) {
            // raise an error
            String msg = "A Queue is expected as the receiving incoming queue for endpoint: " + name;
            log.error(msg);
            throw new RunningException(msg);
        }
        nextQueue = (LinkQueue) o;

        // we are going to use a listener to listen to the incoming messages
        inputQueueListener = new QueueListener(name, inputQueue, new InputQueueListener());
        state = State.INIT;
    }

    public void destroy() {
    }

    public void start() {
        inputQueueListener.start();
    }

    public void stop() {
        inputQueueListener.stop();
    }

    public String getName() {
        return name;
    }

    /**
     * A simple message listener
     */
    private class InputQueueListener implements MessageListener {
        public void onMessage(Message m) {
            handleInput(m);
        }
    }

    public QualifiedName getQualifiedName() {
        if (state != State.CREATED) {
            return new QualifiedName(name, moduleContext.getModuleName().getModule(),
                    moduleContext.getModuleName().getNamespace());
        } else {
            throw new IllegalStateException("The qualified name is available only after initialization");
        }
    }

    /**
     * This should be implemented by the child classes.
     * @param m message
     */
    protected abstract void handleInput(Message m);

    protected void forwardMessage(Message m) {
        nextQueue.offer(m);
    }

    public void setInvokerReceivingQueue(Reference invokerReceivingQueueName) {
        this.invokerReceivingQueueName = invokerReceivingQueueName;
    }

    public void setInputQueue(Reference inputQueueName) {
        this.inputQueueName = inputQueueName;
    }

    public void setNextQueue(Reference nextQueueName) {
        this.nextQueueName = nextQueueName;
    }
}
