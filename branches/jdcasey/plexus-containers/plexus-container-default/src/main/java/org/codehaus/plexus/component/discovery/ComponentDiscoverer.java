package org.codehaus.plexus.component.discovery;

import java.util.List;

import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.context.Context;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public interface ComponentDiscoverer
{
    static String ROLE = ComponentDiscoverer.class.getName();

    void setManager( ComponentDiscovererManager manager );

    List findComponents( Context context, ClassLoader classLoader )
        throws PlexusConfigurationException;
}
