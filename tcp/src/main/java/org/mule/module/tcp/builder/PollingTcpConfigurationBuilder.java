package org.mule.module.tcp.builder;


import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.config.dsl.Builder;
import org.mule.module.core.TimePeriod;
import org.mule.transport.tcp.PollingTcpConnector;
import org.mule.transport.tcp.TcpMessageDispatcherFactory;
import org.mule.transport.tcp.TcpProtocol;

import org.apache.commons.lang.StringUtils;

public class PollingTcpConfigurationBuilder implements Builder<PollingTcpConnector>
{

    private Builder<TcpProtocol> protocolBuilder;
    private String name;
    private int clientSoTimeout = 10000;
    private Long frequency;

    public PollingTcpConfigurationBuilder protocol(Builder<TcpProtocol> protocolBuilder)
    {
        this.protocolBuilder = protocolBuilder;
        return this;
    }

    public PollingTcpConfigurationBuilder every(long time, TimePeriod period)
    {
        this.frequency = period.toMillis(time);
        return this;
    }

    public PollingTcpConfigurationBuilder as(String name)
    {

        this.name = name;
        return this;
    }

    @Override
    public PollingTcpConnector create(MuleContext muleContext)
    {
        final PollingTcpConnector tcpConnector = new PollingTcpConnector(muleContext);
        if (protocolBuilder != null)
        {
            tcpConnector.setTcpProtocol(protocolBuilder.create(muleContext));
        }
        if (frequency != null)
        {
            tcpConnector.setPollingFrequency(frequency);
        }

        final TcpMessageDispatcherFactory dispatcherFactory = new TcpMessageDispatcherFactory();
        tcpConnector.setDispatcherFactory(dispatcherFactory);
        if (StringUtils.isBlank(name))
        {
            name = "PollingTcpConnector";
        }
        tcpConnector.setClientSoTimeout(clientSoTimeout);
        tcpConnector.setName(name);
        try
        {
            muleContext.getRegistry().registerConnector(tcpConnector);
        }
        catch (MuleException e)
        {
            throw new RuntimeException(e);
        }
        return tcpConnector;
    }


}
