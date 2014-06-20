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
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.JarArchiver;

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
     * Name of the generated Mule App.
     *
     * @parameter alias="appName" expression="${appName}" default-value="${project.build.finalName}"
     * @required
     */
    protected String finalName;

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
        for (Artifact dependency : dependencies)
        {
            final String scope = dependency.getScope();
            System.out.println("scope = " + scope);
            if (Artifact.SCOPE_COMPILE.equals(scope) || Artifact.SCOPE_RUNTIME.equals(scope))
            {
                mushiArchiver.addLibraryArtifact(dependency);
            }
        }
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

    protected File getMushiAppFile()
    {
        return new File(this.outputDirectory, this.finalName + ".zip");
    }
}
