package org.mule.mushi;

import org.mule.module.core.application.MuleApplication;
import org.mule.module.core.application.MuleEnvironment;
import org.mule.module.core.application.MuleModule;
import org.mule.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

/**
 * Created by machaval on 6/18/14.
 */
public class MushiApplication implements MuleApplication
{


    public static final String MODULES = "modules";
    public static final String PROPERTIES = "properties";
    public static final String COMMA_SEPARATOR = ",";
    private File applicationHome;
    private String environment;
    private List<MuleModule> modules;
    private ClassLoader classLoader;
    private List<Properties> properties;

    public MushiApplication(File applicationHome, String environment)
    {
        this.applicationHome = applicationHome;
        this.environment = environment;
    }

    @Override
    public void initialize()
    {
        initClassLoader();
        initMuleDeploy();
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

    private void initMuleDeploy()
    {
        modules = new ArrayList<MuleModule>();
        final Properties deployProperties = new Properties();
        try
        {
            final InputStream muleDeployProperties = classLoader.getResourceAsStream("mule-deploy.properties");
            if (muleDeployProperties != null)
            {
                deployProperties.load(muleDeployProperties);
                initModules(deployProperties);
                initProperties(deployProperties);
            }
            else
            {
                throw new RuntimeException("The file mule-deploy.properties was not found");
            }


        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void initModules(Properties deployProperties)
    {
        final String configs;
        if (!StringUtils.isBlank(environment) && deployProperties.containsKey(getModuleWithEnvironment()))
        {
            configs = String.valueOf(deployProperties.get(getModuleWithEnvironment()));
        }
        else if (deployProperties.containsKey(MODULES))
        {
            configs = String.valueOf(deployProperties.get(MODULES));
        }
        else
        {
            throw new RuntimeException("Modules property not specified at mule-deploy.properties");
        }

        loadModules(configs);
    }

    private void initProperties(Properties deployProperties) throws IOException
    {
        this.properties = new ArrayList<Properties>();

        String propertiesValue = null;
        if (!StringUtils.isBlank(environment) && deployProperties.containsKey(getPropertiesWithEnvironment()))
        {
            propertiesValue = String.valueOf(deployProperties.get(getPropertiesWithEnvironment()));
        }
        else if (deployProperties.containsKey(PROPERTIES))
        {
            propertiesValue = String.valueOf(deployProperties.get(PROPERTIES));
        }
        if (propertiesValue != null)
        {
            loadProperties(propertiesValue);
        }

        properties.add(System.getProperties());
    }

    private String getPropertiesWithEnvironment()
    {
        return PROPERTIES + "." + environment;
    }

    private String getModuleWithEnvironment()
    {
        return MODULES + "." + environment;
    }

    private void loadProperties(String propertiesValue) throws IOException
    {
        final String[] propertiesNames = propertiesValue.split(COMMA_SEPARATOR);
        for (String propertiesName : propertiesNames)
        {
            final Properties properties = new Properties();
            final InputStream propertyStream = classLoader.getResourceAsStream(propertiesName);
            if (propertyStream != null)
            {
                properties.load(propertyStream);
                this.properties.add(properties);
            }
            else
            {
                throw new RuntimeException("No property was found with name " + propertiesName);
            }

        }

    }

    private void loadModules(String modulesPropertyValue)
    {
        final String[] modules = modulesPropertyValue.split(COMMA_SEPARATOR);
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

    @Override
    public MuleEnvironment getEnvironment()
    {
        return new MuleEnvironment(this.properties);
    }
}
