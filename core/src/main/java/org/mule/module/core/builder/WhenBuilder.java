package org.mule.module.core.builder;

import org.mule.api.MuleContext;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.routing.filter.Filter;
import org.mule.config.dsl.Builder;
import org.mule.module.core.processor.WhenMessageProcessor;


//when(bla)
//  .then(log(),echo())
//.otherwise(log())
public class WhenBuilder implements MessageProcessorBuilder<WhenMessageProcessor>, PipelineSupport<WhenBuilder>
{

    private Filter condition;
    private ChainBuilder whenMessageProcessors;
    private ChainBuilder otherwiseMessageProcessors;
    private boolean otherwise = false;

    public WhenBuilder(Filter condition)
    {
        this.condition = condition;
        this.whenMessageProcessors = new ChainBuilder();
        this.otherwiseMessageProcessors = new ChainBuilder();
    }

    @Override
    public WhenBuilder then(Builder<? extends MessageProcessor>... messageProcessorBuilders)
    {
        if (otherwise)
        {
            otherwiseMessageProcessors.chain(messageProcessorBuilders);
        }
        else
        {
            whenMessageProcessors.chain(messageProcessorBuilders);
        }
        return this;
    }

    public MessageProcessorBuilder<WhenMessageProcessor> otherwise(Builder<MessageProcessor>... messageProcessorBuilders)
    {
        otherwise = true;
        otherwiseMessageProcessors.chain(messageProcessorBuilders);
        return this;
    }

    @Override
    public WhenMessageProcessor create(MuleContext muleContext)
    {
        final WhenMessageProcessor result = new WhenMessageProcessor(condition);
        result.setWhen(whenMessageProcessors.create(muleContext));
        result.setOtherWise(otherwiseMessageProcessors.create(muleContext));
        return result;
    }

}
