package connect.lang.stmts;

import connect.lang.ExecutionContext;
import connect.lang.RunningException;
import connect.lang.Statement;
import connect.lang.Variable;

public class ValueStatement implements Statement {
    private final Object value;

    public ValueStatement(Object value) {
        this.value = value;
    }

    public void execute(ExecutionContext context) throws RunningException {
        if (value instanceof Variable) {
            context.setReturnValue(((Variable) value).getValue());
        } else {
            context.setReturnValue(value);
        }
    }

    public Object getValue() {
        return value;
    }
}
