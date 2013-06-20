package connect.lang;

import connect.*;
import connect.config.Configuration;
import connect.lang.imports.Import;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * Contains function definitions, queue definitions, bridges, and flow definitions
 */
public class Module implements ManagedLifeCycle {
    private Log log = LogFactory.getLog(Module.class);

    /** The set of imports to this module */
    private List<Import> imports = new ArrayList<Import>();

    /** Name of the module */
    private ModuleName name;

    /** Entities that are in this module */
    private Map<String, Object> entities = new HashMap<String, Object>();

    /** The state of the module */
    private State state = State.CREATED;

    /** These are static statements */
    private List<Statement> statements = new ArrayList<Statement>();

    /** The base context */
    private ModuleContext baseContext;

    /**
     * Create a module with the given name
     *
     * @param name name of the module
     */
    public Module(ModuleName name) {
        this.name = name;
    }

    /**
     * Add an entity to the module. After initialization cannot add entities dynamically
     * @param name name of the entity
     * @param entity entity to be added
     */
    public void addEntity(String name, Object entity) {
        if (state == State.CREATED) {
            entities.put(name, entity);
        } else {
            throw new IllegalStateException("Cannot add an entity " +
                    "after the module is initialized: " + name);
        }
    }

    /**
     * Get all the entities that are in this module
     *
     * @return all the entities
     */
    public Collection<Object> getEntities() {
        return entities.values();
    }

    /**
     * Get an entity within a module
     * @param name name of the entity
     * @return entity
     */
    public Object getEntity(String name) {
        return entities.get(name);
    }

    /**
     * Check weather the item is in the module
     *
     * @param name name of the item
     * @return true if exists
     */
    public boolean isExists(String name) {
        return entities.keySet().contains(name);
    }

    /**
     * Name of the module
     *
     * @return name of the module
     */
    public ModuleName getName() {
        return name;
    }

    /**
     * Start the entities in the module
     */
    public void start() {
        for (Object o : entities.values()) {
            if (o instanceof Manageable) {
                ((Manageable) o).start();
            }
        }
    }

    /**
     * Stop the entities in the module
     */
    public void stop() {
        for (Object o : entities.values()) {
            if (o instanceof Manageable) {
                ((Manageable) o).stop();
            }
        }
    }

    /**
     * Execute the statements
     *
     * @throws RunningException if an error happens
     */
    public void execute() throws RunningException {
        if (state !=  State.INIT) {
            throw new IllegalStateException("The module should be init before executing the statements");
        }

        // execute the statements with the base context
        for (Statement s : statements) {
            s.execute(baseContext);

            if (baseContext.isError()) {
                String msg = "Failed to execute the statement: " + s + " " + baseContext.getError();
                log.error(msg);
                throw new RunningException(msg);
            }
        }
    }

    /**
     * Initialized the module by initializing the entities
     *
     * @param configuration configuration to initialize
     * @throws RunningException if initialization fails
     */
    public void init(Configuration configuration) throws RunningException {
        if (log.isDebugEnabled()) {
            log.debug("Initializing module ");
        }

        baseContext = new ModuleContext(configuration, this);

        // do the imports, this will add all the entities to the base context
        // this can be in-effiecient memory wise. so need to re-visit

        // initialize the objects
        for (Map.Entry<String, Object> e : entities.entrySet()) {
            Object o = e.getValue();
            if (o instanceof ManagedEntity) {
                ((ManagedEntity) o).init(baseContext);
            }
        }

        state = State.INIT;
    }

    public void destroy() throws RunningException {
        // initialize the objects
        for (Map.Entry<String, Object> e : entities.entrySet()) {
            Object o = e.getValue();
            if (o instanceof ManagedLifeCycle) {
                ((ManagedLifeCycle) o).destroy();
            }
        }
    }

    /**
     * Add an import to the module
     *
     * @param i import to be added
     */
    public void addImport(Import i) {
        imports.add(i);
    }

    /**
     * Add a statement to the module
     *
     * @param statement statement
     */
    public void addStatement(Statement statement) {
        statements.add(statement);
    }

    /**
     * Get the entity by first looking at its own entities and then going through the imports
     * @param reference reference
     * @return the object if found, otherwise null
     */
    public Object getEntity(Reference reference) {
        // first check weather we have it
        if (!reference.isQualified()) {
            if (isExists(reference.getName())) {
                return getEntity(reference.getName());
            }
        }

        for (Import i : imports) {
            Object o = i.get(baseContext, reference);
            if (o != null) return o;
        }

        return null;
    }

    /**
     * Get the entity by first looking at its own entities and then going through the imports
     * @param reference reference
     * @param messageType the type of the message we are working on
     * @return the object if found, otherwise null
     */
    public Object getEntity(Reference reference, String messageType) {
        // first check weather we have it
        if (!reference.isQualified()) {
            if (isExists(reference.getName())) {
                return getEntity(reference.getName());
            }
        }

        for (Import i : imports) {
            Object o = i.get(baseContext, reference, messageType);
            if (o != null) return o;
        }

        return null;
    }
}
