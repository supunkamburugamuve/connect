package connect.executor;

import connect.Namable;
import connect.lang.ManagedEntity;

public interface MessageExecutor extends ManagedEntity, Namable {
    void execute(Runnable r);
}
