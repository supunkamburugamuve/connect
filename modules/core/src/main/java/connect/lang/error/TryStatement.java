package connect.lang.error;

import connect.error.ConnectError;
import connect.error.ErrorFactory;
import connect.lang.ExecutionContext;
import connect.lang.MessageExecutionContext;
import connect.lang.RunningException;
import connect.lang.Statement;
import connect.lang.control.BlockStatement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * An statement to catch a error and handle it
 */
public class TryStatement implements Statement {
    private Log log = LogFactory.getLog(TryStatement.class);

    private List<CatchStatement> catchStatements = new ArrayList<CatchStatement>();

    private BlockStatement block = null;

    public void addCatchStatement(CatchStatement catchStatement) {
        catchStatements.add(catchStatement);
    }

    public void setBlock(BlockStatement block) {
        this.block = block;
    }

    public void execute(ExecutionContext context) throws RunningException {
        if (!(context instanceof MessageExecutionContext)) {
            String msg = "A message should be present to execute the statement";
            log.error(msg);
            throw new RunningException(msg);
        }
        // execute the block
        try {
            block.execute(context);
        } catch (RunningException e) {
            context.raiseError(ErrorFactory.create(e));
        }

        // check weather we have an error
        if (!context.isError()) {
            return;
        }

        // we are in error
        // go through all the catch blocks and find weather a matching error handler is found
        for (CatchStatement cs : catchStatements) {
            ConnectError e = context.getError();

            if (cs.isMatch(e)) {
                // this will clear the error
                cs.execute(context);
            }
        }
    }
}
