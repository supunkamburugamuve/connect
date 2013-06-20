package connect.xml.connector;

import connect.connector.ConversionException;
import connect.connector.MessageConverter;
import connect.message.Message;
import connect.xml.message.XMLMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

public class InputStreamToXMLConverter implements MessageConverter {
    private Log log = LogFactory.getLog(InputStreamToXMLConverter.class);

    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

    public Message transform(Message m) throws ConversionException {
        if (m.getPayload() instanceof InputStream) {
            // Using factory get an instance of document builder
            try {
                DocumentBuilder db = documentBuilderFactory.newDocumentBuilder();

                Document doc = db.parse("employees.xml");

//                return new XMLMessage(doc, m.environment());
            } catch (ParserConfigurationException e) {
                handleError("XML Parser configuration error", e);
            } catch (SAXException e) {
                handleError("XML Syntax error", e);
            } catch (IOException e) {
                handleError("IO Error while reading the message", e);
            }
        }
        return null;
    }

    /**
     * This converter is going to get activated if the incoming message is a XML one.
     *
     * @param m input message
     * @return true if the incoming message Contains a {@link InputStream}
     */
    public boolean isTriggered(Message m) {
        return m.getPayload() instanceof InputStream;
    }

    private void handleError(String msg, Exception e) throws ConversionException {
        log.error(msg, e);
        throw new ConversionException(msg, e);
    }
}
