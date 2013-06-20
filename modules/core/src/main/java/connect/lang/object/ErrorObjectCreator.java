package connect.lang.object;

import connect.ConnectConstants;
import connect.error.ErrorFactory;
import connect.lang.AbstractObjectCreator;
import connect.lang.ModuleContext;
import connect.lang.RunningException;
import connect.lang.Variable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

public class ErrorObjectCreator extends AbstractObjectCreator {
    private Log log = LogFactory.getLog(ErrorObjectCreator.class);

    /**
     * Define the allowed parameters.
     */
    private String []params = new String[]{"msg", "type"};

    /**
     * Create an object creator with the default name "error"
     *
     */
    protected ErrorObjectCreator() {
        super(ConnectConstants.ERROR_OBJECT_CREATOR);
    }

    /**
     * Create an object creator with a given name
     *
     */
    protected ErrorObjectCreator(String name) {
        super(name);
    }



    public Object create(Map<String, Variable> variables) throws RunningException {
        // go through the variables
        String message;
        int type = -1;

        Variable v = variables.get(params[0]);
        Object o = v.getValue();

        if (o instanceof String) {
            message = (String) o;
        } else {
            String msg = "Unexpected object for the 'msg' param: Expecting a String";
            log.error(msg);
            throw new RunningException(msg);
        }

        v = variables.get(params[0]);
        o = v.getValue();

        if (o instanceof Integer) {
            type = (Integer) o;
        } else {
            String msg = "Unexpected object for the 'type' param: Expecting a Integer";
            log.error(msg);
            throw new RunningException(msg);
        }

        return ErrorFactory.create(type, message);
    }

    public void init(ModuleContext baseContext) throws RunningException {
        super.init(baseContext);
        addParameters(params);
    }
}
