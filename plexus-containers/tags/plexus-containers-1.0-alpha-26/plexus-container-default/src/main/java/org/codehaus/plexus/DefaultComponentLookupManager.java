package org.codehaus.plexus;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.manager.UndefinedComponentManagerException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.lifecycle.UndefinedLifecycleHandlerException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Jason van Zyl
 * @author Kenney Westerhof
 */
public class DefaultComponentLookupManager
    implements MutableComponentLookupManager
{
    private MutablePlexusContainer container;

    // ----------------------------------------------------------------------
    // Component Lookup
    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------
    // Try to lookup the component manager for the requested component.
    //
    // component manager exists:
    // -> return a component from the component manager.
    //
    // component manager doesn't exist;
    // -> lookup component descriptor for the requested component.
    // -> instantiate component manager for this component.
    // -> track the component manager for this component by the component class name.
    // -> return a component from the component manager.
    // ----------------------------------------------------------------------

    public Object lookup( String componentRole )
        throws ComponentLookupException
    {
        return lookup( componentRole, container.getLookupRealm() );
    }

    public Object lookup( String componentRole, ClassRealm realm )
        throws ComponentLookupException
    {
        return lookup( componentRole, PlexusConstants.PLEXUS_DEFAULT_HINT, realm );
    }

    public Object lookup( Class componentClass )
        throws ComponentLookupException
    {
        return lookup( componentClass.getName(), container.getLookupRealm() );
    }

    public Object lookup( Class componentClass, ClassRealm realm )
        throws ComponentLookupException
    {
        return lookup( componentClass.getName(), realm );
    }

    // ----------------------------------------------------------------------------
    // Role + Hint
    // ----------------------------------------------------------------------------

    public Object lookup( Class role, String roleHint, ClassRealm realm )
        throws ComponentLookupException
    {
        return lookup( role.getName(), roleHint, realm );
    }

    public Object lookup( String role, String roleHint )
        throws ComponentLookupException
    {
        return lookup( role, roleHint, container.getLookupRealm() );
    }

    public Object lookup( Class role, String roleHint )
        throws ComponentLookupException
    {
        return lookup( role.getName(), roleHint );
    }

    public Object lookup( String componentRole, String roleHint, ClassRealm realm )
        throws ComponentLookupException
    {
        Object component;

        if ( roleHint == null )
        {
            roleHint = PlexusConstants.PLEXUS_DEFAULT_HINT;
        }

        ComponentManager componentManager = container.getComponentManagerManager()
            .findComponentManagerByComponentKey( componentRole, roleHint, realm );

        // The first time we lookup a component a component manager will not exist so we ask the
        // component manager manager to create a component manager for us. Also if we are reloading
        // components then we'll also get a new component manager.

        if ( container.isReloadingEnabled() || componentManager == null )
        {
            ComponentDescriptor descriptor = container.getComponentRepository().getComponentDescriptor( componentRole,
                                                                                                        roleHint,
                                                                                                        realm );

            if ( descriptor == null )
            {
                if ( container.getParentContainer() != null )
                {
                    return container.getParentContainer().lookup( componentRole, roleHint, realm );
                }

                String message = "Component descriptor cannot be found in the component repository: " + componentRole
                    + " [" + roleHint + "]" + " (lookup realm: " + realm + ").";

                throw new ComponentLookupException( message );
            }

            componentManager = createComponentManager( descriptor, componentRole, roleHint );
        }

        try
        {
            component = componentManager.getComponent( realm );
        }
        catch ( ComponentInstantiationException e )
        {
            throw new ComponentLookupException( "Unable to lookup component '" + componentRole
                + "', it could not be created", e );
        }
        catch ( ComponentLifecycleException e )
        {
            throw new ComponentLookupException( "Unable to lookup component '" + componentRole
                + "', it could not be started", e );
        }

        container.getComponentManagerManager().associateComponentWithComponentManager( component, componentManager );

        return component;
    }

    // ----------------------------------------------------------------------------
    // Maps
    // ----------------------------------------------------------------------------

    public Map lookupMap( String role )
        throws ComponentLookupException
    {
        return lookupMap( role, container.getLookupRealm() );
    }

    public Map lookupMap( Class role, ClassRealm realm )
        throws ComponentLookupException
    {
        return lookupMap( role.getName(), realm );
    }

    /**
     * Return a Map of components for a given role keyed by the component role hint.
     *
     * @todo Change this to include components looked up from parents as well...
     */
    public Map lookupMap( String role, ClassRealm realm )
        throws ComponentLookupException
    {
        Map components = new HashMap();

        Map componentDescriptors = container.getComponentDescriptorMap( role, realm );

        if ( componentDescriptors != null )
        {
            for ( Iterator i = componentDescriptors.keySet().iterator(); i.hasNext(); )
            {
                String roleHint = (String) i.next();

                Object component = lookup( role, roleHint, realm );

                components.put( roleHint, component );
            }
        }

        return components;
    }

    public Map lookupMap( Class role )
        throws ComponentLookupException
    {
        return lookupMap( role.getName() );
    }

    // ----------------------------------------------------------------------------
    // Lists
    // ----------------------------------------------------------------------------

    /**
     * @deprecated
     */
    public List lookupList( String role )
        throws ComponentLookupException
    {
        return lookupList( role, container.getLookupRealm() );
    }

    public List lookupList( Class role )
        throws ComponentLookupException
    {
        return lookupList( role.getName(), container.getLookupRealm() );
    }

    public List lookupList( Class role, ClassRealm realm )
        throws ComponentLookupException
    {
        return lookupList( role.getName(), realm );
    }

    /**
     * Return a List of components for a given role.
     *
     * @todo Change this to include components looked up from parents as well...
     */
    public List lookupList( String role, ClassRealm realm )
        throws ComponentLookupException
    {
        List components = new ArrayList();

        List componentDescriptors = container.getComponentDescriptorList( role, realm );

        if ( componentDescriptors != null )
        {
            for ( Iterator i = componentDescriptors.iterator(); i.hasNext(); )
            {
                ComponentDescriptor descriptor = (ComponentDescriptor) i.next();

                String roleHint = descriptor.getRoleHint();

                Object component = lookup( role, roleHint, realm );

                components.add( component );
            }
        }

        return components;
    }

    // ----------------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------------

    public void setContainer( MutablePlexusContainer container )
    {
        this.container = container;
    }

    // ----------------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------------

    private ComponentManager createComponentManager( ComponentDescriptor descriptor, String role, String roleHint )
        throws ComponentLookupException
    {
        ComponentManager componentManager;

        try
        {
            componentManager = container.getComponentManagerManager().createComponentManager( descriptor,
                                                                                              container,
                                                                                              role, roleHint );
        }
        catch ( UndefinedComponentManagerException e )
        {
            String message = "Cannot create component manager for " + descriptor.getRole() + " ["
                + descriptor.getRoleHint() + "], so we cannot provide a component instance.";

            throw new ComponentLookupException( message, e );
        }
        catch ( UndefinedLifecycleHandlerException e )
        {
            String message = "Cannot create component manager for " + descriptor.getRole() + " ["
                + descriptor.getRoleHint() + "], so we cannot provide a component instance.";

            throw new ComponentLookupException( message, e );
        }

        return componentManager;
    }
}
