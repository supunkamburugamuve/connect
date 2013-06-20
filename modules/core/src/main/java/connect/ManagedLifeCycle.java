package connect;

import connect.config.Configuration;
import connect.lang.RunningException;

public interface ManagedLifeCycle {
    public void init(Configuration configuration) throws RunningException;

    public void destroy() throws RunningException;
}
