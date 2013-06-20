package connect.lang;

import connect.error.ConnectError;
import connect.message.Message;

import java.util.Map;

/**
 * Every execution block has its own execution context. It has all the information
 * about the current execution.
 */
public interface ExecutionContext {
    int GLOBAL_CONTEXT = 1;
    int BASE_CONTEXT = 0;

    /**
     * Get the object with the given name
     * @param name name of the object
     * @return object
     * @throws RunningException if an error occurs
     */
    Object getEntity(Reference name) throws RunningException;

    /**
     * Get the object with the given name
     * @param name name of the object
     * @return object
     * @throws RunningException if an error occurs
     */
    Object getEntity(Reference name, String messageType) throws RunningException;

    /**
     * Get the value of a variable
     * @param variable variable
     * @return value
     */
    Variable getVariableValue(String variable);

    /**
     * Set a variable
     *
     * @param v variable to set
     */
    void setVariable(Variable v);

    /**
     * Set the return value
     * @param object the return value
     */
    void setReturnValue(Object object);

    /**
     * Get the return value, return null if return value is not set
     *
     * @return the return value
     */
    Object getReturnValue();

    /**
     * This flag is set when an error is raised. After an error is raised it should be handled,
     * otherwise the execution of the normal flow cannot continue.The error handling happens inside a
     * <code>CatchStatement</code>. If a suitable error handler cannot be found the error is propagated
     * to the top level and ultimately the default error handler will be invoked. This error handler must
     * be provided by the invoker of the mediation engine.
     *
     * @return true if an error is raised
     */
    boolean isError();

    /**
     * This is used to raise an error/ clear an error
     *
     * @param error weather we are in error or not
     * @throws RunningException if an error occurs
     */
    void raiseError(ConnectError error) throws RunningException;

    /**
     * Get the current error
     *
     * @return the current error
     */
    ConnectError getError();

    /**
     * Clear the current error in the execution flow. An error is cleared by the
     * corresponding catch block
     */
    void clearError();

    /**
     * Get the current message. If a message is not present return <code>null</code>
     *
     * @return message
     */
    Message getMessage();

    /**
     * Get the parent contexts that are visible
     *
     * @return the list of parent contexts
     */
    ExecutionContext getParent();
}
