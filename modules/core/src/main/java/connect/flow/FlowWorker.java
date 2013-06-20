package connect.flow;

import connect.ConnectConstants;
import connect.ConnectRunningException;
import connect.lang.*;
import connect.message.ErrorMessage;
import connect.message.ErrorMessageFactory;
import connect.message.Message;
import connect.queue.LinkQueue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public class FlowWorker implements Runnable {
    private Log log = LogFactory.getLog(FlowWorker.class);

    /** Name of the flow that invoked this worker */
    private Flow flow;

    /** The statements to be executed */
    private List<Statement> statements;

    /** The message itself */
    private Message message;

    public FlowWorker(Flow flow, List<Statement> statements, Message m) {
        // this should be used to parse the new context
        this.flow = flow;
        this.statements = statements;
        this.message = m;
    }

    public void run() {
        // parse an execution context
        MessageExecutionContext flowContext = MessageExecutionContextFactory.create(flow, message);

        // update the trace
        message.getTrace().addEntity(ConnectConstants.EntityTypes.FLOW, flow.getQualifiedName());

        // prepare tge global variables
        if (log.isDebugEnabled()) {
            log.debug("Start executing the flow: " + flow.getName() + " ");
        }

        for (Statement s : statements) {
            try {
                s.execute(flowContext);
                // we have an unhandled error propagate to this level
                if (flowContext.isError()) {
                    Message originalMessage = flowContext.getMessage();
                    // get the error handler and execute
                    ErrorMessage errorMessage = ErrorMessageFactory.create(
                            flowContext.getMessage(), flowContext.getError());
                                // get the queue that is responsible for handling the error
                    handleError(originalMessage, errorMessage);
                    break;
                }
            } catch (RunningException e) {
                String msg = "Error executing the statement: " + s;
                log.error(msg);
                Message originalMessage = flowContext.getMessage();
                // error has propagated up to this level
                // get the error handler and execute
                ErrorMessage errorMessage = ErrorMessageFactory.create(
                        flowContext.getMessage(), e, msg);

                handleError(originalMessage, errorMessage);
                break;
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("End executing the flow: " + flow.getName() + " ");
        }
    }

    /**
     * Send a message back to the last error receiver along the path.
     *
      * @param originalMessage original message
     * @param errorMessage error message
     */
    private void handleError(Message originalMessage, Message errorMessage) {
        boolean found = false;
        Object o;
        LinkQueue<Message> nextQueue;

        do {
            // get the queue that is responsible for handling the error
            Reference errorQueue = originalMessage.getErrorStack().pop();

            // fetch the queue using the reference
            try {
                o = flow.getBaseContext().getEntity(errorQueue);

                if (o instanceof LinkQueue) {
                    nextQueue = (LinkQueue<Message>) o;
                    nextQueue.offer(message);
                    found = true;
                } else {
                    log.error("The entity referred by " + errorQueue + " is not a Queue");
                }
            } catch (RunningException e) {
                log.error("Cannot find the queue to put the error message, fallback " +
                        "to the next error queue: " + errorQueue);
            }
        } while (originalMessage.getErrorStack().size() > 0 && !found);

        if (!found) {
            String msg = "Cannot find a queue to put the error message.. this cannot happen..";
            log.fatal(msg);
            throw new ConnectRunningException(msg);
        }
    }
}
