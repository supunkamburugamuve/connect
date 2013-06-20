package connect.xml.message;

import connect.ConnectConstants;
import connect.env.ExecutionEnvironment;
import connect.message.AbstractMessage;
import connect.message.MessageTrace;
import connect.xml.XMLMediationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.net.ConnectException;

public class XMLMessage extends AbstractMessage {
    private Log log = LogFactory.getLog(XMLMessage.class);

    private Node payload = null;

    public XMLMessage(Element payload, ExecutionEnvironment environment, MessageTrace trace) {
        super(environment, ConnectConstants.MessageTypes.XML, trace);

        this.payload = payload;
        this.messageCloner = new XMLMessageCloner();
    }

    public XMLMessage(Node node, ExecutionEnvironment environment, MessageTrace trace)
            throws XMLMediationException {
        super(environment, ConnectConstants.MessageTypes.XML, trace);

        if (node instanceof Element) {
            this.payload = node;
        } else {
            String msg = "Expecting a document object";
            log.error(msg);
            throw new XMLMediationException(msg);
        }

        this.messageCloner = new XMLMessageCloner();
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object msg) throws ConnectException {
        if (msg instanceof Node) {
            payload = (Node) msg;
        } else {
            String msg1 = "Expecting a Node as the payload";
            log.error(msg1);
            throw new ConnectException(msg1);
        }
    }
}
