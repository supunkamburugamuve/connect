package connect.lang.imports;

import connect.QualifiedName;
import connect.lang.ModuleContext;
import connect.lang.Module;
import connect.lang.Reference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Import only the specified item.
 */
public class EntityImport implements Import {
    private Log log = LogFactory.getLog(EntityImport.class);
    private QualifiedName name;

    public EntityImport(QualifiedName name) {
        this.name = name;
    }

    public Object get(ModuleContext baseContext, Reference reference) {
        return get(baseContext, reference, null);
    }

    public Object get(ModuleContext baseContext, Reference reference, String messageType) {
        if (reference.isQualified()) {
            if (reference.getQualifiedName().equals(name)) {
                Module m;
                if (messageType == null) {
                    m = baseContext.getConfiguration().getModule(reference.getQualifiedName().getModuleName());
                } else {
                    m = baseContext.getConfiguration().getModule(reference.getQualifiedName().getModuleName(), messageType);
                }
                if (m != null) {
                    return m.getEntity(reference.getQualifiedName().getName());
                } else {
                    log.debug("Entity import without a proper module in the configuration: " + name);
                }
            } else {
                return null;
            }
        } else {
            if (name.getName().equals(reference.getName())) {
                Module m;
                if (messageType == null) {
                    m = baseContext.getConfiguration().getModule(name.getModuleName());
                } else {
                    m = baseContext.getConfiguration().getModule(name.getModuleName());
                }

                if (m != null) {
                    return m.getEntity(reference.getName());
                } else {
                    log.debug("Entity import without a proper module in the configuration: " + name);
                }
            }
        }
        return null;
    }
}
