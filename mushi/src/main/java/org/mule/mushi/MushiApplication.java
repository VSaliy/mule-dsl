package org.mule.mushi;

import org.mule.module.core.application.MuleApplication;
import org.mule.module.core.application.MuleModule;
import org.mule.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * Created by machaval on 6/18/14.
 */
public class MushiApplication implements MuleApplication
{


    private File applicationHome;
    private List<MuleModule> modules;
    private ClassLoader classLoader;

    public MushiApplication(File applicationHome)
    {
        this.applicationHome = applicationHome;
    }

    @Override
    public void initialize()
    {
        initClassLoader();
        initModules();

    }

    //Todo improve to have a better classloader system. With better dependency isolation
    private void initClassLoader()
    {
        final File lib = new File(applicationHome, "lib");
        final Collection<File> jars = FileUtils.listFiles(lib, new String[] {"jar"}, true);
        URL[] urls = new URL[jars.size()];
        int i = 0;
        for (File jar : jars)
        {

            try
            {
                urls[i] = jar.toURI().toURL();
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            i++;
        }
        classLoader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
    }

    private void initModules()
    {
        modules = new ArrayList<MuleModule>();
        final Properties properties = new Properties();
        try
        {
            properties.load(classLoader.getResourceAsStream("mule-deploy.properties"));
            final String configs = String.valueOf(properties.get("modules"));
            final String[] modules = configs.split(",");
            for (String module : modules)
            {
                try
                {
                    this.modules.add((MuleModule) Class.forName(module, true, classLoader).newInstance());
                }
                catch (InstantiationException e)
                {
                    throw new RuntimeException(e);
                }
                catch (IllegalAccessException e)
                {
                    throw new RuntimeException(e);
                }
                catch (ClassNotFoundException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<MuleModule> getModules()
    {
        return modules;
    }

    @Override
    public String getName()
    {
        return applicationHome.getName();
    }

    @Override
    public File getAppHome()
    {
        return applicationHome;
    }

    @Override
    public ClassLoader getApplicationClassLoader()
    {
        return classLoader;
    }
}
