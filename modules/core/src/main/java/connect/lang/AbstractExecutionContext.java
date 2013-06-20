package connect.lang;

import connect.error.ConnectError;
import org.apache.commons.logging.Log;

import java.util.*;

public abstract class AbstractExecutionContext implements ExecutionContext {
    protected Log log;

    /** Pointer to global context */
    protected final ExecutionContext parent;

    /** A map for holding the variables */
    protected Map<String, Variable> variables = new HashMap<String, Variable>();

    /** Store the return value of a previous statement */
    protected Object returnValue = null;

    /** A flag indicating weather an error is raised */
    protected boolean inError = false;

    /**
     * The actual error that this execution context is in
     */
    protected ConnectError error;

    protected AbstractExecutionContext(Log log, ExecutionContext parent) {
        this.log = log;
        this.parent = parent;
    }

    public Variable getVariableValue(String variable) {
        // we have to go through the parent contexts and see
        Variable v = variables.get(variable);
        ExecutionContext p = parent;
        while (v == null && p != null) {
            v = p.getVariableValue(variable);
            p = p.getParent();
        }
        return v;
    }

    public void setVariable(Variable v) {
        variables.put(v.getName(), v);

        VariableMapper mapper = getVariableMapper(v.getName());
        if (mapper != null) {
            mapper.update(this, v);
        }
    }

    private VariableMapper getVariableMapper(String v) {
        ExecutionContext p = parent;
        ExecutionContext current = this;
        while (p != null) {
            current = p;
            p = p.getParent();
        }

        if (current instanceof ModuleContext) {
            return ((ModuleContext) current).getVariableMapper(v);
        }
        return null;
    }

    public void setReturnValue(Object object) {
        this.returnValue = object;
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public boolean isError() {
        return inError;
    }

    public void raiseError(ConnectError error) throws RunningException {
        if (inError) {
            throw new RunningException("A Unexpected error has occurred. " +
                    "We cannot raise an error while we are already in an error: " + error);
        }
        inError = true;
        this.error = error;
    }

    public void clearError() {
        inError = false;
    }

    public ConnectError getError() {
        return error;
    }

    public ExecutionContext getParent() {
        return parent;
    }
}
