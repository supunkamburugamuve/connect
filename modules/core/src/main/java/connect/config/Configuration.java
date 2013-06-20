package connect.config;

import connect.ConnectException;
import connect.ModuleName;
import connect.lang.Module;
import connect.lang.VariableMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The main configuration store.
 *
 * A Configuration consists of System Configuration, configurations specific to message
 * types and the configuration specified by the user.
 */
public class Configuration {
    private static Log log = LogFactory.getLog(Configuration.class);
    /**
     * The system configuration. This includes functions that are not specific to any Message type
     * and available across all the configurations.
     */
    private final SystemConfiguration systemConfiguration;

    /**
     * The configurations done for specific message types.
     */
    private Map<String, TypeConfiguration> typeConfigurations = new HashMap<String, TypeConfiguration>();

    /**
     * The configuration done for a user configuration. This includes setting up end to end flows
     * using different configurations.
     */
    private UserConfiguration userConfiguration;

    public Configuration(SystemConfiguration systemConfiguration) {
        this.systemConfiguration = systemConfiguration;
    }

    public void setUserConfiguration(UserConfiguration userConfiguration) {
        this.userConfiguration = userConfiguration;
    }

    public void addTypeConfiguration(String type, TypeConfiguration typeConfiguration) {
        typeConfigurations.put(type, typeConfiguration);
    }

    public void init() throws ConnectException {
        try {
            systemConfiguration.init(this);

            for (TypeConfiguration typeConfiguration : typeConfigurations.values()) {
                for (Module m : typeConfiguration.getModules()) {
                    m.init(this);
                }
            }

            userConfiguration.init(this);
        } catch (Exception e) {
            String msg = "Failed to initialize the configurations...";
            log.error(msg, e);
            throw new ConnectException(msg, e);
        }
    }

    public void destroy() throws ConnectException {
        try {
            systemConfiguration.destroy();

            for (TypeConfiguration typeConfiguration : typeConfigurations.values()) {
                for (Module m : typeConfiguration.getModules()) {
                    m.destroy();
                }
            }

            userConfiguration.destroy();
        } catch (Exception e) {
            String msg = "Failed to initialize the configurations...";
            log.error(msg, e);
            throw new ConnectException(msg, e);
        }
    }

    public void start() throws ConnectException {
        systemConfiguration.start();

        for (TypeConfiguration typeConfiguration : typeConfigurations.values()) {
            for (Module m : typeConfiguration.getModules()) {
                m.start();
            }
        }

        userConfiguration.start();
    }

    public void stop() throws ConnectException {
        systemConfiguration.start();

        for (TypeConfiguration typeConfiguration : typeConfigurations.values()) {
            for (Module m : typeConfiguration.getModules()) {
                m.stop();
            }
        }

        userConfiguration.start();
    }

    public List<Module> getModules(String namespace) {
        // first check system config
        if (namespace.equals(systemConfiguration.getNamespace())) {
            return systemConfiguration.getModules();
        }

        List<Module> retMods = new ArrayList<Module>();

        // then check the type modules
        for (TypeConfiguration typeConfiguration : typeConfigurations.values()) {
            List<Module> modules = typeConfiguration.getModules();

            for (Module m : modules) {
                if (m.getName().getNamespace().equals(namespace)) {
                    retMods.add(m);
                }
            }
        }

        // the check the user modules
        List<Module> userModules = userConfiguration.getModules(namespace);
        retMods.addAll(userModules);

        return retMods;
    }

    public Module getModule(ModuleName name) {
        // first check system config
        if (name.getNamespace() != null && systemConfiguration.getNamespace() != null) {
            if (name.getNamespace().equals(systemConfiguration.getNamespace())) {
                return systemConfiguration.getModule(name);
            }
        }

        // then check the type modules
        for (TypeConfiguration typeConfiguration : typeConfigurations.values()) {
            List<Module> modules = typeConfiguration.getModules();

            for (Module m : modules) {
                if (m.getName().equals(name)) {
                    return m;
                }
            }
        }

        // the check the user modules
        return userConfiguration.getModule(name);
    }

    public List<Module> getModules(String namespace, String type) {
        // first check system config
        if (namespace.equals(systemConfiguration.getNamespace())) {
            return systemConfiguration.getModules();
        }

        List<Module> retMods = new ArrayList<Module>();

        // then check the type modules
        for (TypeConfiguration typeConfiguration : typeConfigurations.values()) {
            if (!typeConfiguration.getType().equals(type)) {
                continue;
            }

            List<Module> modules = typeConfiguration.getModules();

            for (Module m : modules) {
                if (m.getName().getNamespace().equals(namespace)) {
                    retMods.add(m);
                }
            }
        }

        // the check the user modules
        List<Module> userModules = userConfiguration.getModules(namespace);
        retMods.addAll(userModules);

        return retMods;
    }

    public Module getModule(ModuleName name, String type) {
        // first check system config
        if (name.getNamespace() != null && systemConfiguration.getNamespace() != null) {
            if (name.getNamespace().equals(systemConfiguration.getNamespace())) {
                return systemConfiguration.getModule(name);
            }
        }

        // then check the type modules
        for (TypeConfiguration typeConfiguration : typeConfigurations.values()) {
            if (!typeConfiguration.getType().equals(type)) {
                continue;
            }

            List<Module> modules = typeConfiguration.getModules();

            for (Module m : modules) {
                if (m.getName().equals(name)) {
                    return m;
                }
            }
        }

        // the check the user modules
        return userConfiguration.getModule(name);
    }

    public VariableMapper getVariableMapper(String v, String type) {
        VariableMapper mapper = userConfiguration.getVariableMapper(v);

        if (mapper != null) return mapper;

        for (TypeConfiguration typeConfiguration : typeConfigurations.values()) {
            if (!typeConfiguration.getType().equals(type)) {
                continue;
            }

            List<VariableMapper> mappers = typeConfiguration.getVariableMappers();

            if (mappers == null) continue;

            for (VariableMapper m : mappers)  {
                if (m.getName().equals(v)) {
                    return m;
                }
            }
        }
        mapper = systemConfiguration.getVariableMapper(v);
        return mapper;
    }

    public VariableMapper getVariableMapper(String v) {
        VariableMapper mapper = userConfiguration.getVariableMapper(v);

        if (mapper != null) return mapper;

        for (TypeConfiguration typeConfiguration : typeConfigurations.values()) {
            List<VariableMapper> mappers = typeConfiguration.getVariableMappers();

            if (mappers == null) continue;

            for (VariableMapper m : mappers)  {
                if (m.getName().equals(v)) {
                    return m;
                }
            }
        }
        mapper = systemConfiguration.getVariableMapper(v);
        return mapper;
    }
}
