package org.mule.module.core.processor;

import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.processor.MessageProcessor;
import org.mule.transport.NullPayload;

import org.apache.commons.lang.StringUtils;


public class SetPayloadMessageProcessor implements MessageProcessor
{

    public static final String CONTENT_TYPE = "Content-Type";

    private Expression payloadModifier;
    private String encoding;
    private String contentType;

    public SetPayloadMessageProcessor(Expression payloadModifier)
    {
        this.payloadModifier = payloadModifier;
    }

    public String getEncoding()
    {
        return encoding;
    }

    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }

    public String getContentType()
    {
        return contentType;
    }

    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }

    @Override
    public MuleEvent process(MuleEvent event) throws MuleException
    {
        Object payload;
        final Object process = payloadModifier.eval(new DefaultUnMutableMuleMessage(event.getMessage()));
        if (process == null)
        {
            payload = NullPayload.getInstance();
        }
        else
        {
            payload = process;
        }
        event.getMessage().setPayload(payload);
        if (!StringUtils.isEmpty(getEncoding()))
        {
            event.getMessage().setEncoding(getEncoding());
        }
        if (!StringUtils.isEmpty(getContentType()))
        {
            event.getMessage().setOutboundProperty(CONTENT_TYPE, getContentType());
        }
        return event;
    }
}
