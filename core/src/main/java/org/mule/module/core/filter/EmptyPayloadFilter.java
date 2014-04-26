package org.mule.module.core.filter;

import org.mule.api.MuleMessage;
import org.mule.api.routing.filter.Filter;

import java.lang.reflect.Array;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;

/**
 * Checks for empty payload
 */
public class EmptyPayloadFilter implements Filter
{

    private NullPayloadFilter nullPayloadFilter = new NullPayloadFilter();

    @Override
    public boolean accept(MuleMessage message)
    {
        final Object payload = message.getPayload();

        if (nullPayloadFilter.accept(message))
        {
            return true;
        }
        else if (payload instanceof String)
        {
            return StringUtils.isEmpty((String) payload);
        }
        else if (payload instanceof Collection)
        {
            return ((Collection) payload).isEmpty();
        }
        else if (payload.getClass().isArray())
        {
            return Array.getLength(payload) == 0;
        }
        return false;
    }
}
