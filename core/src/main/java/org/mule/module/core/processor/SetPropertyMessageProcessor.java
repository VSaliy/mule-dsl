package org.mule.module.core.processor;

import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.transport.PropertyScope;


public class SetPropertyMessageProcessor implements MessageProcessor
{


    private Expression setPropertyExpression;
    private String name;
    private PropertyScope propertyScope;

    public SetPropertyMessageProcessor(Expression setPropertyExpression, String name, PropertyScope propertyScope)
    {
        this.setPropertyExpression = setPropertyExpression;
        this.name = name;
        this.propertyScope = propertyScope;
    }

    @Override
    public MuleEvent process(MuleEvent event) throws MuleException
    {


        final Object result = setPropertyExpression.eval(new DefaultUnMutableMuleMessage(event.getMessage()));
        event.getMessage().setProperty(name, result, propertyScope);
        return event;
    }
}
