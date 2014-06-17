package org.mule.module.http.builder;

import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.config.dsl.Builder;
import org.mule.module.core.TimePeriod;
import org.mule.transport.http.HttpPollingConnector;

/**
 * Created by machaval on 6/11/14.
 */
public class PollingHttpConfigurationBuilder implements Builder<HttpPollingConnector>
{


    private Long frequency;
    private Boolean reuseAddress;
    private String name;

    public PollingHttpConfigurationBuilder every(long time, TimePeriod period)
    {
        this.frequency = period.toMillis(time);
        return this;
    }

    public PollingHttpConfigurationBuilder as(String name)
    {
        this.name = name;
        return this;
    }

    public PollingHttpConfigurationBuilder enableReuseAddress()
    {
        this.reuseAddress = true;
        return this;
    }

    public PollingHttpConfigurationBuilder disableReuseAddress()
    {
        this.reuseAddress = false;
        return this;
    }

    @Override
    public HttpPollingConnector create(MuleContext muleContext)
    {
        final HttpPollingConnector httpPollingConnector = new HttpPollingConnector(muleContext);
        if (frequency != null)
        {
            httpPollingConnector.setPollingFrequency(frequency);
        }
        if (reuseAddress != null)
        {
            httpPollingConnector.setReuseAddress(reuseAddress);
        }



        try
        {
            muleContext.getRegistry().registerConnector(httpPollingConnector);
        }
        catch (MuleException e)
        {
            throw new RuntimeException(e);
        }
        return httpPollingConnector;
    }
}
