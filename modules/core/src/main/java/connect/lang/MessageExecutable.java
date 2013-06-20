package connect.lang;

/**
 * Represent and executable entity. When this is executed the context will be changed.
 */
public interface MessageExecutable {
    /**
     * Execute the entity. This can be a statement a function call etc.
     *
     * @param context runtime information about the execution
     * @throws RunningException if an unexpected error occurs
     */
    void execute(ExecutionContext context) throws RunningException ;
}
