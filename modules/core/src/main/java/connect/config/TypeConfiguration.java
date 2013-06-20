package connect.config;

import connect.lang.Module;
import connect.lang.VariableMapper;

import java.util.List;

/**
 * Holds the configuration related to a type.
 */
public interface TypeConfiguration {
    /**
     * Return the message type
     *
     * @return the message type
     */
    String getType();

    /**
     * Return the modules registered for this Message type
     *
     * @return the message type
     */
    List<Module> getModules();

    /**
     * Get the type specific variable mappers
     *
     * @return the variable mappers
     */
    List<VariableMapper> getVariableMappers();
}
