package org.mule.module.core.builder;

import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.processor.MessageProcessorChain;
import org.mule.config.dsl.Builder;
import org.mule.module.core.BuilderConfigurationException;
import org.mule.processor.chain.DefaultMessageProcessorChainBuilder;

import java.util.Arrays;
import java.util.List;


public class ChainBuilder extends AbstractPipelineBuilder implements MessageProcessorBuilder<MessageProcessorChain>
{


    public ChainBuilder chain(Builder<? extends MessageProcessor>... messageProcessors)
    {
        getMessageProcessorBuilders().addAll(Arrays.asList(messageProcessors));
        return this;
    }

    @Override
    public MessageProcessorChain create(MuleContext muleContext)
    {
        List<MessageProcessor> messageProcessors = buildPipelineMessageProcessors(muleContext);
        try
        {
            return new DefaultMessageProcessorChainBuilder().chain(messageProcessors).build();
        }
        catch (MuleException e)
        {
            throw new BuilderConfigurationException("Exception while creating chain.", e);
        }
    }
}
