package connect.env;

import connect.ConnectException;
import connect.config.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class ExecutionEnvironment {
    private Log log = LogFactory.getLog(ExecutionEnvironment.class);

    /**
     * Ths main configuration
     */
    private Configuration configuration;

    /**
     * Create an execution environment with the default system configurator
     */
    public ExecutionEnvironment(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * Initialize the environment
     */
    public void init() throws ConnectException {
        configuration.init();
    }

    public void start() throws ConnectException {
        configuration.start();
    }

    public void stop() throws ConnectException {
        configuration.stop();
    }

    /**
     * Destroy the environment
     */
    public void destroy() throws ConnectException {
        try {
            configuration.destroy();
        } catch (Exception e) {
            String msg = "Failed to destroy the configurations..";
            log.error(msg);
            throw new ConnectException(msg, e);
        }
    }

    /**
     * Get the configuration for this environment
     * @return the configuration
     */
    public Configuration getConfiguration() {
        return configuration;
    }
}
