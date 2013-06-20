package connect.flow;

import connect.*;
import connect.executor.MessageExecutor;
import connect.lang.*;
import connect.message.Message;
import connect.message.MessageListener;
import connect.queue.LinkQueue;
import connect.queue.QueueListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Flow is the basic execution placeholder. All the
 */
public class Flow implements Namable, ManagedEntity, Manageable, Connectable {
    private Log log = LogFactory.getLog(Flow.class);

    /* List of statements this flow will execute */
    private List<Statement> statements = new ArrayList<Statement>();

    /** Name of this flow */
    private final String name;

    /** Executor name */
    private Reference executorName;

    /* This thread pool is used for executing the messages */
    private MessageExecutor executor;

    private Reference inputQueueName;

    /** The incoming getInputQueue */
    private LinkQueue inputQueue;

    /** Queue listener for listening to the incoming messages */
    private QueueListener queueListener;

    /** State */
    private State state = State.CREATED;

    /** The base context that should be used to parse the context */
    private ModuleContext baseContext;

    /** The message type being processed by this flow. This flow cannot process any other type */
    private String messageType;

    public Flow(String name, String messageType) {
        this.name = name;
        this.messageType = messageType;
    }

    public void addStatement(Statement st) {
        statements.add(st);
    }

    public void removeStatement(int index) {
        statements.remove(index);
    }

    public String getName() {
        return name;
    }

    public void init(ModuleContext baseContext) throws RunningException {
        if (state != State.CREATED) {
            throw new IllegalStateException(
                    "Cannot initialized a Flow which is already initialized.. :" + name);
        }

        this.baseContext = baseContext;

        // lookup the name of the getInputQueue to figure out the actual getInputQueue
        Object o = baseContext.getEntity(inputQueueName);
        if (!(o instanceof LinkQueue)) {
            String msg = "Failed to obtain the incoming getInputQueue for flow: " + name;
            log.error(msg);
            throw new RunningException(msg);
        }
        inputQueue = (LinkQueue) o;

        o = baseContext.getEntity(executorName);
        if (o instanceof MessageExecutor) {
            executor = (MessageExecutor) o;
        } else {
            String msg = "Failed to obtain the executor for flow: " + name;
            log.error(msg);
            throw new RunningException(msg);
        }

        queueListener = new QueueListener(name, inputQueue, new MessageHandler(this));

        state = State.INIT;
    }

    public void destroy() throws RunningException {
        if (state == State.CREATED) {
            throw new IllegalStateException("Cannot destroy flow without " +
                    "initializing it.. :" + name);
        }

        // we destroy the executor
        executor.destroy();
        state = State.SHUTDOWN;
    }

    public void start() {
        if (state != State.INIT) {
            throw new IllegalStateException("Cannot start flow without " +
                    "initializing it.. :" + name);
        }

        queueListener.start();
        state = State.STARTED;
    }

    public void stop() {
        if (state != State.STARTED) {
            throw new IllegalStateException("Cannot stop flow without " +
                    "starting it.. :" + name);
        }
        queueListener.stop();
        state = State.STOPPED;
    }

    public Reference getInputQueue() {
        return inputQueueName;
    }

    private class MessageHandler implements MessageListener {
        private Flow flow;

        private MessageHandler(Flow flow) {
            this.flow = flow;
        }

        public void onMessage(Message m) {
            executor.execute(new FlowWorker(flow, statements, m));
        }
    }

    public ModuleContext getBaseContext() {
        return baseContext;
    }

    public void setExecutor(Reference executor) {
        this.executorName = executor;
    }

    public void setInputQueue(Reference inputQueue) {
        this.inputQueueName = inputQueue;
    }

    public String getMessageType() {
        return messageType;
    }

    public QualifiedName getQualifiedName() {
        if (state != State.CREATED) {
            return new QualifiedName(name, baseContext.getModuleName().getModule(),
                    baseContext.getModuleName().getNamespace());
        } else {
            throw new IllegalStateException("The qualified name is available only after initialization");
        }
    }
}
