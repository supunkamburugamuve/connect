package connect.lang;

public interface VariableMapper {
    String getName();

    Variable get(ExecutionContext context, String name);

    void update(ExecutionContext context, Variable v);
}
