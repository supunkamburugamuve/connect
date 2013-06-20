package connect.xml.message;

import connect.message.Message;
import connect.message.MessageCloner;
import connect.xml.XMLMediationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

public class XMLMessageCloner implements MessageCloner {
    private static Log log = LogFactory.getLog(XMLMessageCloner.class);

    public Message clone(Message m) throws XMLMediationException {
        if (!(m instanceof XMLMessage)) {
            String msg = "Expecting a XMLMessage to be cloned";
            log.error(msg);
            throw new XMLMediationException(msg);
        }

        try {
            Node payload = (Node) m.getPayload();

            TransformerFactory tfactory = TransformerFactory.newInstance();
            Transformer tx = tfactory.newTransformer();
            DOMSource source = new DOMSource(payload);
            DOMResult result = new DOMResult();
            tx.transform(source, result);

            Node resultNode = result.getNode();

            return new XMLMessage(resultNode, m.environment(), m.getTrace());
        } catch (TransformerException e) {
            String msg = "Failed to clone the XML Message with ID: " + m.getId();
            log.error(msg);
            throw new XMLMediationException(msg);
        }
    }
}
