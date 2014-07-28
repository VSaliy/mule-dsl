package org.mule.module.core.builder;

import org.mule.api.MuleContext;
import org.mule.module.core.processor.Expression;
import org.mule.module.core.processor.SetPayloadMessageProcessor;

import org.apache.commons.lang.StringUtils;

/**
 * Executes the expression and sets the result as the payload
 */
public class SetPayloadBuilder implements MessageProcessorBuilder<SetPayloadMessageProcessor>
{

    private Expression expression;
    private String contentType;
    private String encoding;

    public SetPayloadBuilder(Expression<?> expression)
    {
        this.expression = expression;
    }


    public SetPayloadBuilder encoding(String encoding)
    {
        this.encoding = encoding;
        return this;
    }

    public SetPayloadBuilder contentType(String contentType)
    {
        this.contentType = contentType;
        return this;
    }


    @Override
    public SetPayloadMessageProcessor create(MuleContext muleContext)
    {
        final SetPayloadMessageProcessor setPayload = new SetPayloadMessageProcessor(expression);
        if (!StringUtils.isEmpty(encoding))
        {
            setPayload.setEncoding(encoding);
        }
        if (!StringUtils.isEmpty(contentType))
        {
            setPayload.setContentType(contentType);
        }
        return setPayload;
    }
}
