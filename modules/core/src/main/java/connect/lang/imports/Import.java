package connect.lang.imports;

import connect.lang.ModuleContext;
import connect.lang.Reference;

/**
 * Represent an import. Once executed will import the entities in to the module.
 */
public interface Import {
    /**
     * Imports the entities to the context
     *
     * @param baseContext context under which this runs
     */
    public Object get(ModuleContext baseContext, Reference name);

    public Object get(ModuleContext baseContext, Reference name, String messageType);
}

