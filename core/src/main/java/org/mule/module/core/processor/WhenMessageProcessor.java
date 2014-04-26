package org.mule.module.core.processor;

import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.processor.MessageProcessorChain;
import org.mule.api.routing.filter.Filter;


public class WhenMessageProcessor implements MessageProcessor
{

    private Filter condition;
    private MessageProcessorChain when;
    private MessageProcessorChain otherWise;


    public WhenMessageProcessor(Filter condition)
    {
        this.condition = condition;
    }


    @Override
    public MuleEvent process(final MuleEvent event) throws MuleException
    {
        MuleEvent result = event;


        if (condition.accept(event.getMessage()))
        {
            result = when.process(event);
        }
        else if (otherWise != null)
        {
            result = otherWise.process(event);
        }


        return result;
    }

    public void setWhen(MessageProcessorChain when)
    {
        this.when = when;
    }

    public void setOtherWise(MessageProcessorChain otherWise)
    {
        this.otherWise = otherWise;
    }
}
