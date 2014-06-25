package org.mule.module.core.builder;

import org.mule.api.MuleContext;
import org.mule.api.exception.MessagingExceptionHandler;
import org.mule.api.exception.MessagingExceptionHandlerAcceptor;
import org.mule.api.processor.MessageProcessor;
import org.mule.config.dsl.Builder;
import org.mule.exception.CatchMessagingExceptionStrategy;
import org.mule.exception.ChoiceMessagingExceptionStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CatchExceptionStrategyBuilder implements Builder<MessagingExceptionHandler>
{


    private String current;
    private Map<String, List<Builder<MessageProcessor>>> messageProcessors = new HashMap<String, List<Builder<MessageProcessor>>>();

    public CatchExceptionStrategyBuilder(Class<? extends Exception> when)
    {
        _catch(when);
    }

    public CatchExceptionStrategyBuilder(String when)
    {
        _catch(when);
    }

    public CatchExceptionStrategyBuilder _catch(String when)
    {
        this.current = when;
        messageProcessors.put(current, new ArrayList<Builder<MessageProcessor>>());
        return this;
    }

    public CatchExceptionStrategyBuilder _catch(Class<? extends Exception> when)
    {
        return _catch("#[exception.causedBy(" + when.getName() + ")]");
    }

    public CatchExceptionStrategyBuilder then(Builder<MessageProcessor>... builders)
    {
        messageProcessors.get(current).addAll(Arrays.asList(builders));
        return this;
    }


    @Override
    public MessagingExceptionHandler create(MuleContext muleContext)
    {
        ChoiceMessagingExceptionStrategy result = new ChoiceMessagingExceptionStrategy();
        List<MessagingExceptionHandlerAcceptor> exceptionHandlerAcceptorList = new ArrayList<MessagingExceptionHandlerAcceptor>();
        for (Map.Entry<String, List<Builder<MessageProcessor>>> exceptionHandler : messageProcessors.entrySet())
        {
            final CatchMessagingExceptionStrategy catchMessagingExceptionStrategy = new CatchMessagingExceptionStrategy();
            catchMessagingExceptionStrategy.setMuleContext(muleContext);
            catchMessagingExceptionStrategy.setWhen(exceptionHandler.getKey());
            catchMessagingExceptionStrategy.setMessageProcessors(BuilderUtils.build(muleContext, exceptionHandler.getValue()));
            exceptionHandlerAcceptorList.add(catchMessagingExceptionStrategy);
        }
        result.setExceptionListeners(exceptionHandlerAcceptorList);
        result.setMuleContext(muleContext);
        return result;
    }
}
