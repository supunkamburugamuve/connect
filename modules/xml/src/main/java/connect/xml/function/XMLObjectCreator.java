package connect.xml.function;

import connect.lang.*;
import connect.xml.XMLMediationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class XMLObjectCreator extends AbstractObjectCreator {
    private Log log = LogFactory.getLog(XMLObjectCreator.class);

    private DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

    /**
     * Define the allowed parameters.
     */
    private String []params = new String[]{"element"};

    /**
     * Create an object creator with a given name
     *
     * @param name name
     */
    public XMLObjectCreator(String name) {
        super(name);
    }

    public Object create(Map<String, Variable> variables) throws XMLMediationException {
        // go through the variables
        try {
            Variable v = variables.get(params[0]);

            if (v == null) {
                String msg = "element parameter for creating a XML object is necessary";
                log.error(msg);
                throw new XMLMediationException(msg);
            }

            Object o = v.getValue();
            documentBuilderFactory.setNamespaceAware(true);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            if (o instanceof File) {
                // Using factory get an instance of document builder
                return documentBuilder.parse((File) o).getDocumentElement();
            } else if (o instanceof InputStream) {
                return documentBuilder.parse((InputStream) o).getDocumentElement();
            } else if (o instanceof String) {
                System.out.println(o);
                return documentBuilder.parse(new ByteArrayInputStream(((String) o).getBytes())).getDocumentElement();
            } else {
                String msg = "Unexpected object for the element param: Expecting a InputStream, String or a File";
                log.error(msg);
                throw new XMLMediationException(msg);
            }
        } catch (ParserConfigurationException e) {
            String msg = "XML Parser configuration error";
            log.error(msg);
            throw new XMLMediationException(msg);
        } catch (SAXException e) {
            String msg = "XML Syntax error";
            log.error(msg);
            throw new XMLMediationException(msg);
        } catch (IOException e) {
            String msg = "IO Error while reading the message";
            log.error(msg);
            throw new XMLMediationException(msg);
        }
    }


    public void init(ModuleContext baseContext) throws RunningException {
        super.init(baseContext);
        addParameters(params);
    }
}
