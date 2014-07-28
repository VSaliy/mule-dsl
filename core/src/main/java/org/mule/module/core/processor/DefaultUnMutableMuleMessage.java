package org.mule.module.core.processor;

import org.mule.api.MuleMessage;
import org.mule.api.transport.PropertyScope;


public class DefaultUnMutableMuleMessage implements UnMutableMuleMessage
{

    private MuleMessage muleMessage;

    public DefaultUnMutableMuleMessage(MuleMessage muleMessage)
    {
        this.muleMessage = muleMessage;
    }

    @Override
    public Object getPayload()
    {
        return muleMessage.getPayload();
    }

    @Override
    public Object getFlowVar(String name)
    {
        return muleMessage.getInvocationProperty(name);
    }

    @Override
    public Object getSessionVar(String name)
    {
        return muleMessage.getProperty(name, PropertyScope.SESSION);
    }

    @Override
    public Object getInboundProperty(String name)
    {
        return muleMessage.getProperty(name, PropertyScope.INBOUND);
    }

    @Override
    public Object getOutboundProperty(String name)
    {
        return muleMessage.getProperty(name, PropertyScope.OUTBOUND);
    }
}
