package connect.lang.error;

import connect.error.ConnectError;
import connect.error.ErrorFactory;
import connect.error.ErrorTypes;
import connect.lang.ExecutionContext;
import connect.lang.MessageExecutionContext;
import connect.lang.RunningException;
import connect.lang.Statement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RaiseErrorStatement implements Statement {
    private Log log = LogFactory.getLog(RaiseErrorStatement.class);

    private Statement errorToRaise;

    public void setErrorToRaise(Statement errorToRaise) {
        this.errorToRaise = errorToRaise;
    }

    public void execute(ExecutionContext context) throws RunningException {
        if (!(context instanceof MessageExecutionContext)) {
            String msg = "A message should be present to execute the statement";
            log.error(msg);
            throw new RunningException(msg);
        }
        // get the error object
        errorToRaise.execute(context);

        // get the return value
        Object o = context.getReturnValue();

        if (o instanceof ConnectError) {
            context.raiseError((ConnectError) o);
        } else {
            String msg = "Unexpected object returned by the " +
                    "configuration, expecting an error object";
            // we are in error because we didn't get the expected object
            ConnectError e = ErrorFactory.create(ErrorTypes.UNEXPECTED_OBJECT, msg);
            context.raiseError(e);
            log.error(msg);
        }
    }
}
