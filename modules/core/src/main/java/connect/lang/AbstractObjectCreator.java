package connect.lang;

import connect.QualifiedName;
import connect.State;
import connect.function.Parameter;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractObjectCreator implements ObjectCreator {
    /** Name of the object */
    protected final String name;

    protected ModuleContext baseContext;

    protected State state = State.CREATED;

    /** We have a list of parameters */
    protected List<Parameter> parameters = new ArrayList<Parameter>();

    /**
     * Create an object creator with a given name
     * @param name name
     */
    protected AbstractObjectCreator(String name) {
        this.name = name;
    }

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
        this.baseContext = baseContext;
        state = State.INIT;
    }

    public void destroy() throws RunningException {
        state = State.SHUTDOWN;
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
