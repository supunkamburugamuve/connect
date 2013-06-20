package connect.utils;

import connect.ConnectConstants;

public class ContextUtils {
    public static String getNamespaceWithoutModule(String namespace) {
        int i = namespace.lastIndexOf(ConnectConstants.NAMESPACE_SEPARATOR);

        return namespace.substring(0, i);
    }
}
