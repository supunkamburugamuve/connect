package connect.lang.control;

import connect.error.ConnectError;
import connect.error.ErrorFactory;
import connect.error.ErrorTypes;
import connect.lang.*;
import connect.lang.MessageExecutionContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

public class IfControl implements Statement {
    private Log log = LogFactory.getLog(IfControl.class);
    /** The condition of the if statement */
    private List<Statement> conditions = new ArrayList<Statement>();
    /** The blocks to be executed */
    private List<BlockStatement> blocks = new ArrayList<BlockStatement>();

    private BlockStatement defaultBlock = null;

    public void addCondition(Statement condition, BlockStatement block) {
        conditions.add(condition);
        blocks.add(block);
    }

    public void addDefault(BlockStatement block) {
        defaultBlock = block;
    }

    public List<Statement> getConditions() {
        return conditions;
    }

    public List<BlockStatement> getBlocks() {
        return blocks;
    }

    public void execute(ExecutionContext context) throws RunningException {
        if (!(context instanceof MessageExecutionContext)) {
            String msg = "A message should be present to execute the statement";
            log.error(msg);
            throw new RunningException(msg);
        }

        BlockStatement st = null;

        // go through the conditions and see weather we have a match
        for (int i = 0; i < conditions.size(); i++) {
            conditions.get(i).execute(context);

            Object r = context.getReturnValue();
            if (!(r instanceof Boolean)) {
                // raise error and break
                String msg = "The condition should return a boolean value";
                ConnectError error = ErrorFactory.create(ErrorTypes.UNEXPECTED_OBJECT, msg);
                context.raiseError(error);

                log.error(msg);

                break;
            }

            Boolean b = (Boolean) r;
            if (b) {
                st = blocks.get(i);
            }
        }

        // try to see weather we have a default case
        if (st == null) {
            st = defaultBlock;
        }

        if (st != null) {
            // parse a context, using the message
            MessageExecutionContext newContext = MessageExecutionContextFactory.create((MessageExecutionContext) context);

            st.execute(newContext);
        }
    }
}
