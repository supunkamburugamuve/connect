package connect.lang;

public enum Scope {
    GLOBAL,     // these variables are visible to every flow function and block
    MESSAGE,    // these are visible to the components in the current message
    FLOW,       // these variables are visible within a flow to all the functions and executions
    CONTEXT    // these variables are visible to the current execution block
}
