package connect.lang.control;

import connect.lang.ExecutionContext;
import connect.lang.MessageExecutionContext;
import connect.lang.RunningException;
import connect.lang.Statement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

public class BlockStatement implements Statement {
    private Log log = LogFactory.getLog(BlockStatement.class);
    /** List of statements to execute */
    private List<Statement> statements = new ArrayList<Statement>();

    public void addStatement(Statement st) {
        statements.add(st);
    }

    public void removeStatement(int index) {
        statements.remove(index);
    }

    public void execute(ExecutionContext context) throws RunningException {
        if (!(context instanceof MessageExecutionContext)) {
            String msg = "A message should be present to execute the statement";
            log.error(msg);
            throw new RunningException(msg);
        }
        // prepare tge global variables
        if (log.isDebugEnabled()) {
            log.debug("Executing the statements");
        }

        // execute each statement
        for (Statement s : statements) {
            s.execute(context);

            // check weather we are in error
            if (context.isError()) {
                // we have to get the error back immediately
                String msg = "Error executing the statement: " + s;
                log.error(msg);

                break;
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("Finished executing the blocks");
        }
    }
}
