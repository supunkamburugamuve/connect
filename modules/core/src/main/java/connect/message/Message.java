package connect.message;

import connect.env.ExecutionEnvironment;
import connect.lang.GlobalContext;
import connect.lang.Reference;

import java.net.ConnectException;
import java.util.Map;
import java.util.Stack;

public interface Message {
    /**
     * The type of the message.
     *
     * @return type of the message
     */
    String getType();

    /**
     * Return the current environment
     *
     * @return current execution environment
     */
    ExecutionEnvironment environment();

    /**
     * Global context for this message
     * @return the global context
     */
    GlobalContext getGlobalContext();

    /**
     * Message properties
     *
     * @return message properties
     */
    Map<String, Object> getProperties();

    /**
     * The actual payload of the message
     * @return payload of the message
     */
    Object getPayload();

    void setPayload(Object msg) throws ConnectException;

    /**
     * Get the message specific cloning object
     *
     * @return Message specific cloning object
     */
    MessageCloner getMessageCloner();

    /**
     * Get the unique id of the message
     *
     * @return id of the message
     */
    String getId();

    /**
     * Where we send if an error happens. These will be queue names
     *
     * @return get the error stack
     * */
    Stack<Reference> getErrorStack();

    MessageTrace getTrace();
}
