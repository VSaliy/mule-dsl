package org.mule.dependency;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by machaval on 6/17/14.
 */
public class ModuleDependencyManager
{

    private final File appHome;
    private DependencyHandler dependencyHandler;
    private List<DependencyModuleBuilder> moduleBuilders;
    private ModuleClassLoader moduleClassLoader;

    public ModuleDependencyManager(File appHome, DependencyHandler dependencyHandler, ClassLoader parent)
    {
        this.appHome = appHome;
        this.dependencyHandler = dependencyHandler;
        this.moduleBuilders = new ArrayList<DependencyModuleBuilder>();
        this.moduleClassLoader = new ModuleClassLoader(parent);
    }

    public DependencyModuleBuilder require(String module)
    {
        final DependencyModuleBuilder result = new DependencyModuleBuilder(module);
        moduleBuilders.add(result);
        return result;
    }


    public ModuleClassLoader build()
    {
        for (DependencyModuleBuilder moduleBuilder : moduleBuilders)
        {
            final DependencyModule dependencyModule = moduleBuilder.create();
            moduleClassLoader.installModule(dependencyModule.getName(), dependencyHandler.getModule(dependencyModule, appHome));
        }
        return moduleClassLoader;
    }

    public ModuleClassLoader getModuleClassLoader()
    {
        return moduleClassLoader;
    }
}
