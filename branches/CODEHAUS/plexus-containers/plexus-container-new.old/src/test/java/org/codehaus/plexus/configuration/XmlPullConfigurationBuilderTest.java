package org.codehaus.plexus.configuration;

import junit.framework.TestCase;
import org.apache.avalon.framework.configuration.Configuration;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

/**
 * Generated by JUnitDoclet, a tool provided by ObjectFab GmbH under LGPL.
 * Please see www.junitdoclet.org, www.gnu.org and www.objectfab.de for
 * informations about the tool, the licence and the authors.
 */
public class XmlPullConfigurationBuilderTest
    extends TestCase
{
    public XmlPullConfigurationBuilderTest( String name )
    {
        super( name );
    }

    /**
     * The JUnit setup method
     */
    protected void setUp()
        throws Exception
    {
    }

    /**
     * The teardown method for JUnit
     */
    protected void tearDown()
        throws Exception
    {
    }

    public void testSimpleConfigurationBuilding()
        throws Exception
    {
        String s = "<conf><name>jason</name></conf>";
        XmlPullConfigurationBuilder cb = new XmlPullConfigurationBuilder();
        Configuration c = cb.parse( new StringReader( s ) );

        assertNotNull( c );
        assertEquals( "jason", c.getChild( "name" ).getValue() );
    }

    public void testComplexConfigurationBuilding()
        throws Exception
    {
        InputStream is = XmlPullConfigurationBuilder.class.getResourceAsStream( "plexus.conf" );
        XmlPullConfigurationBuilder cb = new XmlPullConfigurationBuilder();
        Configuration c = cb.parse( new InputStreamReader( is ) );

        assertNotNull( c );

        Configuration logging = c.getChild( "logging" );
        assertNotNull( logging );
        assertEquals( "org.codehaus.plexus.logging.ConsoleLoggerManager",
                      logging.getChild( "implementation" ).getValue() );

        Configuration componentRepository = c.getChild( "component-repository" );
        assertNotNull( componentRepository );
        assertEquals( "org.codehaus.plexus.service.repository.DefaultComponentRepository",
                      componentRepository.getChild( "implementation" ).getValue() );

        Configuration resourceManager = c.getChild( "resource-manager" );
        assertNotNull( resourceManager );
        assertEquals( "org.codehaus.plexus.classloader.DefaultResourceManager",
                      resourceManager.getChild( "implementation" ).getValue() );

        Configuration lifecycleHandler = c.getChild( "lifecycle-handler" );
        assertNotNull( lifecycleHandler );
        assertEquals( lifecycleHandler.getChild( "id" ).getValue(), "avalon-lifecycle-handler" );
        assertEquals( lifecycleHandler.getChild( "name" ).getValue(), "Avalon Lifecycle Handler" );
        assertEquals( lifecycleHandler.getChild( "implementation" ).getValue(),
                      "org.codehaus.plexus.lifecycle.avalon.AvalonLifecycleHandler" );

        Configuration[] startSegmentPhases =
            lifecycleHandler.getChild( "start-segment" ).getChildren( "phase" );

        assertEquals( "org.codehaus.plexus.lifecycle.phase.LogEnablePhase",
                      startSegmentPhases[0].getAttribute( "implementation" ) );
        assertEquals( "org.codehaus.plexus.lifecycle.phase.ContextualizePhase",
                      startSegmentPhases[1].getAttribute( "implementation" ) );
        assertEquals( "org.codehaus.plexus.lifecycle.phase.ServicePhase",
                      startSegmentPhases[2].getAttribute( "implementation" ) );
        assertEquals( "org.codehaus.plexus.lifecycle.phase.ConfigurePhase",
                      startSegmentPhases[3].getAttribute( "implementation" ) );
        assertEquals( "org.codehaus.plexus.lifecycle.phase.ParameterizePhase",
                      startSegmentPhases[4].getAttribute( "implementation" ) );
        assertEquals( "org.codehaus.plexus.lifecycle.phase.InitializePhase",
                      startSegmentPhases[5].getAttribute( "implementation" ) );
        assertEquals( "org.codehaus.plexus.lifecycle.phase.StartPhase",
                      startSegmentPhases[6].getAttribute( "implementation" ) );

        Configuration[] components = c.getChild( "components" ).getChildren( "component" );

        assertEquals( "org.codehaus.plexus.service.repository.factory.ComponentFactory",
                      components[0].getChild( "role" ).getValue() );
        assertEquals( "java",
                      components[0].getChild( "role-hint" ).getValue() );
        assertEquals( "org.codehaus.plexus.service.repository.factory.JavaComponentFactory",
                      components[0].getChild( "implementation" ).getValue() );
    }

    public void testParserReuse()
        throws Exception
    {
        String s = "<conf><name>jason</name></conf>";
        XmlPullConfigurationBuilder cb = new XmlPullConfigurationBuilder();

        Configuration c = cb.parse( new StringReader( s ) );
        assertNotNull( c );
        assertEquals( "jason", c.getChild( "name" ).getValue() );

        s = "<conf><name>bob</name></conf>";
        c = cb.parse( new StringReader( s ) );
        assertNotNull( c );
        assertEquals( "bob", c.getChild( "name" ).getValue() );

        s = "<conf><name>pete</name></conf>";
        c = cb.parse( new StringReader( s ) );
        assertNotNull( c );
        assertEquals( "pete", c.getChild( "name" ).getValue() );

        s = "<conf><name>melissa</name></conf>";
        c = cb.parse( new StringReader( s ) );
        assertNotNull( c );
        assertEquals( "melissa", c.getChild( "name" ).getValue() );
    }

    public void testPrimitiveElementRetrieval()
        throws Exception
    {
        InputStream is = XmlPullConfigurationBuilder.class.getResourceAsStream( "primitive-element.xml" );
        XmlPullConfigurationBuilder cb = new XmlPullConfigurationBuilder();
        Configuration c = cb.parse( new InputStreamReader( is ) );

        assertTrue( 0 == c.getChild( "integer" ).getValueAsInteger() );
        assertTrue( 0 == c.getChild( "long" ).getValueAsLong() );
        assertTrue( 0 == c.getChild( "float" ).getValueAsFloat() );

        assertEquals( true, c.getChild( "boolean-true" ).getValueAsBoolean() );
        assertEquals( true, c.getChild( "boolean-yes" ).getValueAsBoolean() );
        assertEquals( true, c.getChild( "boolean-on" ).getValueAsBoolean() );
        assertEquals( true, c.getChild( "boolean-1" ).getValueAsBoolean() );

        assertEquals( false, c.getChild( "boolean-false" ).getValueAsBoolean() );
        assertEquals( false, c.getChild( "boolean-no" ).getValueAsBoolean() );
        assertEquals( false, c.getChild( "boolean-off" ).getValueAsBoolean() );
        assertEquals( false, c.getChild( "boolean-0" ).getValueAsBoolean() );
    }

    public void testPrimitiveAttributeRetrieval()
        throws Exception
    {
        InputStream is = XmlPullConfigurationBuilder.class.getResourceAsStream( "primitive-attr.xml" );
        XmlPullConfigurationBuilder cb = new XmlPullConfigurationBuilder();
        Configuration c = cb.parse( new InputStreamReader( is ) );

        assertTrue( 0 == c.getChild( "integer" ).getAttributeAsInteger( "value" ) );
        assertTrue( 0 == c.getChild( "long" ).getAttributeAsInteger( "value" ) );
        assertTrue( 0 == c.getChild( "float" ).getAttributeAsInteger( "value" ) );

        assertEquals( true, c.getChild( "boolean-true" ).getAttributeAsBoolean( "value" ) );
        assertEquals( true, c.getChild( "boolean-yes" ).getAttributeAsBoolean( "value" ) );
        assertEquals( true, c.getChild( "boolean-on" ).getAttributeAsBoolean( "value" ) );
        assertEquals( true, c.getChild( "boolean-1" ).getAttributeAsBoolean( "value" ) );

        assertEquals( false, c.getChild( "boolean-false" ).getAttributeAsBoolean( "value" ) );
        assertEquals( false, c.getChild( "boolean-no" ).getAttributeAsBoolean( "value" ) );
        assertEquals( false, c.getChild( "boolean-off" ).getAttributeAsBoolean( "value" ) );
        assertEquals( false, c.getChild( "boolean-0" ).getAttributeAsBoolean( "value" ) );
    }

}