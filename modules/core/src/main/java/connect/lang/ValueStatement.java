package connect.lang;

/**
 * This statement return a value after the statement has being executed
 */
public interface ValueStatement extends Statement {
    /**
     * Set the name of the return variable. This has to be called before the execute method.
     * @param name of the return variable
     */
    void setValueName(String name);
}
