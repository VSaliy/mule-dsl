package org.mule.dependency;

import org.mule.util.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class LocalDependencyHandlerImpl extends BaseDependencyHandler
{

    @Override
    public File getModule(DependencyModule dependencyModule, File mule)
    {
        final String moduleName = dependencyModule.getName();
        final String version;
        if (StringUtils.isBlank(dependencyModule.getVersion()))
        {
            version = getHighestVersion(mule, dependencyModule.getName());
            if (version == null)
            {
                throw new RuntimeException("No version found for " + moduleName);
            }
        }
        else
        {
            version = dependencyModule.getVersion();
        }
        final File moduleTargetDirectory = getModuleVersionDirectory(mule, moduleName, version);
        return moduleTargetDirectory;
    }

    private String getHighestVersion(File appHome, String name)
    {
        final List<String> versions = listVersions(name, appHome);
        return versions.isEmpty() ? null : versions.get(versions.size() - 1);
    }

    @Override
    public List<String> listVersions(String module, File appHome)
    {
        final File moduleDirectory = getModuleDirectory(appHome, module);
        if (!moduleDirectory.exists())
        {
            return Collections.emptyList();
        }
        else
        {
            final String[] versions = moduleDirectory.list();
            Arrays.sort(versions);
            return Arrays.asList(versions);
        }

    }
}
