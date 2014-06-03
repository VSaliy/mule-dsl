package org.mule.module.core.builder;

import org.mule.api.MuleContext;
import org.mule.api.processor.MessageProcessor;
import org.mule.config.dsl.Builder;


public class InlineMessageProcessorBuilder implements Builder<MessageProcessor>
{


    private MessageProcessor messageProcessor;

    public InlineMessageProcessorBuilder(MessageProcessor messageProcessor)
    {
        this.messageProcessor = messageProcessor;
    }

    @Override
    public MessageProcessor create(MuleContext muleContext)
    {
        return messageProcessor;
    }
}
