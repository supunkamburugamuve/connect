package connect.lang;

import connect.QualifiedName;

/**
 * There can be many references. For example a File reference. A WSDL in a URL.
 * All these can be references. There are message flow specific references as well.
 * They have to be registered and implemented by the flow implementations.
 */
public class Reference {
    private final String name;

    private final QualifiedName qualifiedName;

    public Reference(String name) {
        this.name = name;
        this.qualifiedName = null;
    }

    public Reference(connect.QualifiedName qualifiedName) {
        this.qualifiedName = qualifiedName;
        this.name = null;
    }

    public String getName() {
        return name;
    }

    public QualifiedName getQualifiedName() {
        return qualifiedName;
    }

    public boolean isQualified() {
        return qualifiedName != null;
    }

    public static Reference parse(String queue) {
        return null;
    }
}
