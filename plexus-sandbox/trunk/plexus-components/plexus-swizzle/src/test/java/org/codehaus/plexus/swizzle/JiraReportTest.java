/**
 *
 * Copyright 2006
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.codehaus.plexus.swizzle;

import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;

import org.codehaus.plexus.PlexusTestCase;

/**
 * @author jtolentino
 * @version $$Id: JiraReportTest.java 3353 2006-05-31 14:17:11Z jtolentino $$
 */
public class JiraReportTest
    extends PlexusTestCase
{

    public void testResolvedIssuesReport()
        throws Exception
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream result = new PrintStream( baos );

        JiraReport report = (DefaultJiraReport) lookup( JiraReport.ROLE );

        report.generateResolvedIssuesReport( result );

        result.close();

        ClassLoader classLoader = this.getClass().getClassLoader();
        String expectedFile = "org/codehaus/plexus/swizzle/ResolvedIssuesExpectedResult.txt";
        URL resource = classLoader.getResource( expectedFile );
        String expected = streamToString( resource.openStream() );

        String actual = new String( baos.toByteArray() );

        assertEquals( expected, actual );
    }

    public void testVotesReport()
        throws Exception
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream result = new PrintStream( baos );

        JiraReport report = (DefaultJiraReport) lookup( JiraReport.ROLE );

        report.generateVotesReport( result );

        result.close();

        ClassLoader classLoader = this.getClass().getClassLoader();
        String expectedFile = "org/codehaus/plexus/swizzle/VotesExpectedResult.txt";
        URL resource = classLoader.getResource( expectedFile );
        String expected = streamToString( resource.openStream() );

        String actual = new String( baos.toByteArray() );

        assertEquals( expected, actual );
    }

    private static String streamToString( InputStream in )
        throws IOException
    {
        StringBuffer text = new StringBuffer();
        try
        {
            int b;
            while ( ( b = in.read() ) != -1 )
            {
                text.append( (char) b );
            }
        }
        finally
        {
            in.close();
        }
        return text.toString();
    }

}
