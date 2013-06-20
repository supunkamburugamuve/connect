package connect.xml.config;

import connect.ModuleName;
import connect.lang.Module;
import connect.xml.XMLConstants;
import connect.xml.function.ValidateFunction;
import connect.xml.function.XMLObjectCreator;
import connect.xml.function.XPathFunction;
import connect.xml.function.XSLTFunction;

public class XMLModuleCreator {
    public static Module createXMLModule() {
        Module xmlModule = new Module(new ModuleName(XMLConstants.XML_MODULE));

        xmlModule.addEntity(XMLConstants.VALIDATE_FUNCTION, new ValidateFunction());
        xmlModule.addEntity(XMLConstants.XPATH_FUNCTION, new XPathFunction());
        xmlModule.addEntity(XMLConstants.XSLT_FUNCTION, new XSLTFunction());

        xmlModule.addEntity(XMLConstants.XML_CREATOR, new XMLObjectCreator(XMLConstants.XML_CREATOR));

        return xmlModule;
    }
}
