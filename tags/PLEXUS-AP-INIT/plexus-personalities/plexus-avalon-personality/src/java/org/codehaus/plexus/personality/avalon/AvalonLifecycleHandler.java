package org.codehaus.plexus.personality.avalon;

import org.codehaus.plexus.lifecycle.AbstractLifecycleHandler;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.component.repository.ComponentRepository;

/** An Avalon component lifecycle handler.
 *
 *  The <code>AvalonLifecycleHandler</code> must have the following entities
 *  set in order to propery execute the Avalon lifecycle.
 *
 *  Logger
 *  Context
 *  ServiceManager
 *
 *  @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 *
 *  @version $Id$
 *
 *  @todo need suspendSegment/resumeSegment facilities.
 */
public class AvalonLifecycleHandler
    extends AbstractLifecycleHandler
{
    /** */
    public static String SERVICE_MANAGER = "component.manager";

    // ----------------------------------------------------------------------
    //  Constructors
    // ----------------------------------------------------------------------

    public AvalonLifecycleHandler()
    {
        super();
    }

    public void initialize()
        throws Exception
    {
        ComponentRepository cr = (ComponentRepository) getEntities().get( LifecycleHandler.SERVICE_REPOSITORY );

        System.out.println( "cr = " + cr );

        addEntity( SERVICE_MANAGER, new AvalonServiceManager( cr ) );
    }
}
