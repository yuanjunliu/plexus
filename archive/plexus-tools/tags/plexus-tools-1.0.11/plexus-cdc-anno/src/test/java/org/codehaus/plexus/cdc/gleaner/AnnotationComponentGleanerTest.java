/*
 * Copyright (C) 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.plexus.cdc.gleaner;

import java.util.List;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Configuration;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.repository.cdc.ComponentDescriptor;
import org.codehaus.plexus.component.repository.cdc.ComponentRequirement;
import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * Tests for the {@link AnnotationComponentGleaner} class.
 *
 * @version $Rev$ $Date$
 */
public class AnnotationComponentGleanerTest
    extends PlexusTestCase
{
    private AnnotationComponentGleaner gleaner;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        gleaner = new AnnotationComponentGleaner();
    }

    @Override
    protected void tearDown() throws Exception {
        gleaner = null;

        super.tearDown();
    }

    private static class NoAnnotationsClass {}

    public void testNoAnnotationsClass() throws Exception {
        ComponentDescriptor component = gleaner.glean(NoAnnotationsClass.class);
        assertNull(component);
    }

    private static abstract class AbstractClass {}

    public void testAbstractClass() throws Exception {
        ComponentDescriptor component = gleaner.glean(AbstractClass.class);
        assertNull(component);
    }

    @Component(role=AbstractWithAnnoClass.class)
    private static abstract class AbstractWithAnnoClass {}

    public void testAbstractWithAnnoClass() throws Exception {
        ComponentDescriptor component = gleaner.glean(AbstractWithAnnoClass.class);
        assertNull(component);
    }

    private static interface NoAnnotationsIntf {}
    
    public void testNoAnnotationsIntf() throws Exception {
        ComponentDescriptor component = gleaner.glean(NoAnnotationsIntf.class);
        assertNull(component);
    }

    @Component(role=ChildComponent.class)
    private static class ChildComponent {}

    @Component(role=MyComponent.class, hint="foo")
    private static class MyComponent
    {
        @Requirement
        private ChildComponent child;

        @Configuration("bar")
        private String foo;
    }

    public void testMyComponent() throws Exception {
        ComponentDescriptor component = gleaner.glean(MyComponent.class);
        assertNotNull(component);

        assertEquals(MyComponent.class.getName(), component.getRole());
        assertEquals("foo", component.getRoleHint());

        List requirements = component.getRequirements();
        assertNotNull(requirements);
        assertEquals(1, requirements.size());

        ComponentRequirement requirement = (ComponentRequirement) requirements.get(0);
        assertNotNull(requirement);
        assertEquals(ChildComponent.class.getName(), requirement.getRole());

        PlexusConfiguration config = component.getConfiguration();
        assertNotNull(config);
        assertEquals(1, config.getChildCount());

        PlexusConfiguration child = config.getChild(0);
        assertNotNull(child);
        assertEquals("foo", child.getName());
        assertEquals("bar", child.getValue());
    }
}
