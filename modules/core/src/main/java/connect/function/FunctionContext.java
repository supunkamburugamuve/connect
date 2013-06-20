package connect.function;

import connect.lang.MessageExecutionContext;
import connect.lang.Variable;
import connect.message.Message;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains the function parameters.
 */
public class FunctionContext extends MessageExecutionContext {
    private Map<String, Variable> parameters = new HashMap<String, Variable>();

    public FunctionContext(Message message, MessageExecutionContext context) {
        super(message, context, context.getFlow());
    }

    public void addParameter(Variable v) {
        parameters.put(v.getName(), v);
    }

    public Variable getParameter(String name) {
        return parameters.get(name);
    }
}
