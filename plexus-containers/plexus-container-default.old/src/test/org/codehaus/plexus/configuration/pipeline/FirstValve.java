package org.codehaus.plexus.configuration.pipeline;


/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class FirstValve
    implements Valve
{
    public String getId()
    {
        return "first";
    }
}
