package connect.lang.stmts;

import connect.error.ConnectError;
import connect.error.ErrorFactory;
import connect.error.ErrorTypes;
import connect.function.Parameter;
import connect.lang.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is an generic statement for creating a new object of a given type.
 */
public class NewObjectStatement implements Statement {
    private Log log = LogFactory.getLog(NewObjectStatement.class);

    /** This is the name of the object to be created */
    private final Reference name;

    /** The initial arguments needed to parse the object */
    private Map<String, Statement> parameterValues = new HashMap<String, Statement>();

    public NewObjectStatement(Reference name) {
        this.name = name;
    }

    public void execute(ExecutionContext executionContext) throws RunningException {
        Object o = executionContext.getEntity(name);
        try {
        if (!(o instanceof ObjectCreator)) {
            String msg = "Expecting an object creator instance reffered by the name: " + name;
            ConnectError error = ErrorFactory.create(ErrorTypes.UNEXPECTED_OBJECT, msg);
            executionContext.raiseError(error);
            return;
        }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        ObjectCreator creator = (ObjectCreator) o;

        List<Parameter> paramters = creator.getParameters();

        Map<String, Variable> variables = new HashMap<String, Variable>();
        // execute the statements for the parameters and set the values
        for (int i = 0; i < paramters.size(); i++) {
            Parameter p = paramters.get(i);

            Variable v = new Variable(p.getName());

            Statement s = parameterValues.get(p.getName());
            Object val;

            if (s != null) {
                s.execute(executionContext);

                val = executionContext.getReturnValue();

                if (val == null) {
                    String msg = "A statement execution returned a NULL value for " +
                            "the parameter: " + i + " of the object creation statement";
                    ConnectError error = ErrorFactory.create(ErrorTypes.UNEXPECTED_NULL, msg);
                    executionContext.raiseError(error);

                    log.error(msg);
                    return;
                }
            } else {
                // set the default value
                if (!p.isOptional() && p.getDefaultValue() == null) {
                    String msg = "Function call with parameter: " + i + " should specify a value or it " +
                            "should be declared optional";
                    ConnectError error = ErrorFactory.create(ErrorTypes.UNEXPECTED_NULL, msg);
                    executionContext.raiseError(error);

                    log.error(msg);
                    return;
                }

                val = p.getDefaultValue();
            }

            v.setValue(val);
            variables.put(p.getName(), v);
        }

        Object newObject;
        try {
            newObject = creator.create(variables);
        } catch (RunningException e) {
            String msg = "Object creator instance referred by the name: " + name + " failed to parse an object";
            ConnectError error = ErrorFactory.create(ErrorTypes.OBJECT_CREATION, msg);
            executionContext.raiseError(error);
            return;
        }

        if (newObject == null) {
            String msg = "Object creator instance referred by the name: " + name + " returned an null object";
            ConnectError error = ErrorFactory.create(ErrorTypes.UNEXPECTED_NULL, msg);
            executionContext.raiseError(error);
            return;
        }

        executionContext.setReturnValue(newObject);
    }

    public void addParameter(String name, Statement param) {
        parameterValues.put(name, param);
    }

    public void addParameter(String name, Variable variable) {
        parameterValues.put(name, new ValueStatement(variable));
    }
}
