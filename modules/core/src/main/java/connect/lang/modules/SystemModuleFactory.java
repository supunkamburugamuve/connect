package connect.lang.modules;

import connect.ModuleName;
import connect.function.core.ConnectFunction;
import connect.function.core.ErrorQueueFunction;
import connect.function.core.LogFunction;
import connect.lang.Module;

/**
 * We parse the default modules here
 */
public class SystemModuleFactory {
    public static Module createSystemModule() {
        Module module = new Module(new ModuleName("system", "connect"));

        ConnectFunction connectFunction = new ConnectFunction();
        LogFunction logFunction = new LogFunction();
        ErrorQueueFunction errorQueueFunction = new ErrorQueueFunction();

        module.addEntity(connectFunction.getName(), connectFunction);
        module.addEntity(errorQueueFunction.getName(), errorQueueFunction);
        module.addEntity(logFunction.getName(), logFunction);

        return module;
    }
}
