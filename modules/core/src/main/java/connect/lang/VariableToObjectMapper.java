package connect.lang;

public interface VariableToObjectMapper {
    String getName();

    void update(ExecutionContext context, Variable v);
}
