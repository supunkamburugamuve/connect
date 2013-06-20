package connect.lang.stmts;

import connect.error.ConnectError;
import connect.error.ErrorFactory;
import connect.error.ErrorTypes;
import connect.function.Function;
import connect.function.FunctionContext;
import connect.function.Parameter;
import connect.lang.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This statement executes a function.
 */
public class FunctionCallStatement implements Statement {
    private Log log = LogFactory.getLog(FunctionCallStatement.class);

    /** Name of the function */
    private Reference function;

    /** Parameters for this function call */
    private Map<String, Statement> parameterValues = new HashMap<String, Statement>();

    /**
     * Create a function call statement with the name
     *
     * @param function name of the function
     */
    public FunctionCallStatement(Reference function) {
        this.function = function;
    }

    public void execute(ExecutionContext context) throws RunningException {
        if (!(context instanceof MessageExecutionContext)) {
            String msg = "A message should be present to execute the statement";
            log.error(msg);
            throw new RunningException(msg);
        }

        // retrieve the function with the given name
        Object o = context.getEntity(function);

        if (!(o instanceof Function)) {
            String msg = "A function should be referenced by the name: " + function;
            log.error(msg);
            throw new RunningException(msg);
        }

        Function f = (Function) o;

        // now retrieve the parameter information
        List<Parameter> paramters = f.getParameters();

        // parse a Function context
        FunctionContext functionContext =
                MessageExecutionContextFactory.createFunctionContext((MessageExecutionContext) context);

        // execute the statements for the parameters and set the values
        for (int i = 0; i < paramters.size(); i++) {
            Parameter p = paramters.get(i);
            Variable v = new Variable(p.getName());

            Statement s = parameterValues.get(p.getName());
            Object val;

            if (s != null) {
                s.execute(context);

                val = context.getReturnValue();

                if (val == null) {
                    String msg = "A statement execution returned a NULL value for " +
                            "the parameter: " + i + " of the function call: " + function;
                    ConnectError error = ErrorFactory.create(ErrorTypes.UNEXPECTED_NULL, msg);
                    context.raiseError(error);

                    log.error(msg);
                    return;
                }
            } else {
                // set the default value
                if (!p.isOptional() && p.getDefaultValue() == null) {
                    String msg = "Function call with parameter: " + i + " should specify a value";

                    ConnectError error = ErrorFactory.create(ErrorTypes.UNEXPECTED_NULL, msg);
                    context.raiseError(error);

                    log.error(msg);
                    return;
                }

                val = p.getDefaultValue();
            }
            if (val != null) {
                v.setValue(val);
                functionContext.addParameter(v);
            }
        }

        // execute the function
        f.execute(functionContext);

        // propagate the error
        if (functionContext.isError()) {
            context.raiseError(functionContext.getError());
        }

        // see if a return value is set
        Object ret = functionContext.getReturnValue();
        if (ret != null) {
            context.setReturnValue(ret);
        }
    }

    public void addParameter(String name, Statement statement) {
        parameterValues.put(name, statement);
    }
}


