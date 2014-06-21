package org.mule.gulash;

import org.mule.module.core.application.MuleApplication;
import org.mule.module.core.application.MuleEnvironment;
import org.mule.module.core.application.MuleModule;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by machaval on 6/17/14.
 */
public class GulashApplication implements MuleApplication
{

    private File groovyFile;

    public GulashApplication(File groovyFile)
    {
        this.groovyFile = groovyFile;
    }

    @Override
    public void initialize()
    {

    }

    @Override
    public List<MuleModule> getModules()
    {
        return Arrays.<MuleModule>asList(new GulashModule(groovyFile, getAppHome()));
    }


    @Override
    public String getName()
    {
        return groovyFile.getName();
    }

    @Override
    public File getAppHome()
    {
        return groovyFile.getParentFile();
    }

    @Override
    public ClassLoader getApplicationClassLoader()
    {
        return null;
    }

    @Override
    public MuleEnvironment getEnvironment()
    {
        return new MuleEnvironment(System.getProperties());
    }
}
