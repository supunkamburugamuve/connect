package connect.xml.function;

import connect.function.AbstractFunction;
import connect.function.FunctionContext;
import connect.lang.ModuleContext;
import connect.lang.None;
import connect.lang.RunningException;
import connect.lang.Variable;
import connect.xml.XMLConstants;
import connect.xml.XMLMediationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.List;

public class XPathFunction extends AbstractFunction {
    private static Log log = LogFactory.getLog(XSLTFunction.class);

    /**
     * Define the allowed parameters.
     */
    private final String []params = new String[]{XMLConstants.PARAM_ELEMENT,
            XMLConstants.PARAM_EXPR};

    /**
     * The Xpath factory to be used
     */
    private final XPathFactory factory = XPathFactory.newInstance();


    public void execute(FunctionContext context) throws RunningException {
        XPath xpath = factory.newXPath();

        String expr;
        // get the xpath expression
        Variable p = context.getParameter(XMLConstants.PARAM_EXPR);
        if (p == null || p.getValue() == null) {
            String msg = requiredParameterMessage(XMLConstants.PARAM_EXPR);
            log.error(msg);
            throw new XMLMediationException(msg);
        }
        expr = p.getValue().toString();

        // set the return type to node
        QName returnType = XPathConstants.NODE;

        // get the return type if specified
        p = context.getParameter(XMLConstants.PARAM_XPATH_RETURN_TYPE);
        if (p != null && p.getValue() != null) {
            Object o = p.getValue();
            if (!(o instanceof String)) {
                String msg = invalidParameterMessage(XMLConstants.PARAM_XPATH_RETURN_TYPE);
                log.error(msg);
                throw new XMLMediationException(msg);
            }

            if (o.equals(XMLConstants.XPATH_RETURN_NODE)) {
                returnType = XPathConstants.NODE;
            } else if (o.equals(XMLConstants.XPATH_RETURN_NODESET)) {
                returnType = XPathConstants.NODESET;
            } else if (o.equals(XMLConstants.XPATH_RETURN_NUMBER)) {
                returnType = XPathConstants.NUMBER;
            } else if (o.equals(XMLConstants.XPATH_RETURN_STRING)) {
                returnType = XPathConstants.STRING;
            } else if (o.equals(XMLConstants.XPATH_RETURN_BOOLEAN)) {
                returnType = XPathConstants.BOOLEAN;
            } else {
                String msg = invalidParameterMessage(XMLConstants.PARAM_XPATH_RETURN_TYPE);
                log.error(msg);
                throw new XMLMediationException(msg);
            }
        }

        Node n = null;
        // get the node to operate
        p = context.getParameter(XMLConstants.PARAM_ELEMENT);
        if (p == null) {
            n = (Node) context.getMessage().getPayload();
        } else if (p.getValue() instanceof Node) {
            n = (Node) p.getValue();
        } else {
            String msg = invalidParameterMessage(XMLConstants.PARAM_ELEMENT);
            log.error(msg);
            throw new XMLMediationException(msg);
        }

        try {
            XPathExpression e = xpath.compile(expr);
            Object o = e.evaluate(n, returnType);
            if (o != null) {
                if (o instanceof NodeList) {
                    // parse a normal list
                    List<Node> nodeList = new ArrayList<Node>();
                    for (int i = 0; i < ((NodeList) o).getLength(); i++) {
                        nodeList.add(((NodeList) o).item(i));
                    }
                    context.setReturnValue(nodeList);
                } else {
                    // set as it is, because we are expecting String, Boolean or Node
                    context.setReturnValue(o);
                }
            } else {
                // if nothing, we set the special object
                context.setReturnValue(new None());
            }
        } catch (XPathExpressionException e) {
            String msg = "Invalid XPath expression: " + expr;
            log.error(msg);
            throw new XMLMediationException(msg, e);
        }
    }

    public void init(ModuleContext configuration) throws RunningException {
        super.init(configuration);
        name = XMLConstants.XPATH_FUNCTION;

        addParameters(params);
    }
}
