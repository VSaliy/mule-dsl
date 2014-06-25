package org.mule.module.core.builder;

import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.exception.MessagingExceptionHandler;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.processor.ProcessingStrategy;
import org.mule.api.source.MessageSource;
import org.mule.config.dsl.Builder;
import org.mule.construct.Flow;

import java.util.Arrays;
import java.util.List;


public class FlowBuilderImpl extends AbstractPipelineBuilder implements FlowBuilder
{


    private Builder<? extends MessageSource> messageSourceBuilder;
    private String name;
    private Builder<MessagingExceptionHandler> exceptionBuilder;

    public FlowBuilderImpl(String name)
    {
        this.name = name;
    }

    @Override
    public PrivateFlowBuilder on(Builder<? extends MessageSource> messageSourceBuilder)
    {
        this.messageSourceBuilder = messageSourceBuilder;
        return this;
    }

    @Override
    public void onException(Builder<MessagingExceptionHandler> exceptionBuilder)
    {
        this.exceptionBuilder = exceptionBuilder;
    }

    public PrivateFlowBuilder then(Builder<? extends MessageProcessor>... builder)
    {
        getMessageProcessorBuilders().addAll(Arrays.asList(builder));
        return this;
    }

    public Flow create(MuleContext muleContext)
    {

        final List<MessageProcessor> messageProcessorList = buildPipelineMessageProcessors(muleContext);

        final Flow flow = new Flow(name, muleContext);
        if (messageSourceBuilder != null)
        {
            MessageSource messageSource = messageSourceBuilder.create(muleContext);
            flow.setMessageSource(messageSource);
        }
        flow.setMessageProcessors(messageProcessorList);
        if (exceptionBuilder != null)
        {
            flow.setExceptionListener(exceptionBuilder.create(muleContext));
        }
        try
        {
            muleContext.getRegistry().registerFlowConstruct(flow);
        }
        catch (MuleException e)
        {
            e.printStackTrace();
        }
        return flow;
    }
}
