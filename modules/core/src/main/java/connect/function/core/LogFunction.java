package connect.function.core;

import connect.function.AbstractFunction;
import connect.function.CoreFunctionConstants;
import connect.function.FunctionContext;
import connect.lang.ModuleContext;
import connect.lang.RunningException;
import connect.lang.Variable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LogFunction extends AbstractFunction {
    private Log log = LogFactory.getLog(LogFunction.class);

    public static final String PARAM_LEVEL = "level";

    public static final String PARAM_MESSAGE = "message";

    public static final String LOG_LEVEL_INFO = "info";
    public static final String LOG_LEVEL_DEBUG = "debug";
    public static final String LOG_LEVEL_ERROR = "error";
    public static final String LOG_LEVEL_FATAL = "fatal";

    private String[] params = new String[] {PARAM_MESSAGE, PARAM_LEVEL};

    public LogFunction() {
        name = CoreFunctionConstants.LOG_FUNCTION;
    }

    public void execute(FunctionContext context) throws RunningException {
        // get the next
        Variable p = context.getParameter(PARAM_LEVEL);
        String level;
        if (p == null || p.getValue() == null) {
            level = LOG_LEVEL_INFO;
        } else {
            level = (String) p.getValue();
        }

        // get the message
        Variable messageVar = context.getParameter(PARAM_MESSAGE);
        Object message;
        if (messageVar == null || messageVar.getValue() == null) {
            message = context.getMessage();
        } else {
            // get the current message
            message = messageVar.getValue();
        }

        if (level.equals(LOG_LEVEL_DEBUG)) {
            log.debug(message.toString());
        } else if (level.equals(LOG_LEVEL_INFO)) {
            log.info(message.toString());
        } else if (level.equals(LOG_LEVEL_ERROR)) {
            log.error(message.toString());
        }else if (level.equals(LOG_LEVEL_FATAL)) {
            log.fatal(message.toString());
        }

    }

    public void init(ModuleContext configuration) throws RunningException {
        super.init(configuration);
        addParameters(params);
    }
}
