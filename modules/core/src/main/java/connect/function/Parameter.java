package connect.function;

/**
 * Defines the parameter passed to a function. A parameter
 * should be passed along with its name to a function.
 */
public class Parameter {
    /** Name of the parameter */
    private String name;
    /** Default value if any */
    private Object defaultValue;
    /** weather this parameter is optional */
    private boolean optional = false;

    /**
     * Define a parameter with the name
     *
     * @param name name of the parameter
     */
    public Parameter(String name) {
        this.name = name;
    }

    public Parameter(String name, boolean optional) {
        this.name = name;
        this.optional = optional;
    }

    public Parameter(String name, Object defaultValue, boolean optional) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.optional = optional;
    }

    public Parameter(String name, Object defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    /**
     * Name of the parameter
     * @return name of the parameter
     */
    public String getName() {
        return name;
    }

    /**
     * The default value of this parameter
     *
     * @param defaultValue default value
     */
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public boolean isOptional() {
        return optional;
    }
}
