package org.codehaus.plexus.classworlds.launcher;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.DuplicateRealmException;
import org.codehaus.plexus.classworlds.realm.NoSuchRealmException;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;

/**
 * <code>Launcher</code> configurator.
 * 
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 * @author Jason van Zyl
 * @version $Id$
 */
public class Configurator
{
    public static final String MAIN_PREFIX = "main is";
    public static final String SET_PREFIX = "set";
    public static final String IMPORT_PREFIX = "import";
    public static final String LOAD_PREFIX = "load";

    /** The launcher to configure. */
    private Launcher launcher;

    /** The ClassWorld to create */
    private ClassWorld world;

    /** Processed Realms. */
    private Map configuredRealms;

    public Configurator( Launcher launcher )
    {
        this.launcher = launcher;
        this.configuredRealms = new HashMap();
    }

    public Configurator( ClassWorld world )
    {
        this.world = world;
    }

    /**
     * Configure from a file.
     * 
     * @param is The config input stream
     * @throws IOException If an error occurs reading the config file.
     * @throws MalformedURLException If the config file contains invalid URLs.
     * @throws ConfigurationException If the config file is corrupt.
     * @throws org.codehaus.plexus.classworlds.realm.DuplicateRealmException If the config file
     *             defines two realms with the same id.
     * @throws org.codehaus.plexus.classworlds.realm.NoSuchRealmException If the config file defines
     *             a main entry point in a non-existent realm.
     */
    public void configure( InputStream is )
        throws IOException, ConfigurationException, DuplicateRealmException, NoSuchRealmException
    {
        BufferedReader reader = new BufferedReader( new InputStreamReader( is, "UTF-8" ) );

        if ( world == null )
        {
            world = new ClassWorld();
        }

        ClassLoader foreignClassLoader = null;

        if ( this.launcher != null )
        {
            foreignClassLoader = this.launcher.getSystemClassLoader();
        }

        RealmConfiguration currentRealm = null;

        String line = null;

        int lineNo = 0;

        boolean mainSet = false;

        while ( true )
        {
            line = reader.readLine();

            if ( line == null )
            {
                break;
            }

            ++lineNo;
            line = line.trim();

            if ( canIgnore( line ) )
            {
                continue;
            }

            if ( line.startsWith( MAIN_PREFIX ) )
            {
                if ( mainSet )
                {
                    throw new ConfigurationException( "Duplicate main configuration", lineNo, line );
                }

                String conf = line.substring( MAIN_PREFIX.length() ).trim();

                int fromLoc = conf.indexOf( "from" );

                if ( fromLoc < 0 )
                {
                    throw new ConfigurationException( "Missing from clause", lineNo, line );
                }

                String mainClassName = conf.substring( 0, fromLoc ).trim();

                String mainRealmName = conf.substring( fromLoc + 4 ).trim();

                if ( this.launcher != null )
                {
                    this.launcher.setAppMain( mainClassName, mainRealmName );
                }

                mainSet = true;
            }
            else if ( line.startsWith( SET_PREFIX ) )
            {
                String conf = line.substring( SET_PREFIX.length() ).trim();

                int usingLoc = conf.indexOf( " using" ) + 1;

                String property = null;

                String propertiesFileName = null;

                if ( usingLoc > 0 )
                {
                    property = conf.substring( 0, usingLoc ).trim();

                    propertiesFileName = filter( conf.substring( usingLoc + 5 ).trim() );

                    conf = propertiesFileName;
                }

                String defaultValue = null;

                int defaultLoc = conf.indexOf( " default" ) + 1;

                if ( defaultLoc > 0 )
                {
                    defaultValue = conf.substring( defaultLoc + 7 ).trim();

                    if ( property == null )
                    {
                        property = conf.substring( 0, defaultLoc ).trim();
                    }
                    else
                    {
                        propertiesFileName = conf.substring( 0, defaultLoc ).trim();
                    }
                }

                String value = System.getProperty( property );

                if ( value != null )
                {
                    continue;
                }

                if ( propertiesFileName != null )
                {
                    File propertiesFile = new File( propertiesFileName );

                    if ( propertiesFile.exists() )
                    {
                        Properties properties = new Properties();

                        try
                        {
                            properties.load( new FileInputStream( propertiesFileName ) );

                            value = properties.getProperty( property );
                        }
                        catch ( Exception e )
                        {
                            // do nothing
                        }
                    }
                }

                if ( value == null && defaultValue != null )
                {
                    value = defaultValue;
                }

                if ( value != null )
                {
                    value = filter( value );
                    System.setProperty( property, value );
                }
            }
            else if ( line.startsWith( "[" ) )
            {
                int rbrack = line.indexOf( "]" );

                if ( rbrack < 0 )
                {
                    throw new ConfigurationException( "Invalid realm specifier", lineNo, line );
                }

                String realmName = line.substring( 1, rbrack );

                currentRealm = new RealmConfiguration( realmName );

                // Stash the configured realm for subsequent association processing.
                //configuredRealms.put( realmName, curRealm );
                configuredRealms.put( realmName, currentRealm );
            }
            else if ( line.startsWith( IMPORT_PREFIX ) )
            {
                if ( currentRealm == null )
                {
                    throw new ConfigurationException( "Unhandled import", lineNo, line );
                }

                String conf = line.substring( IMPORT_PREFIX.length() ).trim();

                int fromLoc = conf.indexOf( "from" );

                if ( fromLoc < 0 )
                {
                    throw new ConfigurationException( "Missing from clause", lineNo, line );
                }

                String importSpec = conf.substring( 0, fromLoc ).trim();

                String relamName = conf.substring( fromLoc + 4 ).trim();

                currentRealm.addImport( new ImportStatement( relamName, importSpec ) );

            }
            else if ( line.startsWith( LOAD_PREFIX ) )
            {
                String constituent = line.substring( LOAD_PREFIX.length() ).trim();

                currentRealm.addLoad( constituent );

                constituent = filter( constituent );

                if ( constituent.indexOf( "*" ) >= 0 )
                {
                    loadGlob( constituent, currentRealm );
                }
                else
                {
                    File file = new File( constituent );

                    if ( file.exists() )
                    {
                        currentRealm.addLoad( file.toURI().toURL().toExternalForm() );
                    }
                    else
                    {
                        try
                        {
                            currentRealm.addLoad( new URL( constituent ).toExternalForm() );
                        }
                        catch ( MalformedURLException e )
                        {
                            throw new FileNotFoundException( constituent );
                        }
                    }
                }
            }
            else
            {
                throw new ConfigurationException( "Unhandled configuration", lineNo, line );
            }
        }

        // Associate child realms to their parents.
        createRealms();

        if ( this.launcher != null )
        {
            this.launcher.setWorld( world );
        }

        reader.close();
    }

