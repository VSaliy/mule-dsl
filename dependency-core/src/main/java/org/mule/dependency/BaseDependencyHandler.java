package org.mule.dependency;

import org.mule.module.core.Mule;

import java.io.File;

/**
 * Created by machaval on 6/17/14.
 */
public abstract class BaseDependencyHandler implements DependencyHandler
{

    protected File getModuleVersionDirectory(File appHome, String module, String version)
    {
        final File moduleDirectory = getModuleDirectory(appHome, module);
        final File moduleTargetDirectory = new File(moduleDirectory, version);

        return moduleTargetDirectory;
    }

    protected File getModuleDirectory(File appHome, String module)
    {
        final File libDirectory = getLibDirectory(appHome);
        return new File(libDirectory, module);
    }

    public File getLibDirectory(File appHome)
    {
        return new File(appHome, ".lib");
    }
}
