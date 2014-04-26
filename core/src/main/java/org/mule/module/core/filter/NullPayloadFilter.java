package org.mule.module.core.filter;

import org.mule.api.MuleMessage;
import org.mule.api.routing.filter.Filter;
import org.mule.transport.NullPayload;


public class NullPayloadFilter implements Filter
{

    @Override
    public boolean accept(MuleMessage message)
    {
        final Object payload = message.getPayload();
        return payload == null || payload instanceof NullPayload;
    }
}
