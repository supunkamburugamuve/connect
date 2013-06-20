package connect.lang;

import connect.Namable;
import connect.function.Parameter;

import java.util.List;
import java.util.Map;

public interface ObjectCreator extends ManagedEntity, Namable {
    /**
     * Expected parameters
     * @return list of parameters
     * */
    List<Parameter> getParameters();

    /**
     * Create an object using the variables passed
     *
     * @param variables variable
     * @return the new object
     * @throws RunningException if an error occurs
     */
    Object create(Map<String, Variable> variables) throws RunningException;
}
