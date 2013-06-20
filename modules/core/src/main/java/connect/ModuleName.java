package connect;

/**
 * Represents a module name
 */
public class ModuleName {
    private String module;

    private String namespace;

    public ModuleName(String module) {
        this(module, null);
    }

    public ModuleName(String module, String namespace) {
        if (module == null) {
            throw new IllegalArgumentException("Module name shouldn't be null");
        }
        this.module = module;
        this.namespace = namespace;
    }

    public String getModule() {
        return module;
    }

    public String getNamespace() {
        return namespace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModuleName that = (ModuleName) o;

        if (module != null ? !module.equals(that.module) : that.module != null) return false;
        if (namespace != null ? !namespace.equals(that.namespace) : that.namespace != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = module != null ? module.hashCode() : 0;
        result = 31 * result + (namespace != null ? namespace.hashCode() : 0);
        return result;
    }
}


