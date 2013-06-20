package connect.xml.function;

import connect.function.AbstractFunction;
import connect.function.FunctionContext;
import connect.lang.ModuleContext;
import connect.lang.RunningException;
import connect.lang.Variable;
import connect.message.Message;
import connect.xml.XMLMediationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.util.List;

public class ValidateFunction extends AbstractFunction {
    private static Log log = LogFactory.getLog(ValidateFunction.class);
    /**
     * The SchemaFactory used to parse new schema instances.
     */
    private final SchemaFactory factory = SchemaFactory.newInstance(
            XMLConstants.W3C_XML_SCHEMA_NS_URI);

    private final String []params = new String[]{connect.xml.XMLConstants.PARAM_ELEMENT,
            connect.xml.XMLConstants.PARAM_SCHEMAS};

    public void execute(FunctionContext context) throws RunningException {

        Message message = context.getMessage();
        Document payload = (Document) message.getPayload();

        // first get the parameter element
        Variable element = context.getParameter(params[0]);

        Node node = null;
        // we have to operate on the whole message
        if (element == null) {
            node = payload;
        } else if (element.getValue() instanceof Node) {
            node = (Node) element.getValue();
        } else {
            String msg = "Unknown type specified for the element parameter in XSLT function: "
                    + element.getValue();
            log.error(msg);
            throw new XMLMediationException(msg);
        }

        Variable schemas = context.getParameter(connect.xml.XMLConstants.PARAM_SCHEMAS);
        if (schemas == null) {
            String msg = invalidParameterMessage(connect.xml.XMLConstants.PARAM_SCHEMAS);
            log.error(msg);
            throw new XMLMediationException(msg);
        }

        Object o = schemas.getValue();
        if (!(o instanceof List)) {
            String msg = invalidParameterMessage(connect.xml.XMLConstants.PARAM_SCHEMAS);
            log.error(msg);
            throw new XMLMediationException(msg);
        }

        List schemaList = (List) o;
        DOMSource sources[] = new DOMSource[schemaList.size()];
        for (int i = 0; i < schemaList.size(); i++) {
            if (schemaList.get(i) instanceof Node) {
                Node n = (Node) schemaList.get(i);
                sources[i] = new DOMSource(n);
            } else {
                String msg = invalidParameterMessage(connect.xml.XMLConstants.PARAM_SCHEMAS);
                log.error(msg);
                throw new XMLMediationException(msg);
            }
        }

        try {
            Schema schema = factory.newSchema(sources);
            Validator validator = schema.newValidator();

            Source source = new DOMSource(node);
            // This is the reference to the DefaultHandler instance
            ValidateMediatorErrorHandler errorHandler = new ValidateMediatorErrorHandler();
            validator.setErrorHandler(errorHandler);

            // perform actual validation
            validator.validate(source);

            if (errorHandler.isValidationError()) {
                context.setReturnValue(Boolean.FALSE);
                String msg = "Validation failed for schema " + errorHandler.getSaxParseException();
                if (log.isDebugEnabled()) {
                    log.debug(msg);
                }
            } else {
                context.setReturnValue(Boolean.TRUE);
            }
        } catch (SAXException e) {
            String msg = "Failed to parse the schema.";
            log.error(msg);
            throw new XMLMediationException(msg);
        } catch (IOException e) {
            String msg = "Failed to validate the using the schema.";
            log.error(msg);
            throw new XMLMediationException(msg);
        }
    }

    public void init(ModuleContext baseContext) throws RunningException {
        super.init(baseContext);
        name = connect.xml.XMLConstants.VALIDATE_FUNCTION;
    }
    /**
     * This class handles validation errors to be used for the error reporting
     */
    private static class ValidateMediatorErrorHandler extends DefaultHandler {

        private boolean validationError = false;
        private SAXParseException saxParseException = null;

        public void error(SAXParseException exception) throws SAXException {
            validationError = true;
            saxParseException = exception;
        }

        public void fatalError(SAXParseException exception) throws SAXException {
            validationError = true;
            saxParseException = exception;
        }

        public void warning(SAXParseException exception) throws SAXException {
        }

        public boolean isValidationError() {
            return validationError;
        }

        public SAXParseException getSaxParseException() {
            return saxParseException;
        }
    }
}