    // TODO return this to protected when the legacy wrappers can be removed.
    /**
     * Associate parent realms with their children.
     * 
     * @throws DuplicateRealmException
     * @throws NoSuchRealmException
     */
    public void createRealms()
        throws DuplicateRealmException, NoSuchRealmException
    {
        List sortRealmNames = new ArrayList( configuredRealms.keySet() );

        // sort by name
        Comparator comparator = new Comparator()
        {
            public int compare( Object o1, Object o2 )
            {
                String g1 = (String) o1;
                String g2 = (String) o2;
                return g1.compareTo( g2 );
            }
        };

        Collections.sort( sortRealmNames, comparator );

        // So now we have something like the following for defined
        // realms:
        //
        // root
        // root.maven
        // root.maven.plugin
        //
        // Now if the name of a realm is a superset of an existing realm
        // the we want to make child/parent associations.

        for ( Iterator i = sortRealmNames.iterator(); i.hasNext(); )
        {
            String realmName = (String) i.next();

            int j = realmName.lastIndexOf( '.' );

            if ( j > 0 )
            {
                String parentRealmName = realmName.substring( 0, j );

                RealmConfiguration parentRealm = (RealmConfiguration) configuredRealms.get( parentRealmName );

                if ( parentRealm != null )
                {
                    RealmConfiguration realm = (RealmConfiguration) configuredRealms.get( realmName );

                    world.newRealm( realm.id, world.getRealm( parentRealm.id ) );
                }
            }
            else
            {
                world.newRealm( realmName );
            }
        }
    }

