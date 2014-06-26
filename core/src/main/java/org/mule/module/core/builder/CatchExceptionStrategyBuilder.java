package org.mule.module.core.builder;

import org.mule.api.MuleContext;
import org.mule.api.exception.MessagingExceptionHandler;
import org.mule.api.processor.MessageProcessor;
import org.mule.config.dsl.Builder;
import org.mule.exception.CatchMessagingExceptionStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CatchExceptionStrategyBuilder implements Builder<MessagingExceptionHandler>
{


    private String when;
    private List<Builder<MessageProcessor>> messageProcessors = new ArrayList<Builder<MessageProcessor>>();

    public CatchExceptionStrategyBuilder(Class<? extends Exception> when)
    {
        this("#[exception.causedBy(" + when.getName() + ")]");
    }

    public CatchExceptionStrategyBuilder(String when)
    {
        this.when = when;
    }

    public CatchExceptionStrategyBuilder then(Builder<MessageProcessor>... builders)
    {
        messageProcessors.addAll(Arrays.asList(builders));
        return this;
    }

    @Override
    public MessagingExceptionHandler create(MuleContext muleContext)
    {

        final CatchMessagingExceptionStrategy catchMessagingExceptionStrategy = new CatchMessagingExceptionStrategy();
        catchMessagingExceptionStrategy.setMuleContext(muleContext);
        catchMessagingExceptionStrategy.setWhen(when);
        catchMessagingExceptionStrategy.setMessageProcessors(BuilderUtils.build(muleContext, messageProcessors));
        return catchMessagingExceptionStrategy;
    }
}
