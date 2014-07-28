package org.mule.module.core.processor;

import org.mule.api.MuleContext;
import org.mule.api.transport.PropertyScope;
import org.mule.config.dsl.Builder;

/**
 * Executes the expression and sets the result as a message property using the specified name and scope
 */
public class SetPropertyMessageProcessorBuilder implements Builder<SetPropertyMessageProcessor>
{

    private Expression<?> expression;
    private String name;
    private PropertyScope scope;

    public SetPropertyMessageProcessorBuilder(String name, Expression<?> expression, PropertyScope scope)
    {
        this.name = name;
        this.expression = expression;
        this.scope = scope;
    }


    @Override
    public SetPropertyMessageProcessor create(MuleContext muleContext)
    {
        return new SetPropertyMessageProcessor(expression, name, scope);
    }
}
