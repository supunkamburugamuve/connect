package connect.lang;

/**
 * A variable can hold a actual value or it can be a reference. A variable has a name.
 */
public class Variable {
    /** name of the variable */
    private final String name;
    /** Value of the variable */
    private Object value;

    public Variable(String name) {
        this(name, null);
    }

    public Variable(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
