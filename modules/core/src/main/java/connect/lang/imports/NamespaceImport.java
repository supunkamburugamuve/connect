package connect.lang.imports;

import connect.QualifiedName;
import connect.lang.ModuleContext;
import connect.lang.Module;
import connect.lang.Reference;

import java.util.Collection;

public class NamespaceImport implements Import {
    private String namespace;

    public NamespaceImport(String namespace) {
        this.namespace = namespace;
    }

    public Object get(ModuleContext baseContext, Reference reference) {
        Collection<Module> modules = baseContext.getConfiguration().getModules(namespace);

        for (Module m : modules) {
            if (reference.isQualified()) {
                QualifiedName qualifiedName = reference.getQualifiedName();

                // both namespace and module should match
                if (!m.getName().getNamespace().equals(qualifiedName.getNamespace()) || !m.getName().getModule().equals(qualifiedName.getModule())) {
                    continue;
                }

                if (m.isExists(qualifiedName.getName())) {
                    return m.getEntity(qualifiedName.getName());
                }
            } else {
                if (m.isExists(reference.getName())) {
                    return m.getEntity(reference.getName());
                }
            }
        }
        return null;
    }

    public Object get(ModuleContext baseContext, Reference reference, String messageType) {
        Collection<Module> modules;
        if (messageType == null) {
            modules = baseContext.getConfiguration().getModules(namespace);
        } else {
            modules = baseContext.getConfiguration().getModules(namespace, messageType);
        }

        for (Module m : modules) {
            if (reference.isQualified()) {
                QualifiedName qualifiedName = reference.getQualifiedName();

                // both namespace and module should match
                if (!m.getName().getNamespace().equals(qualifiedName.getNamespace()) || !m.getName().getModule().equals(qualifiedName.getModule())) {
                    continue;
                }

                if (m.isExists(qualifiedName.getName())) {
                    return m.getEntity(qualifiedName.getName());
                }
            } else {
                if (m.isExists(reference.getName())) {
                    return m.getEntity(reference.getName());
                }
            }
        }
        return null;
    }
}
