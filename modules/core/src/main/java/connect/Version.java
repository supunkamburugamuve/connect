package connect;

/**
 * Represent the version of an entity
 */
public class Version {
    private final int major;

    private final int minor;

    public Version() {
        this(0, 0);
    }

    public Version(int major, int minor) {
        this.major = major;
        this.minor = minor;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    @Override
    public String toString() {
        return major + "." + minor;
    }
}
