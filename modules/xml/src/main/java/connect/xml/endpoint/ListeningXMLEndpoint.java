package connect.xml.endpoint;

import connect.endpoint.AbstractEndpoint;
import connect.message.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ListeningXMLEndpoint extends AbstractEndpoint {
    private static Log log = LogFactory.getLog(ListeningXMLEndpoint.class);

    public ListeningXMLEndpoint(String name) {
        super(log, name);
    }

    @Override
    public void handleInput(Message m) {
        forwardMessage(m);
    }
}
