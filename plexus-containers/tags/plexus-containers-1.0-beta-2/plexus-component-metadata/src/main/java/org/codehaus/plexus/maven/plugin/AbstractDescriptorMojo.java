package org.codehaus.plexus.maven.plugin;

/*
 * The MIT License
 * 
 * Copyright (c) 2004-2006, The Codehaus
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.metadata.ExtractorConfiguration;
import org.codehaus.plexus.metadata.MetadataGenerator;
import org.codehaus.plexus.metadata.merge.Merger;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @since 1.3.4
 */
public abstract class AbstractDescriptorMojo
    extends AbstractMojo
{
    protected static final String COMPILE_SCOPE = "compile";

    protected static final String TEST_SCOPE = "test";

    /**
     * @parameter default-value="${project.build.outputDirectory}/META-INF/plexus/components.xml"
     * @required
     */
    protected File generatedComponentDescriptor;

    /**
     * @parameter default-value="${basedir}/src/main/resources/META-INF/plexus/components.xml"
     * @required
     */
    protected File sourceComponentDescriptor;

    /**
     * @parameter default-value="${project.build.directory}/components.xml"
     * @required
     */
    protected File intermediaryComponentDescriptor;

    /**
     * Whether to generate a Plexus Container descriptor instead of a component descriptor.
     * 
     * @parameter default-value="false"
     * @required
     */
    private boolean containerDescriptor;

    /**
     * @parameter expression="${project}"
     * @required
     */
    protected MavenProject mavenProject;

    /** @component */
    protected MavenProjectHelper mavenProjectHelper;

    /** @component */
    protected MetadataGenerator metadataGenerator;    

    /** @component role-hint="componentsXml" */
    private Merger merger;

    protected void generateDescriptor( String scope, File outputFile )
        throws MojoExecutionException
    {
        ExtractorConfiguration extractorConfiguration = new ExtractorConfiguration();
        
        try
        {
            if ( scope.equals( COMPILE_SCOPE ) )
            {
                extractorConfiguration.classpath = mavenProject.getCompileClasspathElements();
                extractorConfiguration.classesDirectory = new File( mavenProject.getBuild().getOutputDirectory() );
                extractorConfiguration.sourceDirectories = mavenProject.getCompileSourceRoots();
            }
            else if ( scope.equals( TEST_SCOPE ) )
            {
                extractorConfiguration.classpath = mavenProject.getTestClasspathElements();
                extractorConfiguration.classesDirectory = new File( mavenProject.getBuild().getTestOutputDirectory() );
                extractorConfiguration.sourceDirectories = mavenProject.getTestCompileSourceRoots();                
            }
            
            if ( sourceComponentDescriptor.exists() )
            {
                metadataGenerator.generateDescriptor( extractorConfiguration, intermediaryComponentDescriptor );
                
                List<File> componentDescriptors = new ArrayList<File>();
                
                componentDescriptors.add( sourceComponentDescriptor );
                
                // We have run the metadata generator but we may have the case where there is entire
                // overlap in the source descriptor and what's being generated. This happens during
                // a transition phase when moving from manually crafted descriptors to purely
                // generated descriptors.
                if ( intermediaryComponentDescriptor.exists() )
                {
                    componentDescriptors.add( intermediaryComponentDescriptor );
                }
                
                merger.mergeDescriptors( outputFile, componentDescriptors );
            }
            else
            {
                metadataGenerator.generateDescriptor( extractorConfiguration, generatedComponentDescriptor );                
            }                        
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Error generating metadata: ", e );
        }        
    }
}
