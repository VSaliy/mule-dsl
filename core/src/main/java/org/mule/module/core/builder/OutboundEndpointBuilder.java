package org.mule.module.core.builder;

import org.mule.MessageExchangePattern;
import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.endpoint.EndpointException;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.transport.Connector;
import org.mule.config.dsl.Builder;
import org.mule.endpoint.EndpointURIEndpointBuilder;

import org.apache.commons.lang.StringUtils;


public class OutboundEndpointBuilder implements Builder<OutboundEndpoint>
{

    private String url;
    private MessageExchangePattern exchangePattern;
    private String connectorName;

    public OutboundEndpointBuilder(String url)
    {
        this.url = url;
    }

    public OutboundEndpointBuilder onWay()
    {
        this.exchangePattern = MessageExchangePattern.ONE_WAY;
        return this;
    }

    public OutboundEndpointBuilder requestResponse()
    {
        this.exchangePattern = MessageExchangePattern.REQUEST_RESPONSE;
        return this;
    }

    public OutboundEndpointBuilder using(String connectorName)
    {
        this.connectorName = connectorName;
        return this;
    }


    @Override
    public OutboundEndpoint create(MuleContext muleContext)
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
            final OutboundEndpoint endpoint = endpointURIEndpointBuilder.buildOutboundEndpoint();
            muleContext.getRegistry().registerEndpoint(endpoint);
            return endpoint;
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
