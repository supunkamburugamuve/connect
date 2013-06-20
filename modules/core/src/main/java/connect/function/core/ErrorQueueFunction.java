package connect.function.core;

import connect.function.AbstractFunction;
import connect.function.CoreFunctionConstants;
import connect.function.FunctionContext;
import connect.lang.ModuleContext;
import connect.lang.Reference;
import connect.lang.RunningException;
import connect.lang.Variable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * We use this function to set the current error queue.
 */
public class ErrorQueueFunction extends AbstractFunction {
    private static Log log = LogFactory.getLog(ErrorQueueFunction.class);

    public static final String ERROR_QUEUE = "queueName";

    private String[] params = new String[] {ERROR_QUEUE};

    public ErrorQueueFunction() {
        name = CoreFunctionConstants.ERROR_QUEUE_FUNCTION;
    }

    public void execute(FunctionContext context) throws RunningException {
        // get the next
        Variable p = context.getParameter(ERROR_QUEUE);
        String queue;
        if (p == null || p.getValue() == null) {
            String msg = requiredParameterMessage(ERROR_QUEUE);
            log.error(msg);
            throw new RunningException(msg);
        } else {
            queue = (String) p.getValue();
        }

        Reference name = Reference.parse(queue);

        context.getMessage().getErrorStack().push(name);

        context.setReturnValue(null);
    }

    public void init(ModuleContext configuration) throws RunningException {
        super.init(configuration);
        addParameters(params);
    }
}
