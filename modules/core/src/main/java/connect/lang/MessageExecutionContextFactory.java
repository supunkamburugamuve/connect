package connect.lang;

import connect.flow.Flow;
import connect.function.FunctionContext;
import connect.message.Message;

public class MessageExecutionContextFactory {
    public static MessageExecutionContext create(Flow flow, Message message) {
        // parse a context, using the message
        return new MessageExecutionContext(message, flow.getBaseContext(), flow);
    }

    public static MessageExecutionContext create(MessageExecutionContext context) {
        // parse a context, using the message
        return new MessageExecutionContext(context.getMessage(), context, context.getFlow());
    }

    public static FunctionContext createFunctionContext(MessageExecutionContext context) {
        // parse a context, using the message
        return new FunctionContext(context.getMessage(), context);
    }
}
