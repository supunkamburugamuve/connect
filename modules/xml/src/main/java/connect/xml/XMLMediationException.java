package connect.xml;

import connect.lang.RunningException;

/**
 * This exception is thrown by the XML related entities
 */
public class XMLMediationException extends RunningException {
    public XMLMediationException(String s) {
        super(s);
    }

    public XMLMediationException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