    /**
     * Load a glob into the specified classloader.
     * 
     * @param line The path configuration line.
     * @param realm The realm to populate
     * @param optionally Whether the path is optional or required
     * @throws MalformedURLException If the line does not represent a valid path element.
     * @throws FileNotFoundException If the line does not represent a valid path element in the
     *             filesystem.
     */
    protected void loadGlob( String line, RealmConfiguration realm )
        throws MalformedURLException, FileNotFoundException
    {
        File globFile = new File( line );

        File dir = globFile.getParentFile();
        if ( !dir.exists() )
        {
            throw new FileNotFoundException( dir.toString() );
        }

        String localName = globFile.getName();

        int starLoc = localName.indexOf( "*" );

        final String prefix = localName.substring( 0, starLoc );

        final String suffix = localName.substring( starLoc + 1 );

        File[] matches = dir.listFiles( new FilenameFilter()
        {
            public boolean accept( File dir, String name )
            {
                if ( !name.startsWith( prefix ) )
                {
                    return false;
                }

                if ( !name.endsWith( suffix ) )
                {
                    return false;
                }

                return true;
            }
        } );

        for ( int i = 0; i < matches.length; ++i )
        {
            realm.addLoad( matches[i].toURI().toURL().toExternalForm() );
        }
    }

    /**
     * Filter a string for system properties.
     * 
     * @param text The text to filter.
     * @return The filtered text.
     * @throws ConfigurationException If the property does not exist or if there is a syntax error.
     */
    protected String filter( String text )
        throws ConfigurationException
    {
        String result = "";

        int cur = 0;
        int textLen = text.length();

        int propStart = -1;
        int propStop = -1;

        String propName = null;
        String propValue = null;

        while ( cur < textLen )
        {
            propStart = text.indexOf( "${", cur );

            if ( propStart < 0 )
            {
                break;
            }

            result += text.substring( cur, propStart );

            propStop = text.indexOf( "}", propStart );

            if ( propStop < 0 )
            {
                throw new ConfigurationException( "Unterminated property: " + text.substring( propStart ) );
            }

            propName = text.substring( propStart + 2, propStop );

            propValue = System.getProperty( propName );

            /* do our best if we are not running from surefire */
            if ( propName.equals( "basedir" ) && ( propValue == null || propValue.equals( "" ) ) )
            {
                propValue = ( new File( "" ) ).getAbsolutePath();
            }

            if ( propValue == null )
            {
                throw new ConfigurationException( "No such property: " + propName );
            }
            result += propValue;

            cur = propStop + 1;
        }

        result += text.substring( cur );

        return result;
    }

    /**
     * Determine if a line can be ignored because it is a comment or simply blank.
     * 
     * @param line The line to test.
     * @return <code>true</code> if the line is ignorable, otherwise <code>false</code>.
     */
    private boolean canIgnore( String line )
    {
        return ( line.length() == 0 || line.startsWith( "#" ) );
    }

    class RealmConfiguration
    {
        String id;
        TreeSet imports;
        TreeSet exports;
        TreeSet loads;

        public RealmConfiguration( String id )
        {
            this.id = id;
        }

        public void addImport( ImportStatement importStatement )
        {
            imports.add( importStatement );
        }

        public void addExport( String exportStatement )
        {
            exports.add( exportStatement );
        }

        public void addLoad( String loadStatement )
        {
            loads.add( loadStatement );
        }
    }

    class ImportStatement
    {
        public ImportStatement( String relamName, String importSpec )
        {
            // TODO Auto-generated constructor stub
        }

        String importRealm;
        String importSpecification;
    }
}
