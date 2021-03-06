package org.codehaus.plexus.archiver.gzip;

import java.io.File;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.archiver.zip.ZipArchiver;

public class GZipArchiverTest extends PlexusTestCase
{
    public GZipArchiverTest( String testName )
    {
        super( testName );
    }
    
    public void testCreateArchive() throws Exception
    {
        ZipArchiver zipArchiver = new ZipArchiver();
        zipArchiver.addDirectory( new File( "src" ) );
        zipArchiver.setDestFile( new File( "target/output/archiveForGzip.zip" ) );
        zipArchiver.createArchive();
        GZipArchiver archiver = new GZipArchiver();
        String[] inputFiles = new String[1];
        inputFiles[0] = "archiveForGzip.zip";
        archiver.addDirectory( new File( "target/output") , inputFiles, null );
        archiver.setDestFile( new File( "target/output/archive.gzip" ) );
        archiver.createArchive();
    }
}
