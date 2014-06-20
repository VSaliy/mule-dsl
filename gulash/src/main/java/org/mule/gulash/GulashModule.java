package org.mule.gulash;

import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.dependency.ModuleClassLoader;
import org.mule.dependency.ModuleDependencyManager;
import org.mule.gulash.depedencies.MavenDependencyHandler;
import org.mule.module.Apikit;
import org.mule.module.Core;
import org.mule.module.Groovy;
import org.mule.module.core.Mule;
import org.mule.module.core.StartListener;
import org.mule.module.core.TimePeriod;
import org.mule.module.core.application.MuleModule;
import org.mule.module.core.builder.PropertiesBuilder;
import org.mule.util.FileUtils;

import java.io.File;
import java.util.List;
import java.util.Set;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.raml.model.ActionType;

/**
 * Created by machaval on 6/17/14.
 */
public class GulashModule implements MuleModule
{

    private File groovyFile;
    private File appHome;
    private StringBuilder script;
    private ModuleDependencyManager moduleDependencyManager;

    public GulashModule(File groovyFile, File appHome)
    {
        this.groovyFile = groovyFile;
        this.appHome = appHome;
    }

    @Override
    public void initialize()
    {
        try
        {
            final ClassLoader parentClassloader = Thread.currentThread().getContextClassLoader();

            moduleDependencyManager = new ModuleDependencyManager(appHome, new MavenDependencyHandler(), parentClassloader);
            final GroovyShell groovyShell = createGroovyShell(createDependencyBinding(moduleDependencyManager), parentClassloader);

            final List<String> lines = FileUtils.readLines(groovyFile);

            script = new StringBuilder();
            StringBuilder require = new StringBuilder();
            for (int i = 0; i < lines.size(); i++)
            {
                String line = lines.get(i);
                //needs to do this better
                if (StringUtils.trim(line).startsWith("require"))
                {
                    require.append(line).append("\n");
                }
                else
                {
                    script.append(line).append("\n");
                }
            }

            groovyShell.evaluate(require.toString(), "Dependencies");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Mule mule)
    {


        final ModuleClassLoader moduleClassLoader = moduleDependencyManager.build();
        final Set<String> installedModules = moduleClassLoader.getInstalledModules();
        for (String installedModule : installedModules)
        {
            script.insert(0, "import org.mule.module." + installedModule + ";\n");
        }
        final GroovyShell groovyShell = createGroovyShell(createMuleBinding(mule), moduleClassLoader);
        groovyShell.evaluate(script.toString(), groovyFile.getName());


    }

    protected GroovyShell createGroovyShell(Binding binding, ClassLoader executionClassLoader)
    {
        final CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
        compilerConfiguration.setScriptBaseClass(MuleBaseScript.class.getName());
        final ImportCustomizer importCustomizer = new ImportCustomizer();
        importCustomizer.addStaticStars(Core.class.getName(), Groovy.class.getName(), Apikit.class.getName(), PropertiesBuilder.class.getName(), TimePeriod.class.getName());
        importCustomizer.addImports(StartListener.class.getName());
        importCustomizer.addImports(MuleEvent.class.getName());
        importCustomizer.addImports(MuleMessage.class.getName());
        importCustomizer.addImports(ActionType.class.getName());
        compilerConfiguration.addCompilationCustomizers(importCustomizer);

        return new GroovyShell(executionClassLoader, binding, compilerConfiguration);
    }


    protected Binding createMuleBinding(Mule mule)
    {
        final Binding binding = new Binding();
        binding.setVariable("mule", mule);
        return binding;
    }

    protected Binding createDependencyBinding(ModuleDependencyManager dependency)
    {
        final Binding binding = new Binding();
        binding.setVariable("dependency", dependency);
        return binding;
    }
}
