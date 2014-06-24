package org.mule.mushi;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.resolver.ResolutionNode;
import org.apache.maven.artifact.resolver.WarningResolutionListener;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.CollectionUtils;

/**
 * Generates a mushi app
 *
 * @goal mushi
 * @phase package
 */
public class MushiMojo extends AbstractMojo
{


    /**
     * The Maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;


    /**
     * Directory containing the classes.
     *
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     */
    private File classesDirectory;

    /**
     * Directory containing the generated Mule App.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     */
    protected File outputDirectory;


    /**
     * Local maven repository.
     *
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    protected ArtifactRepository localRepository;

    /**
     * Artifact collector, needed to resolve dependencies.
     *
     * @component role="org.apache.maven.artifact.resolver.ArtifactCollector"
     * @required
     * @readonly
     */
    protected ArtifactCollector artifactCollector;

    /**
     * @component role="org.apache.maven.artifact.metadata.ArtifactMetadataSource" hint="maven"
     */
    protected ArtifactMetadataSource artifactMetadataSource;

    /**
     * Name of the generated Mule App.
     *
     * @parameter alias="appName" expression="${appName}" default-value="${project.build.finalName}"
     * @required
     */
    protected String finalName;

    /**
     * Artifact resolver, needed to download source jars for inclusion in classpath.
     *
     * @component role="org.apache.maven.artifact.resolver.ArtifactResolver"
     * @required
     * @readonly
     */
    protected ArtifactResolver artifactResolver;

    protected Logger logger;

    public void execute() throws MojoExecutionException
    {

        try
        {
            final MushiArchiver mushiArchiver = new MushiArchiver(false);
            mushiArchiver.setDestFile(getMushiAppFile());
            includeDependencies(mushiArchiver);
            includeProjectJar(mushiArchiver);
            mushiArchiver.createArchive();
        }
        catch (IOException e)
        {
            getLog().error("Cannot create archive", e);
        }
        catch (ArchiverException e)
        {
            getLog().error("Cannot create archive", e);
        }

    }

    private void includeDependencies(MushiArchiver mushiArchiver) throws ArchiverException
    {


        final Set<Artifact> dependencies = project.getDependencyArtifacts();
        final Set<Artifact> filteredDependencies = new HashSet<Artifact>();
        for (Artifact dependency : dependencies)
        {
            if (isCorrectScope(dependency))
            {
                filteredDependencies.add(dependency);
            }
        }
        try
        {
            final ArrayList listeners = new ArrayList();
            // listeners.add(new WarningResolutionListener(logger));
            final ArtifactResolutionResult collect = getArtifactCollector().collect(filteredDependencies, project.getArtifact(), getLocalRepository(), project.getRemoteArtifactRepositories(), getArtifactMetadataSource(), new ArtifactFilter()
            {
                @Override
                public boolean include(Artifact artifact)
                {
                    final boolean isMuleCore = artifact.getGroupId().equals("org.mule") && artifact.getArtifactId().equals("mule-core");
                    final boolean isMuleLightCore = artifact.getGroupId().equals("org.mule.feather") && artifact.getArtifactId().equals("core");
                    return !isMuleCore && !isMuleLightCore;
                }
            }, listeners);
            final Set<ResolutionNode> artifactResolutionNodes = collect.getArtifactResolutionNodes();
            for (ResolutionNode artifactResolutionNode : artifactResolutionNodes)
            {


                Artifact artifact = artifactResolutionNode.getArtifact();
                // don't resolve jars for reactor projects
                if (isCorrectScope(artifact))
                {
                    try
                    {
                        getArtifactResolver().resolve(artifact, artifactResolutionNode.getRemoteRepositories(), getLocalRepository());
                    }
                    catch (ArtifactNotFoundException e)
                    {
                        getLog().debug(e.getMessage(), e);
                    }
                    catch (ArtifactResolutionException e)
                    {
                        getLog().debug(e.getMessage(), e);
                    }
                }

                if (artifact.getType().equals("jar") && isCorrectScope(artifact))
                {
                    mushiArchiver.addLibraryArtifact(artifact);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void enableLogging(Logger logger)
    {
        this.logger = logger;
    }

    private boolean isCorrectScope(Artifact artifact)
    {
        return Artifact.SCOPE_COMPILE.equals(artifact.getScope()) || Artifact.SCOPE_RUNTIME.equals(artifact.getScope());
    }

    private void includeProjectJar(MushiArchiver archiver) throws ArchiverException, MojoExecutionException
    {
        if (!this.classesDirectory.exists())
        {
            getLog().warn(this.classesDirectory + " does not exist, skipping");
            return;
        }

        getLog().info("Copying classes as a jar");

        final JarArchiver jarArchiver = new JarArchiver();
        jarArchiver.addDirectory(this.classesDirectory);
        final File jar = new File(this.outputDirectory, this.finalName + ".jar");
        jarArchiver.setDestFile(jar);
        try
        {
            jarArchiver.createArchive();
            archiver.addLib(jar);
        }
        catch (IOException e)
        {
            final String message = "Cannot create project jar";
            getLog().error(message, e);
            throw new MojoExecutionException(message, e);
        }
    }

    public ArtifactRepository getLocalRepository()
    {
        return localRepository;
    }

    public void setLocalRepository(ArtifactRepository localRepository)
    {
        this.localRepository = localRepository;
    }

    public ArtifactCollector getArtifactCollector()
    {
        return artifactCollector;
    }

    public void setArtifactCollector(ArtifactCollector artifactCollector)
    {
        this.artifactCollector = artifactCollector;
    }

    public ArtifactMetadataSource getArtifactMetadataSource()
    {
        return artifactMetadataSource;
    }

    public void setArtifactMetadataSource(ArtifactMetadataSource artifactMetadataSource)
    {
        this.artifactMetadataSource = artifactMetadataSource;
    }

    public void setProject(MavenProject project)
    {
        this.project = project;
    }

    public void setClassesDirectory(File classesDirectory)
    {
        this.classesDirectory = classesDirectory;
    }

    public void setOutputDirectory(File outputDirectory)
    {
        this.outputDirectory = outputDirectory;
    }

    public void setFinalName(String finalName)
    {
        this.finalName = finalName;
    }

    public ArtifactResolver getArtifactResolver()
    {
        return artifactResolver;
    }

    public void setArtifactResolver(ArtifactResolver artifactResolver)
    {
        this.artifactResolver = artifactResolver;
    }

    protected File getMushiAppFile()
    {
        return new File(this.outputDirectory, this.finalName + ".zip");
    }
}
