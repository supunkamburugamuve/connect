package connect.message;

import connect.env.ExecutionEnvironment;
import connect.lang.GlobalContext;
import connect.lang.Reference;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public abstract class AbstractMessage implements Message {
    /**
     * Properties of the message
     */
    protected Map<String, Object> properties = new HashMap<String, Object>();

    /**
     * Id of the message
     */
    protected String id;

    /**
     * The message cloner
     */
    protected MessageCloner messageCloner;

    /**
     * The environment where we execute this message
     */
    protected final ExecutionEnvironment environment;


    /** Where we send if an error happens. These will be queue names */
    protected Stack<Reference> errorStack;

    /**
     * Carries the information through all the executions
     */
    protected GlobalContext globalContext;

    /**
     * Type of the message. Cannot be changed after the initial assignment
     */
    protected final String type;

    /**
     * This is the trace of messages and flows that lead to this message
     */
    protected MessageTrace trace;

    /**
     * The execution environment
     *
     * @param environment the environment
     */
    protected AbstractMessage(ExecutionEnvironment environment, String type, MessageTrace messageTrace) {
        this.environment = environment;
        this.type = type;
        this.trace = messageTrace;
    }

    /**
     * Get the properties
     *
     * @return get the properties
     */
    public Map<String, Object> getProperties() {
        return properties;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMessageCloner(MessageCloner messageCloner) {
        this.messageCloner = messageCloner;
    }

    public MessageCloner getMessageCloner() {
        return messageCloner;
    }

    public ExecutionEnvironment environment() {
        return environment;
    }

    public Stack<Reference> getErrorStack() {
        return errorStack;
    }

    public GlobalContext getGlobalContext() {
        return globalContext;
    }

    public MessageTrace getTrace() {
        return trace;
    }
}
