package connect.xml.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A resource map.
 *
 * Instances of this class are used to resolve resources using registry entries.
 * This is useful for XML documents that can reference other documents (e.g. WSDL documents
 * importing XSD or other WSDL documents). A <code>ResourceMap</code> object contains a set of
 * (location, registry key) mappings. The <code>resolve</code> method can be used to
 * get retrieve the registry entry registered for a given location as an {@link org.xml.sax.InputSource}
 * object.
 */
public class ResourceMap {
    private static final Log log = LogFactory.getLog(ResourceMap.class);

    private final Map<String,String> resources = new LinkedHashMap<String,String>();

    /**
     * Add a resource.
     *
     * @param location the location as it appears in referencing documents
     * @param key the registry key that points to the referenced document
     */
    public void addResource(String location, String key) {
        resources.put(location, key);
    }

    /**
     * Get the (location, registry key) mappings.
     *
     * @return a map containing the (location, registry key) pairs
     */
    public Map<String,String> getResources() {
        return Collections.unmodifiableMap(resources);
    }

    /**
     * Resolve a resource for a given location.
     *
     * @param location the location of of the resource at is appears in the referencing document
     * @return an <code>InputSource</code> object for the referenced resource
     */
    public InputSource resolve(String location) {
        String key = resources.get(location);
        if (key == null) {
            if (log.isDebugEnabled()) {
                log.debug("No resource mapping is defined for location '" + location + "'");
            }
            return null;
        } else {
            return null;
        }
    }
}

