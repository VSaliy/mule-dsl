package org.mule.module.core.application;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;


public class MuleEnvironment
{

    private List<Properties> properties;

    public MuleEnvironment(List<Properties> properties)
    {
        this.properties = properties;
    }

    public MuleEnvironment(Properties... properties)
    {
        this.properties = Arrays.asList(properties);
    }


    public String getStringProperty(String name)
    {
        return getStringProperty(name, null);
    }

    public String getStringProperty(String name, String defaultValue)
    {
        for (Properties property : properties)
        {
            if (property.containsKey(name))
            {
                return String.valueOf(property.getProperty(name));
            }
        }
        return defaultValue;
    }

    public Integer getIntProperty(String name)
    {
        return getIntProperty(name, null);
    }

    public Integer getIntProperty(String name, Integer defaultValue)
    {
        final String stringProperty = getStringProperty(name);
        return stringProperty != null ? Integer.parseInt(stringProperty) : defaultValue;
    }

    public Long getLongProperty(String name)
    {
        return getLongProperty(name, null);
    }

    public Long getLongProperty(String name, Long defaultValue)
    {
        final String stringProperty = getStringProperty(name);
        return stringProperty != null ? Long.parseLong(stringProperty) : defaultValue;
    }

    public Double getDoubleProperty(String name)
    {
        return getDoubleProperty(name, null);
    }

    public Double getDoubleProperty(String name, Double defaultValue)
    {
        final String stringProperty = getStringProperty(name);
        return stringProperty != null ? Double.parseDouble(stringProperty) : defaultValue;
    }
}
