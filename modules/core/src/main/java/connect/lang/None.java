package connect.lang;

/**
 * This is a special Object for specifying None value
 */
public class None {
    private final String value = "None";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        None none = (None) o;

        return !(!value.equals(none.value));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
