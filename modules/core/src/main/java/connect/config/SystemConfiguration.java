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

/**
 * The connected configuration. This has all the functional and declarative
 * components declared by the system.
 */
public class SystemConfiguration {
    private static Log log = LogFactory.getLog(SystemConfiguration.class);

    /** Default namespace */
    private final String namespace;

    /** These are the default typeModules that has to be present in all the places */
    private List<Module> systemModules = new ArrayList<Module>();

    /** The system level variable mappers */
    private Map<String, VariableMapper> variableMappers = new HashMap<String, VariableMapper>();

    public SystemConfiguration(String namespace) {
        this.namespace = namespace;
    }

    public List<Module> getModules() {
        return systemModules;
    }

    public Module getModule(ModuleName name) {
        if (name.getNamespace().equals(namespace)) {
            for (Module m : systemModules) {
                if (m.getName().getModule().equals(name.getModule())) {
                    return m;
                }
            }
        }
        return null;
    }


    public String getNamespace() {
        return namespace;
    }

    public VariableMapper getVariableMapper(String v) {
        return variableMappers.get(v);
    }

    public void addVariableMapper(VariableMapper v) {
        variableMappers.put(v.getName(), v);
    }

    /**
     * Add a module with a given namespace
     *
     * @param m module
     */
    public void addModule(Module m) {
        systemModules.add(m);
    }

    /**
     * Initialize the modules
     *
     * @throws Exception if the module initialization fails
     */
    public void init(Configuration configuration) throws Exception {
        // initialize the modules with namespaces
        for (Module m : systemModules) {
            initModule(m, configuration);
        }
    }

    public void destroy() throws Exception {
        // destroy the modules with namespaces
        for (Module m : systemModules) {
            destroyModule(m);
        }
    }

    public void start() {
        for (Module m : systemModules) {
            m.start();
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

