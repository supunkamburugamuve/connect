package connect.lang.error;

import connect.error.ConnectError;
import connect.lang.ExecutionContext;
import connect.lang.MessageExecutionContext;
import connect.lang.RunningException;
import connect.lang.Statement;
import connect.lang.control.BlockStatement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CatchStatement implements Statement {
    private Log log = LogFactory.getLog(CatchStatement.class);

    private int type;

    private BlockStatement block;

    public CatchStatement(int type) {
        this.type = type;
    }

    public CatchStatement(int type, BlockStatement block) {
        this.type = type;
        this.block = block;
    }

    public void execute(ExecutionContext context) throws RunningException {
        if (!(context instanceof MessageExecutionContext)) {
            String msg = "A message should be present to execute the statement";
            log.error(msg);
            throw new RunningException(msg);
        }
        // first we have to clear the error
        context.clearError();

        // then we will execute the block
        block.execute(context);
        // return immediately after raising the error
        if (context.isError()) {
            log.error("Exception occurred while executing the catch statement");
        }
    }

    public void setBlock(BlockStatement block) {
        this.block = block;
    }

    public boolean isMatch(ConnectError e) {
        return e.getType() == type;
    }
}
