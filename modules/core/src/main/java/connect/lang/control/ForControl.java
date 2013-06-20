package connect.lang.control;

import connect.error.ConnectError;
import connect.error.ErrorFactory;
import connect.error.ErrorTypes;
import connect.lang.*;
import connect.lang.MessageExecutionContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public class ForControl implements Statement {
    private Log log = LogFactory.getLog(ForControl.class);
    /** Name of the variable */
    private String variableName;
    /** This produce the list of objects to iterate through */
    private Statement list;
    /** The set of statements to execute */
    private BlockStatement executionBlock;

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public void setList(Statement list) {
        this.list = list;
    }

    public void setExecutionBlock(BlockStatement executionBlock) {
        this.executionBlock = executionBlock;
    }

    public void execute(ExecutionContext context) throws RunningException {
        if (!(context instanceof MessageExecutionContext)) {
            String msg = "A message should be present to execute the statement";
            log.error(msg);
            throw new RunningException(msg);
        }

        if (log.isDebugEnabled()) {
            log.debug("Executing the for loop with variable: " + variableName + " and expression: " +
                    list);
        }

        try {
            list.execute(context);
        } catch (RunningException e) {
            String msg = "Error executing the statement for For control: " + list;
            log.error(msg);
        }

        if (context.isError()) {
            return;
        }

        Object l = context.getReturnValue();
        if (!(l instanceof List)) {
            // raise error and immediately return
            String msg = "Expecting a list of objects to iterate";
            ConnectError error = ErrorFactory.create(ErrorTypes.UNEXPECTED_OBJECT, msg);
            context.raiseError(error);

            log.error(msg);
            return;
        }

        List objects = (List) l;

        // parse a context
        MessageExecutionContext newContext = MessageExecutionContextFactory.create((MessageExecutionContext) context);

        for (Object o : objects) {
            newContext.setVariable(new Variable(variableName, o));
            executionBlock.execute(newContext);

            // if there is an error set break immediately
            if (newContext.isError()) {
                break;
            }
        }
    }
}
