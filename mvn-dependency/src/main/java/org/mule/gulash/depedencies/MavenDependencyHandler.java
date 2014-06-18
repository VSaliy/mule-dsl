package org.mule.gulash.depedencies;

import org.mule.dependency.BaseDependencyHandler;
import org.mule.dependency.DependencyModule;
import org.mule.module.core.BuilderConfigurationException;
import org.mule.util.FileUtils;
import org.mule.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.version.Version;


public class MavenDependencyHandler extends BaseDependencyHandler
{

    public static final String DEFAULT_GROUP_ID = "org.mule.modules";
    public static final String DEFAULT_MODULE_PREFIX = "mule-module-";
    public static final String DEFAULT_CLASSIFIER = "plugin";
    public static final String DEFAULT_EXTENSION = "zip";


    @Override
    public File getModule(DependencyModule dependencyModule, File appHome)
    {
        try
        {
            String version = StringUtils.isBlank(dependencyModule.getVersion()) ? getHighestVersion(dependencyModule.getName()) : dependencyModule.getVersion();
            String moduleName = dependencyModule.getName();

            final File moduleTargetDirectory = getModuleVersionDirectory(appHome, moduleName, version);
            if (!moduleTargetDirectory.exists())
            {
                final RepositorySystem system = MvnFactory.newRepositorySystem();
                final RepositorySystemSession session = MvnFactory.newRepositorySystemSession(system);
                final Artifact artifact = createArtifact(moduleName, version);
                final ArtifactRequest artifactRequest = createArtifactRequest(artifact);
                if (!StringUtils.isBlank(dependencyModule.getUrl()))
                {
                    artifactRequest.addRepository(MvnFactory.newCustomRepository(dependencyModule.getUrl()));
                }
                final ArtifactResult rangeResult = system.resolveArtifact(session, artifactRequest);
                File file = rangeResult.getArtifact().getFile();
                moduleTargetDirectory.mkdirs();
                FileUtils.unzip(file, moduleTargetDirectory);

            }
            System.out.println("Module " + dependencyModule.getName() + ":" + version + " was successfully installed at " + moduleTargetDirectory.getAbsolutePath());
            return moduleTargetDirectory;
        }
        catch (IOException e)
        {
            throw new BuilderConfigurationException("Exception while installing module  " + dependencyModule, e);
        }
        catch (ArtifactResolutionException e)
        {
            throw new BuilderConfigurationException("Exception while installing module  " + dependencyModule, e);
        }
    }

    private DefaultArtifact createArtifact(String module, String version)
    {
        return new DefaultArtifact(DEFAULT_GROUP_ID, DEFAULT_MODULE_PREFIX + module, DEFAULT_CLASSIFIER, DEFAULT_EXTENSION, version);
    }


    public String getHighestVersion(String module)
    {

        try
        {
            final RepositorySystem system = MvnFactory.newRepositorySystem();
            final RepositorySystemSession session = MvnFactory.newRepositorySystemSession(system);
            final Artifact artifact = createArtifact(module, "[0,)");
            final VersionRangeRequest rangeRequest = createVersionRangeRequest(artifact);
            final VersionRangeResult rangeResult = system.resolveVersionRange(session, rangeRequest);
            return rangeResult.getHighestVersion().toString();
        }
        catch (VersionRangeResolutionException e)
        {
            throw new BuilderConfigurationException("Exception while trying to get versions module  " + module, e);
        }
    }

    @Override
    public List<String> listVersions(String module, File appHome)
    {
        try
        {
            final RepositorySystem system = MvnFactory.newRepositorySystem();
            final RepositorySystemSession session = MvnFactory.newRepositorySystemSession(system);
            final Artifact artifact = createArtifact(module, "[0,)");
            final VersionRangeRequest rangeRequest = createVersionRangeRequest(artifact);
            final VersionRangeResult rangeResult = system.resolveVersionRange(session, rangeRequest);
            final List<Version> versions = rangeResult.getVersions();
            List<String> result = new ArrayList<String>();
            for (Version version : versions)
            {
                result.add(version.toString());
            }
            return result;
        }
        catch (VersionRangeResolutionException e)
        {
            throw new BuilderConfigurationException("Exception while trying to get versions module  " + module, e);
        }
    }

    private VersionRangeRequest createVersionRangeRequest(Artifact artifact)
    {
        final VersionRangeRequest rangeRequest = new VersionRangeRequest();
        rangeRequest.addRepository(MvnFactory.newMuleReleasesRepository());
        rangeRequest.addRepository(MvnFactory.newCentralRepository());
        rangeRequest.setArtifact(artifact);
        return rangeRequest;
    }

    private ArtifactRequest createArtifactRequest(Artifact artifact)
    {
        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.addRepository(MvnFactory.newMuleReleasesRepository());
        artifactRequest.addRepository(MvnFactory.newCentralRepository());
        artifactRequest.setArtifact(artifact);
        return artifactRequest;
    }


}
