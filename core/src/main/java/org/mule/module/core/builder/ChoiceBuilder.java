package org.mule.module.core.builder;

import org.mule.api.MuleContext;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.routing.filter.Filter;
import org.mule.config.dsl.Builder;
import org.mule.module.Core;
import org.mule.module.core.BuilderConfigurationException;
import org.mule.routing.ChoiceRouter;

import java.util.ArrayList;
import java.util.List;


public class ChoiceBuilder implements Builder<ChoiceRouter>, PipelineSupport<ChoiceBuilder>
{

    private List<Filter> filters = new ArrayList<Filter>();
    private List<ChainBuilder> routes = new ArrayList<ChainBuilder>();
    private ChainBuilder otherwise;
    private ChainBuilder current;

    public ChoiceBuilder on(String expression)
    {
        return on(Core.expression(expression));
    }

    public ChoiceBuilder on(Filter filter)
    {
        ChainBuilder route = new ChainBuilder();
        routes.add(route);
        current = route;
        filters.add(filter);
        return this;
    }

    public Builder<ChoiceRouter> otherwise(Builder<? extends MessageProcessor>... messageProcessors)
    {
        otherwise = new ChainBuilder();
        otherwise.chain(messageProcessors);
        return this;
    }

    @Override
    public ChoiceBuilder then(Builder<? extends MessageProcessor>... messageProcessors)
    {
        if (current == null)
        {
            throw new BuilderConfigurationException("Invoke on or otherwise before then method");
        }
        current.chain(messageProcessors);
        return this;
    }


    @Override
    public ChoiceRouter create(MuleContext muleContext)
    {
        final ChoiceRouter choiceRouter = new ChoiceRouter();
        choiceRouter.setMuleContext(muleContext);
        for (int i = 0; i < filters.size(); i++)
        {
            Filter filter = filters.get(i);
            ChainBuilder route = routes.get(i);
            choiceRouter.addRoute(route.create(muleContext), filter);
        }

        choiceRouter.setDefaultRoute(otherwise.create(muleContext));
        choiceRouter.setMuleContext(muleContext);
        return choiceRouter;
    }
}
