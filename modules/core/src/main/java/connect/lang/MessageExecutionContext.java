package connect.lang;

import connect.flow.Flow;
import connect.message.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MessageExecutionContext extends AbstractExecutionContext {
    private static Log log = LogFactory.getLog(MessageExecutionContext.class);

    /** Current message */
    protected  Message message;

    /** The flow to which this message belongs */
    protected Flow flow;

    protected MessageExecutionContext(Message message, ExecutionContext context, Flow flow) {
        super(log, context);
        this.message = message;
        this.flow = flow;
    }

    public Message getMessage() {
        return message;
    }

    public Object getEntity(Reference name) throws RunningException {
        // parent cannot be null
        if (parent == null) {
            String msg = "There must be a parent context..";
            log.error(msg);
            throw new RunningException(msg);
        }

        ExecutionContext p = parent;
        ExecutionContext current = this;

        while (p != null) {
            current = p;
            p = p.getParent();
        }
        // invoke the base context
        return current.getEntity(name, flow.getMessageType());
    }

    public Object getEntity(Reference name, String messageType) throws RunningException {
        return getEntity(name);
    }

    public Flow getFlow() {
        return flow;
    }

    /**
     * We will first look in the parent contexts and if cannot be found look in to the global context
     * @param variable variable to be find
     * @return object
     */
    public Variable getVariableValue(String variable) {
        Variable v = super.getVariableValue(variable);
        if (v == null) {
            return message.getGlobalContext().getVariableValue(variable);
        }
        return v;
    }
}
