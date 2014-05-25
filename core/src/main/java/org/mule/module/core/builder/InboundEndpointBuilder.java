package org.mule.module.core.builder;

import org.mule.MessageExchangePattern;
import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.endpoint.EndpointException;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.transport.Connector;
import org.mule.config.dsl.Builder;
import org.mule.endpoint.EndpointURIEndpointBuilder;

import org.apache.commons.lang.StringUtils;


public class InboundEndpointBuilder implements Builder<InboundEndpoint>
{

    private String url;
    private MessageExchangePattern exchangePattern;
    private String connectorName;

    public InboundEndpointBuilder(String url)
    {
        this.url = url;
    }

    public InboundEndpointBuilder onWay()
    {
        this.exchangePattern = MessageExchangePattern.ONE_WAY;
        return this;
    }

    public InboundEndpointBuilder requestResponse()
    {
        this.exchangePattern = MessageExchangePattern.REQUEST_RESPONSE;
        return this;
    }

    public InboundEndpointBuilder using(String connectorName)
    {
        this.connectorName = connectorName;
        return this;
    }


    @Override
    public InboundEndpoint create(MuleContext muleContext)
    {
        final EndpointURIEndpointBuilder endpointURIEndpointBuilder = new EndpointURIEndpointBuilder(url, muleContext);
        try
        {
            if (!StringUtils.isBlank(connectorName))
            {
                final Connector connector = muleContext.getRegistry().lookupObject(connectorName);
                endpointURIEndpointBuilder.setConnector(connector);
            }

            if (exchangePattern != null)
            {
                endpointURIEndpointBuilder.setExchangePattern(exchangePattern);
            }

            endpointURIEndpointBuilder.setMuleContext(muleContext);
            final InboundEndpoint inboundEndpoint = endpointURIEndpointBuilder.buildInboundEndpoint();
            muleContext.getRegistry().registerEndpoint(inboundEndpoint);
            return inboundEndpoint;
        }
        catch (EndpointException e)
        {
            throw new IllegalStateException(e);
        }
        catch (InitialisationException e)
        {
            throw new IllegalStateException(e);
        }
        catch (MuleException e)
        {
            throw new IllegalStateException(e);
        }
    }
}
