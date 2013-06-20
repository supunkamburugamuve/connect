package connect.xml.endpoint;

import connect.endpoint.AbstractEndpoint;
import connect.message.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SendingXMLEndpoint extends AbstractEndpoint {
    private static Log log = LogFactory.getLog(SendingXMLEndpoint.class);

    public SendingXMLEndpoint(String name) {
        super(log, name);
    }

    @Override
    public void handleInput(Message m) {
        forwardMessage(m);
    }
}
