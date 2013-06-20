package connect.function;

import connect.Namable;
import connect.lang.ManagedEntity;
import connect.lang.RunningException;

import java.util.List;

/**
 * A Function is a definition with parameters and a execution block.
 *
 * There are two kinds of functions. Java code based implementations and User configurations.
 *
 * When a function is called, the caller has to assign variables to the parameters.
 */
public interface Function extends ManagedEntity, Namable {
    void addParameter(Parameter parameter);

    List<Parameter> getParameters();

    void execute(FunctionContext context) throws RunningException;
}



