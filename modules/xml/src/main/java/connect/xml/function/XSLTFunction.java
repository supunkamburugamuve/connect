package connect.xml.function;

import connect.error.ConnectError;
import connect.error.ErrorFactory;
import connect.error.ErrorTypes;
import connect.function.FunctionContext;
import connect.function.AbstractFunction;
import connect.function.Parameter;
import connect.lang.ModuleContext;
import connect.lang.RunningException;
import connect.lang.Variable;
import connect.message.Message;
import connect.xml.XMLConstants;
import connect.xml.error.XMLErrorTypes;
import connect.xml.util.ResourceMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import java.util.Hashtable;
import java.util.Map;

/**
 * This is a primitive function of the XMLDom based mediation. It will take input as a
 * XSLT and XML and return the transformed XML. We will keep the function semantic simple and consistent.
 */
public class XSLTFunction extends AbstractFunction {
    private static Log log = LogFactory.getLog(XSLTFunction.class);

    /**
     * Define the allowed parameters.
     */
    private String []params = new String[]{XMLConstants.PARAM_ELEMENT, XMLConstants.PARAM_XSLT};

    /**
     * The transformation factory
     */
    private final TransformerFactory transFact = TransformerFactory.newInstance();

    /**
     * The resource map to be used for resolving imports
     */
    private ResourceMap resourceMap;

    /**
     * Cache multiple templates
     * Unique string used as a key for each template
     * The Template instance used to parse a Transformer object. This is  thread-safe
     */
    private Map<String, Templates> cachedTemplatesMap = new Hashtable<String, Templates>();

    public void execute(FunctionContext context) throws RunningException {
        Node node;
        Node xslt;

        Message message = context.getMessage();
        Element payload = (Element) message.getPayload();

        // first get the parameter element
        Variable element = context.getParameter(params[0]);

        // we have to operate on the whole message
        if (element == null) {
            node = payload;
        } else if (element.getValue() instanceof Node) {
            node = (Node) element.getValue();
        } else {
            String msg = "Unknown type specified for the element parameter in XSLT function: "
                    + element.getValue();
            log.error(msg);
            ConnectError error = ErrorFactory.create(ErrorTypes.UNEXPECTED_TYPE, msg);
            context.raiseError(error);
            return;
        }

        // now fetch the xslt
        Variable xsltNode = context.getParameter(params[1]);
        if (xsltNode == null || !(xsltNode.getValue() instanceof Node)) {
            String msg = "Failed to retrieve the XSLT Node parameter. Please specify a valid XSLT";
            log.error(msg);
            ConnectError error = ErrorFactory.create(ErrorTypes.REQUIRED_PARAM_MISSING, msg);
            context.raiseError(error);
            return;
        }

        xslt = (Node) xsltNode.getValue();

        Source xmlSource = new DOMSource(node);
        Source xsltSource = new DOMSource(xslt);

        // parse an instance of TransformerFactory
        Transformer trans;
        try {
            DocumentBuilderFactory dbf =
                    DocumentBuilderFactory.newInstance();
            Document resultDoc = dbf.newDocumentBuilder().newDocument();
            // apply the xslt to the payload
            DOMResult result = new DOMResult(resultDoc);

            trans = transFact.newTransformer(xsltSource);
            //trans.setErrorListener(new ErrorListenerImpl("Node transform"));

            trans.transform(xmlSource, result);

            // set the result value
            context.setReturnValue(result.getNode());
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
            String msg = "Failed to do the XSLT transformation";
            log.error(msg, e);
            ConnectError error = ErrorFactory.create(e, XMLErrorTypes.XSLT_TRANSFORM_CONFIGURATION_ERROR, msg);
            context.raiseError(error);
        } catch (TransformerException e) {
            e.printStackTrace();
            String msg = "Failed to do the XSLT transformation";
            log.error(msg, e);
            ConnectError error = ErrorFactory.create(e, XMLErrorTypes.XSLT_TRANSFORM_ERROR, msg);
            context.raiseError(error);
        } catch (ParserConfigurationException e) {
            String msg = "Failed to do the XSLT transformation";
            log.error(msg, e);
            ConnectError error = ErrorFactory.create(e, XMLErrorTypes.XSLT_TRANSFORM_ERROR, msg);
            context.raiseError(error);
        }
    }

    public void init(ModuleContext configuration) throws RunningException {
        super.init(configuration);
        Parameter p = new Parameter(XMLConstants.PARAM_ELEMENT, true);
        addParameter(p);
        p = new Parameter(XMLConstants.PARAM_XSLT);
        addParameter(p);
    }

    private static class ErrorListenerImpl implements ErrorListener {
        private final String activity;

        public ErrorListenerImpl(String activity) {
            this.activity = activity;
        }

        public void warning(TransformerException e) throws TransformerException {
            log.warn("Warning encountered during " + activity + " : " + e);
        }

        public void error(TransformerException e) throws TransformerException {
            log.error("Error occurred in " + activity + " : " + e);
            throw e;
        }

        public void fatalError(TransformerException e) throws TransformerException {
            log.error("Fatal error occurred in " + activity + " : " + e);
            throw e;
        }
    }
}
