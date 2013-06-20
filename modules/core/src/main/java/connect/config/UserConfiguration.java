package connect.config;

import connect.ModuleName;
import connect.lang.Module;
import connect.lang.RunningException;
import connect.lang.VariableMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserConfiguration {
    private Log log = LogFactory.getLog(UserConfiguration.class);

    /** Modules which contain different configurations */
    private Map<ModuleName, Module> modules = new HashMap<ModuleName, Module>();

    /**
     * The user specified variable mappers
     */
    private Map<String, VariableMapper> variableMappers = new HashMap<String, VariableMapper>();

    public UserConfiguration() {
    }

    /**
     * Get all the modules with the given namespace
     * @param namespace namespace
     * @return list of modules
     */
    public List<Module> getModules(String namespace) {
        List<Module> ms = new ArrayList<Module>();

        for (Map.Entry<ModuleName, Module> entry : modules.entrySet()) {
            if (entry.getKey().getNamespace().equals(namespace)) {
                ms.add(entry.getValue());
            }
        }

        return ms;
    }

    public Module getModule(ModuleName name) {
        return modules.get(name);
    }

    /**
     * Add a module with a given namespace
     *
     * @param m module
     */
    public void addModule(Module m) {
        modules.put(m.getName(), m);
    }

    public void addVariableMapper(VariableMapper v) {
        variableMappers.put(v.getName(), v);
    }

    public VariableMapper getVariableMapper(String v) {
        return variableMappers.get(v);
    }

    /**
     * Initialize the modules
     *
     * @throws Exception if the module initialization fails
     */
    public void init(Configuration configuration) throws Exception {
        // initialize the modules with namespaces
        for (Map.Entry<ModuleName, Module> me : modules.entrySet()) {
            initModule(me.getValue(), configuration);
        }
    }

    public void destroy() throws Exception {
        // destroy the modules with namespaces
        for (Map.Entry<ModuleName, Module> me : modules.entrySet()) {
            destroyModule(me.getValue());
        }
    }

    public void start() {
        for (Module m : modules.values()) {
            m.start();
        }
    }

    public void stop() {
        for (Module m : modules.values()) {
            m.stop();
        }
    }

    /**
     * Initialize the module
     * @param m module
     * @throws Exception if an error happens
     */
    private void initModule(Module m, Configuration configuration) throws Exception {
        try {
            m.init(configuration);
        } catch (RunningException e) {
            String msg = "Error initializing the module: " + m.getName();
            log.error(msg);
            throw new Exception(msg, e);
        }
    }

    /**
     * Destroy the module
     * @param m module
     * @throws Exception if an error happens
     */
    private void destroyModule(Module m) throws Exception {
        try {
            m.destroy();
        } catch (RunningException e) {
            String msg = "Error destroying the module: " + m.getName();
            log.error(msg);
            throw new Exception(msg, e);
        }
    }
}
