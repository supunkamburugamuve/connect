package connect.lang;

import connect.ModuleName;
import connect.config.Configuration;
import connect.message.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This is the base context created and populated when the entity initialization.
 */
public class ModuleContext extends AbstractExecutionContext {
    private static Log log = LogFactory.getLog(ModuleContext.class);
    /** This is the configuration of the system */
    protected final Configuration configuration;

    /** The module that created the context */
    protected final Module module;

    public ModuleContext(Configuration configuration, Module module) {
        super(log, null);
        this.configuration = configuration;
        this.module = module;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public Variable getVariableValue(String variable) {
        Variable v = super.getVariableValue(variable);

        if (v != null) return v;

        VariableMapper mapper = configuration.getVariableMapper(variable);
        if (mapper != null) {
            v = mapper.get(this, variable);

            setVariable(v);
            return v;
        }
        return null;
    }

    public ModuleName getModuleName() {
        return module.getName();
    }

    public VariableMapper getVariableMapper(String v) {
        return configuration.getVariableMapper(v);
    }

    public void clearError() {
        inError = false;
    }

    public Message getMessage() {
        throw new IllegalAccessError("Cannot call the getMessage of module context");
    }

    public Object getEntity(Reference reference) throws RunningException {
        return module.getEntity(reference);
    }

    public Object getEntity(Reference reference, String messageType) throws RunningException {
        return module.getEntity(reference, messageType);
    }

}

