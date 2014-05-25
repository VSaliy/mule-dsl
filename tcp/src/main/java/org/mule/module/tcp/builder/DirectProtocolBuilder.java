package org.mule.module.tcp.builder;

import org.mule.api.MuleContext;
import org.mule.config.dsl.Builder;
import org.mule.transport.tcp.TcpProtocol;
import org.mule.transport.tcp.protocols.DirectProtocol;
import org.mule.transport.tcp.protocols.MuleMessageDirectProtocol;


public class DirectProtocolBuilder implements Builder<TcpProtocol>
{

    private boolean payloadOnly = true;

    public DirectProtocolBuilder message()
    {
        payloadOnly = false;
        return this;
    }

    @Override
    public TcpProtocol create(MuleContext muleContext)
    {
        return payloadOnly ? new DirectProtocol() : new MuleMessageDirectProtocol();
    }
}
