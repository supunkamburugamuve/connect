package connect.lang.stmts;

import connect.error.ErrorFactory;
import connect.error.ErrorTypes;
import connect.lang.ExecutionContext;
import connect.lang.RunningException;
import connect.lang.Statement;
import connect.lang.Variable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Assign the value to a variable by executing a statement
 */
public class AssignmentStatement implements Statement {
    private Log log = LogFactory.getLog(AssignmentStatement.class);

    /** The variable to assign the return value */
    private Variable variable;

    /** This statement is executed and the return value is assigned to the variable */
    private Statement statement;

    public void execute(ExecutionContext context) throws RunningException {
        if (statement == null || variable == null) {
            throw new IllegalStateException("Variable or the statement cannot be null");
        }

        statement.execute(context);

        // return immediately on error
        if (context.isError()) {
            String msg = "Error occurred while executing the expression: ";
            log.error(msg + statement);
            return;
        }

        Object retVal = context.getReturnValue();
        if (retVal == null) {
            String msg = "Assignment statement requires the right hand side to return a value ";
            context.raiseError(ErrorFactory.create(ErrorTypes.EXPECTED_VALUE_NOT_FOUND, msg));
            log.error(msg + statement);
            return;
        }

        variable.setValue(retVal);

        context.setVariable(variable);
    }

    public void setVariable(Variable variable) {
        this.variable = variable;
    }

    public void setStatement(Statement statement)  {
        this.statement = statement;
    }
}
