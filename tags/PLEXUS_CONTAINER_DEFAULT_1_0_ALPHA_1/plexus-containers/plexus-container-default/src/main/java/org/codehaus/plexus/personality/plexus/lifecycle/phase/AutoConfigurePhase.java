package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;

/**
 * @todo (michal) should this phase be called only for components which
 *  does not implement Configurable interface?
 */
public class AutoConfigurePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws Exception
    {
        ComponentConfigurator componentConfigurator =
            (ComponentConfigurator) manager.getContainer().lookup( ComponentConfigurator.ROLE );

        //if (object instanceof Configurable)

        if ( manager.getComponentDescriptor().hasConfiguration() )
        {
            ComponentDescriptor descriptor = manager.getComponentDescriptor();

            componentConfigurator.configureComponent( object, descriptor, manager.getComponentDescriptor().getConfiguration() );
        }
    }
}
