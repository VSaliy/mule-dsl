package org.mule.module;

import org.mule.module.http.builder.PollingHttpConfigurationBuilder;


public class Http
{

    public static PollingHttpConfigurationBuilder httpPollingConfig()
    {
        return new PollingHttpConfigurationBuilder();
    }
}
