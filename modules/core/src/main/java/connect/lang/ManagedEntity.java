package connect.lang;

public interface ManagedEntity {
    void init(ModuleContext baseContext) throws RunningException;

    void destroy() throws RunningException;
}
