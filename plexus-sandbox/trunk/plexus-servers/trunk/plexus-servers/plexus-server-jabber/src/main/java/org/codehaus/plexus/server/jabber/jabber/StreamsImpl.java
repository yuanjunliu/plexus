/*
 * BSD License http://open-im.org/bsd-license.html
 * Copyright (c) 2003, OpenIM Project http://open-im.org
 * All rights reserved.
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the OpenIM project. For more
 * information on the OpenIM project, please see
 * http://open-im.org/
 */
package org.codehaus.plexus.server.jabber.jabber;


import org.apache.avalon.framework.service.ServiceManager;

import org.xmlpull.v1.XmlPullParser;

import org.codehaus.plexus.server.jabber.ServerParameters;
import org.codehaus.plexus.server.jabber.DefaultSessionProcessor;
import org.codehaus.plexus.server.jabber.session.IMServerSession;
import org.codehaus.plexus.server.jabber.session.IMSession;


/**
 * @author AlAg
 * @version 1.0
 * @avalon.component version="1.0" name="Streams" lifestyle="singleton"
 * @avalon.service type="org.codehaus.plexus.server.jabber.jabber.Streams"
 */
public class StreamsImpl
    extends DefaultSessionProcessor
    implements Streams
{

    protected ServerParameters m_serverParameters;
    protected String m_namespace;

    /**
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.ServerParameters:1.0"  key="ServerParameters"
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.jabber.Error:1.0" key="Error"
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.jabber.server.Iq:1.0" key="server.Iq"
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.jabber.server.dialback.Result:1.0" key="server.dialback.Result"
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.jabber.server.dialback.Verify:1.0" key="server.dialback.Verify"
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.jabber.server.Message:1.0" key="server.Message"
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.jabber.server.Presence:1.0" key="server.Presence"
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.jabber.client.Iq:1.0" key="client.Iq"
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.jabber.client.Presence:1.0" key="client.Presence"
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.jabber.client.Message:1.0" key="client.Message"
     */
    public void service( ServiceManager serviceManager )
        throws org.apache.avalon.framework.service.ServiceException
    {
        m_serverParameters = (ServerParameters) serviceManager.lookup( "ServerParameters" );
        super.service( serviceManager );
    }


    public void process( final IMSession session,
                         final Object context )
        throws Exception
    {
        final XmlPullParser xpp = session.getXmlPullParser();
        m_namespace = xpp.getNamespace( null );
        processAttribute( session, context );
        if ( session instanceof IMServerSession )
        {
            getLogger().info(
                "Start stream " + ( (IMServerSession) session ).getRemoteHostname() + " id " + session.getId() );
        }
        super.process( session, context );
        if ( session instanceof IMServerSession )
        {
            getLogger().info(
                "Stop stream " + ( (IMServerSession) session ).getRemoteHostname() + " id " + session.getId() );
        }
    }

    //-------------------------------------------------------------------------
    public void processAttribute( final IMSession session,
                                  final Object context )
        throws Exception
    {

        final XmlPullParser xpp = session.getXmlPullParser();
        String to = xpp.getAttributeValue( "", "to" );
        String from = xpp.getAttributeValue( "", "from" );

        if ( from == null || from.length() == 0 )
        {
            getLogger().debug( "from attribut not specified in stream declaration" );
        }
        else
        {
            if ( session instanceof IMServerSession )
            {
                ( (IMServerSession) session ).setRemoteHostname( from );
            }
        }


        if ( session.getConnectionType() == IMSession.S2S_L2R_CONNECTION )
        {
            getLogger().debug( "Local to Remote connection " + to );
        }
        else
        {
            String s =
                "<stream:stream xmlns:stream='http://etherx.jabber.org/streams' " + "id='" + session.getId() + "' ";
            if ( session.getConnectionType() == IMSession.C2S_CONNECTION )
            {
                s += "xmlns='jabber:client' ";
            }
            else if ( session.getConnectionType() == IMSession.S2S_R2L_CONNECTION )
            {
                s += "xmlns='jabber:server' xmlns:db='jabber:server:dialback' ";
            }
            s += "from='" + m_serverParameters.getHostName() + "'>";
            session.writeOutputStream( s );

        }
    }

    public String getNamespace()
    {
        return m_namespace;
    }

}

