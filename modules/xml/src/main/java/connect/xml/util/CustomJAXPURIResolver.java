package connect.xml.util;

import org.xml.sax.InputSource;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

/**
 * Class that adapts a {@link ResourceMap} to JAXP's {@link javax.xml.transform.URIResolver}.
 */
public class CustomJAXPURIResolver implements URIResolver {
    private final ResourceMap resourceMap;

    /**
     * Constructor.
     *
     * @param resourceMap the resource map; may be null if no resource map is configured
     */
    public CustomJAXPURIResolver(ResourceMap resourceMap) {
        this.resourceMap = resourceMap;
    }

    /**
     * Resolve an xsl:import or xsl:include.
     * This method will first attempt to resolve the location using the configured
     * {@link ResourceMap} object. If this fails (because no {@link ResourceMap} is
     */
    public Source resolve(String href, String base) throws TransformerException {
        Source result = null;
        if (resourceMap != null) {
            InputSource is = resourceMap.resolve(href);
            if (is != null) {
                result = new StreamSource(is.getByteStream());
            }
        }
        if (result == null) {
            result = new StreamSource(base + ":"  + href);
        }
        return result;
    }
}
