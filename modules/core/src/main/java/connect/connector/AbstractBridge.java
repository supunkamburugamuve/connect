package connect.connector;

import connect.ConnectRunningException;
import connect.QualifiedName;
import connect.State;
import connect.executor.MessageExecutor;
import connect.lang.ModuleContext;
import connect.lang.Reference;
import connect.lang.RunningException;
import connect.message.Message;
import connect.message.MessageListener;
import connect.queue.LinkQueue;
import connect.queue.QueueListener;
import org.apache.commons.logging.Log;

/**
 * Abstract class implementing basic functions of a bridge.
 */
public abstract class AbstractBridge implements Bridge {
    protected Log log = null;
    /* Name of the bridge */
    protected final String name;
    /* Reference to the incoming queue */
    protected Reference inQueueName;
    /* Reference to the outgoing queue */
    protected Reference nextQueueName;
    /* Reference to the executor */
    protected Reference executorName;
    /* Incoming queue */
    protected LinkQueue<Message> inQueue = null;
    /** Outgoing queue */
    protected LinkQueue<Message> nextQueue = null;
    /* A message executor */
    protected MessageExecutor executor = null;
    /* This will be notified when a message arrives through the queue */
    protected QueueListener inListener = null;
    /** State of the bridge */
    protected State state = State.CREATED;
    /** EntityConfiguration */
    protected ModuleContext baseContext;

    protected AbstractBridge(Log log, String name) {
        this.log = log;
        this.name = name;
    }

    public void stop() {
        if (state != State.STARTED) {
            throw new IllegalStateException("Cannot stop Bridge without starting it.. :" + name);
        }
        // stop the listener
        inListener.stop();
    }

    public void init(ModuleContext baseContext) throws RunningException{
        if (state != State.CREATED) {
            throw new IllegalStateException(
                    "Cannot initialized a Bridge which is already initialized..");
        }

        this.baseContext = baseContext;

        // fetch the queue using the reference
        Object o = baseContext.getEntity(inQueueName);
        if (o instanceof LinkQueue) {
            inQueue = (LinkQueue) o;
        } else {
            handleError("The entity referred by " + inQueueName + " is not a Queue");
            return;
        }

        // fetch the queue using the reference
        o = baseContext.getEntity(nextQueueName);
        if (o instanceof LinkQueue) {
            nextQueue = (LinkQueue) o;
        } else {
            handleError("The entity referred by " + nextQueueName + " is not a Queue");
            return;
        }

        o = baseContext.getEntity(executorName);
        if (o instanceof MessageExecutor) {
            executor = (MessageExecutor) o;
        } else {
            handleError("The entity reference");
        }

        inListener = new QueueListener(name, inQueue, new InMessageListener());

        state = State.INIT;

        if (log.isDebugEnabled()) {
            log.debug("Initialized the connector: " + name);
        }
    }

    private class InMessageListener implements MessageListener {
        public void onMessage(Message m) {
            onInMessage(m);
        }
    }

    public void start() {
        if (state != State.INIT) {
            throw new IllegalStateException("Cannot start Bridge without initializing it.. :" + name);
        }

        inListener.start();
    }

    public void destroy() throws RunningException {
        if (state == State.CREATED) {
            throw new IllegalStateException("Cannot destroy a un-initialized object..");
        }

        // destroy the executor
        executor.destroy();
    }

    public Reference getInputQueue() {
        return inQueueName;
    }

    protected abstract void onInMessage(Message m);

    protected void forwardMessage(Message m) {
        nextQueue.offer(m);
    }

    public void setInputQueue(Reference inQueueName) {
        this.inQueueName = inQueueName;
    }

    public void setNextQueue(Reference nextQueueName) {
        this.nextQueueName = nextQueueName;
    }

    public Reference getInQueue() {
        return inQueueName;
    }

    public Reference getNextQueue() {
        return nextQueueName;
    }

    public void setExecutorName(Reference executorName) {
        this.executorName = executorName;
    }

    public Reference getExecutorName() {
        return executorName;
    }

    public String getName() {
        return name;
    }

    private void handleError(String msg) {
        log.error(msg);
        throw new ConnectRunningException(msg);
    }

    @Override
    public String toString() {
        return name;
    }

    public QualifiedName getQualifiedName() {
        if (state != State.CREATED) {
            return new QualifiedName(name, baseContext.getModuleName().getModule(),
                    baseContext.getModuleName().getNamespace());
        } else {
            throw new IllegalStateException("The qualified name is available only after initialization");
        }
    }

    protected void sendError(Message originalMessage, Message errorMessage) {
        // get the queue that is responsible for handling the error
        Reference errorQueue = originalMessage.getErrorStack().pop();

        // fetch the queue using the reference
        Object o;
        try {
            o = baseContext.getEntity(errorQueue);
        } catch (RunningException e) {
            log.error("Failed to send the error back because error queue cannot be found: " + errorQueue);
            return;
        }

        if (o instanceof LinkQueue) {
            LinkQueue<Message> nextQueue = (LinkQueue<Message>) o;
            nextQueue.offer(errorMessage);
        } else {
            log.error("The entity referred by " + errorQueue + " is not a Queue");
        }
    }
}
