package connect.lang.imports;

import connect.ModuleName;
import connect.QualifiedName;
import connect.lang.ModuleContext;
import connect.lang.Module;
import connect.lang.Reference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ModuleImport implements Import{
    private Log log = LogFactory.getLog(ModuleImport.class);

    private ModuleName moduleName;

    /**
     * Create a module import. Import all the entities from the module.
     * @param moduleName name of the module
     */
    public ModuleImport(ModuleName moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * Execute the import with respect to the base context. This will populate the base context with the objects
     *
     * @param baseContext module context
     */
    public Object get(ModuleContext baseContext, Reference reference) {
        return get(baseContext, reference, null);
    }

    public Object get(ModuleContext baseContext, Reference reference, String messageType) {
        Module m;
        if (messageType == null) {
            m = baseContext.getConfiguration().getModule(moduleName);
        } else {
            m = baseContext.getConfiguration().getModule(moduleName, messageType);
        }

        if (m == null) {
            log.debug("Entity import without the module in the configuration: " + moduleName);
            return null;
        }

        if (reference.isQualified()) {
            QualifiedName qualifiedName = reference.getQualifiedName();

            // both namespace and module should match
            if (!m.getName().equals(qualifiedName.getModuleName())) {
                return null;
            }

            if (m.isExists(qualifiedName.getName())) {
                return m.getEntity(qualifiedName.getName());
            }
        } else {
            if (m.isExists(reference.getName())) {
                return m.getEntity(reference.getName());
            }
        }
        return null;
    }
}
