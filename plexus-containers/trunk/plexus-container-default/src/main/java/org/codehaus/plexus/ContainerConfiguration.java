package org.codehaus.plexus;

import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.composition.ComponentComposerManager;
import org.codehaus.plexus.component.discovery.ComponentDiscovererManager;
import org.codehaus.plexus.component.factory.ComponentFactoryManager;
import org.codehaus.plexus.component.manager.ComponentManagerManager;
import org.codehaus.plexus.component.repository.ComponentRepository;
import org.codehaus.plexus.container.initialization.ContainerInitializationPhase;

import java.net.URL;
import java.util.Map;

/**
 * @author Jason van Zyl
 */
public interface ContainerConfiguration
{
    ContainerConfiguration setName( String name );

    String getName();

    ContainerConfiguration setContext( Map context );

    Map getContext();

    ContainerConfiguration setClassWorld( ClassWorld classWorld );

    ClassWorld getClassWorld();

    ContainerConfiguration setParentContainer( PlexusContainer container );

    PlexusContainer getParentContainer();

    ContainerConfiguration setContainerConfiguration( String configuration );

    String getContainerConfiguration();

    ContainerConfiguration setContainerConfigurationURL( URL configuration );

    URL getContainerConfigurationURL();

    ContainerConfiguration setRealm( ClassRealm realm );

    ClassRealm getRealm();

    // Programmatic Container Initialization and Setup

    ContainerInitializationPhase[] getInitializationPhases();

    ComponentLookupManager getComponentLookupManager();

    ContainerConfiguration setComponentDiscovererManager( ComponentDiscovererManager componentDiscovererManager );

    ComponentDiscovererManager getComponentDiscovererManager();

    ContainerConfiguration setComponentFactoryManager( ComponentFactoryManager componentFactoryManager );

    ComponentFactoryManager getComponentFactoryManager();

    ContainerConfiguration setComponentManagerManager( ComponentManagerManager componentManagerManager );        

    ComponentManagerManager getComponentManagerManager();

    ContainerConfiguration setComponentRepository( ComponentRepository componentRepository );

    ComponentRepository getComponentRepository();

    ContainerConfiguration setComponentComposerManager( ComponentComposerManager componentComposerManager );

    ComponentComposerManager getComponentComposerManager();
}

