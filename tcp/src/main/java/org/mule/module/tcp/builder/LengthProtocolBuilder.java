package org.mule.module.tcp.builder;

import org.mule.api.MuleContext;
import org.mule.config.dsl.Builder;
import org.mule.transport.tcp.protocols.LengthProtocol;


public class LengthProtocolBuilder implements Builder<LengthProtocol>
{


    private int maxMessageLength = -1;

    public LengthProtocolBuilder maxMessageLength(int maxMessageLength)
    {
        this.maxMessageLength = maxMessageLength;
        return this;
    }


    @Override
    public LengthProtocol create(MuleContext muleContext)
    {
        final LengthProtocol lengthProtocolBuilder = new LengthProtocol();
        if (maxMessageLength != -1)
        {
            lengthProtocolBuilder.setMaxMessageLength(maxMessageLength);
        }
        return lengthProtocolBuilder;
    }
}
