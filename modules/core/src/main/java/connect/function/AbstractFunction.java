package connect.function;

import connect.QualifiedName;
import connect.State;
import connect.lang.ModuleContext;
import connect.lang.RunningException;

import java.util.ArrayList;
import java.util.List;

/**
 * Gives basic functions to the function implementations.
 */
public abstract class AbstractFunction implements Function {
    /** Name of the function */
    protected String name = null;

    /** We have a list of parameters */
    protected List<Parameter> parameters = new ArrayList<Parameter>();

    protected ModuleContext moduleContext;

    protected State state = State.CREATED;

    /**
     * Get the list of parameters
     *
     * @return list of parameters
     */
    public List<Parameter> getParameters() {
        return parameters;
    }

    /**
     * Add a parameter to the function
     *
     * @param parameter  add a parameter
     */
    public void addParameter(Parameter parameter) {
        parameters.add(parameter);
    }

    /**
     * Get the name of the function
     *
     * @return name of the function
     */
    public String getName() {
        return name;
    }

    protected void addParameters(String parameters[]) {
        if (parameters != null) {
            for (String p : parameters) {
                this.parameters.add(new Parameter(p));
            }
        }
    }

    protected String requiredParameterMessage(String param) {
        return "The parameter:" + param + " is required";
    }

    protected String invalidParameterMessage(String param) {
        return "The parameter:" + param + " has an unexpected type";
    }

    public void init(ModuleContext baseContext) throws RunningException {
        this.moduleContext = baseContext;
        this.state = State.INIT;
    }

    public void destroy() throws RunningException {
        this.state = State.SHUTDOWN;
    }

    public QualifiedName getQualifiedName() {
        if (state != State.CREATED) {
            return new QualifiedName(name, moduleContext.getModuleName().getModule(),
                    moduleContext.getModuleName().getNamespace());
        } else {
            throw new IllegalStateException("The qualified name is available only after initialization");
        }
    }
}
