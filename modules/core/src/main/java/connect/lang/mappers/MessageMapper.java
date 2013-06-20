package connect.lang.mappers;

import connect.lang.ExecutionContext;
import connect.lang.MessageExecutionContext;
import connect.lang.Variable;
import connect.lang.VariableMapper;

import java.net.ConnectException;

public class MessageMapper implements VariableMapper {
    public static final String VARIABLE_MESSAGE = "msg";

    public String getName() {
        return VARIABLE_MESSAGE;
    }

    public Variable get(ExecutionContext context, String name) {
        if (name.equals(VARIABLE_MESSAGE)) {
            if (context instanceof MessageExecutionContext) {
                return new Variable(VARIABLE_MESSAGE, context.getMessage().getPayload());
            }
        }
        return null;
    }

    public void update(ExecutionContext context, Variable v) {
        if (v.getName().equals(VARIABLE_MESSAGE)) {
            if (context instanceof MessageExecutionContext) {
                try {
                    context.getMessage().setPayload(v.getValue());
                } catch (ConnectException e) {

                }
            }
        }
    }
}
