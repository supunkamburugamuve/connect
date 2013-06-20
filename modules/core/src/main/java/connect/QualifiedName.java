package connect;

import connect.lang.RunningException;

/**
 * This is used to reference a object in a unique way.
 * A qualified name consists of 3 parts
 *
 * namespace.module.name:version
 *
 * Version is optional. name and module are mandatory and namespace can be empty
 */
public class QualifiedName {
    public static final String DIVIDER = ".";

    /** Name of the object. cannot be null */
    private final String name;

    /** Name of the module. cannot be null */
    private final String module;

    /** Namespace can be null */
    private final String namespace;

    /** Version is optional */
    private final Version version;

    public QualifiedName(String name, String module) {
        this(name, module, null);
    }

    public QualifiedName(String name, String module, String namespace) {
        this(name, module, namespace, null);
    }

    public QualifiedName(String name, String module, String namespace, Version version) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }

        this.name = name;
        this.namespace = namespace;
        this.module = module;
        this.version = version;
    }

    /**
     * Create a name object using the string representation of the name
     * @param name string representation of the name
     * @return Name object
     * @throws connect.lang.RunningException if an invalid name is passed
     */
    public static QualifiedName parse(String name) throws RunningException {
        // divide the string using the :
        if (name.indexOf(DIVIDER) == 0) {
            throw new RunningException("Invalid name.. You cannot start name with ':' " + name);
        }

        String namespace = name.substring(0, name.indexOf(":") - 1);
        String n = name.substring(name.indexOf(":"));

        return new QualifiedName(n, namespace);
    }

    /**
     * Get the name of the object
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * The the module of this name
     *
     * @return module part of the name
     */
    public String getModule() {
        return module;
    }

    /**
     * Get the version, if not set version will default to 0,0
     *
     * @return the version
     */
    public Version getVersion() {
        return version;
    }

    /**
     * Get the namespace. This can return null.
     *
     * @return the namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Check weather this is only a name or a fully qualified name
     *
     * @return true if the module is present
     */
    public boolean isFullyQualified() {
        return module != null;
    }

    /**
     * Get the qualified name
     * @return qualified name
     */
    public String getQualifiedName() {
        if (namespace != null) {
            return namespace + ":" + name;
        } else {
            return name;
        }
    }

    public ModuleName getModuleName() {
        return new ModuleName(module, namespace);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QualifiedName name1 = (QualifiedName) o;

        return !(name != null ? !name.equals(name1.name) : name1.name != null) &&
                !(namespace != null ? !namespace.equals(name1.namespace) : name1.namespace != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (namespace != null ? namespace.hashCode() : 0);
        return result;
    }
}
