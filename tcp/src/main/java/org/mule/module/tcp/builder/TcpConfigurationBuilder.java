package org.mule.module.tcp.builder;

import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.config.dsl.Builder;
import org.mule.transport.tcp.TcpConnector;
import org.mule.transport.tcp.TcpMessageDispatcherFactory;
import org.mule.transport.tcp.TcpProtocol;

import org.apache.commons.lang.StringUtils;


public class TcpConfigurationBuilder implements Builder<TcpConnector>
{

    private Builder<TcpProtocol> protocolBuilder;
    private String name;

    public TcpConfigurationBuilder protocol(Builder<TcpProtocol> protocolBuilder)
    {
        this.protocolBuilder = protocolBuilder;
        return this;
    }

    public TcpConfigurationBuilder as(String name)
    {

        this.name = name;
        return this;
    }

    @Override
    public TcpConnector create(MuleContext muleContext)
    {
        final TcpConnector tcpConnector = createConfig(muleContext);
        if (protocolBuilder != null)
        {
            tcpConnector.setTcpProtocol(protocolBuilder.create(muleContext));
        }
        final TcpMessageDispatcherFactory dispatcherFactory = new TcpMessageDispatcherFactory();
        tcpConnector.setDispatcherFactory(dispatcherFactory);
        if (StringUtils.isBlank(name))
        {
            name = "TcpConnector";
        }
        tcpConnector.setName(name);
        try
        {
            muleContext.getRegistry().registerConnector(tcpConnector);
        }
        catch (MuleException e)
        {

        }
        return tcpConnector;
    }

    protected TcpConnector createConfig(MuleContext muleContext)
    {
        return new TcpConnector(muleContext);
    }
}
