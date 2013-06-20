package connect.xml.config;

import connect.ConnectConstants;
import connect.config.TypeConfiguration;
import connect.lang.Module;
import connect.lang.VariableMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

public class XMLConfiguration implements TypeConfiguration {
    private Log log = LogFactory.getLog(XMLConfiguration.class);

    List<Module> list = new ArrayList<Module>();

    public XMLConfiguration() {
        list.add(XMLModuleCreator.createXMLModule());
    }

    public String getType() {
        return ConnectConstants.MessageTypes.XML;
    }

    public List<Module> getModules() {

        return list;
    }

    public List<VariableMapper> getVariableMappers() {
        return null;
    }
}
