package connect.function.core;

import connect.error.ConnectError;
import connect.error.ErrorFactory;
import connect.error.ErrorTypes;
import connect.function.AbstractFunction;
import connect.function.CoreFunctionConstants;
import connect.function.FunctionContext;
import connect.lang.*;
import connect.message.Message;
import connect.queue.LinkQueue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Send the message to the next entity, by placing it in a queue
 */
public class ConnectFunction extends AbstractFunction {
    private static Log log = LogFactory.getLog(ConnectFunction.class);

    public static final String PARAM_NEXT = "next";

    private String[] params = new String[] {PARAM_NEXT};

    public ConnectFunction() {
        name = CoreFunctionConstants.CONNECT_FUNCTION;
    }

    public void execute(FunctionContext context) throws RunningException {
        // get the next
        Variable p = context.getParameter(PARAM_NEXT);
        if (p == null || p.getValue() == null) {
            String msg = requiredParameterMessage(PARAM_NEXT);
            log.error(msg);
            throw new RunningException(msg);
        }
        Reference next = (Reference) p.getValue();

        // get the current message
        Message message = context.getMessage();

        /** This should come from the context/configuration */
        Object o = context.getEntity(next);
        if (!(o instanceof LinkQueue)) {
            String msg = "The connect should refer a queue object";
            log.error(msg);
            ConnectError error = ErrorFactory.create(ErrorTypes.UNEXPECTED_OBJECT, msg);
            context.raiseError(error);
            return;
        }

        // get the queue
        LinkQueue inputQueue = (LinkQueue) o;
        inputQueue.offer(message);
    }

    public void init(ModuleContext configuration) throws RunningException {
        super.init(configuration);
        addParameters(params);
    }
}
