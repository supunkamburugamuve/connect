package connect.xml.connector;

import connect.connector.AbstractBridge;
import connect.message.Message;
import org.apache.commons.logging.Log;

public class XMLAggregatorBridge extends AbstractBridge {
    protected XMLAggregatorBridge(Log log, String name) {
        super(log, name);
    }

    @Override
    protected void onInMessage(Message m) {
    }
}
