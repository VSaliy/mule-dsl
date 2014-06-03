package org.mule.module;


import org.mule.api.processor.MessageProcessor;
import org.mule.api.routing.filter.Filter;
import org.mule.config.dsl.Builder;
import org.mule.module.core.builder.AsyncBuilder;
import org.mule.module.core.builder.ChoiceBuilder;
import org.mule.module.core.builder.CustomMessageProcessorBuilder;
import org.mule.module.core.builder.CustomMessageProcessorBuilderImpl;
import org.mule.module.core.builder.EnricherBuilder;
import org.mule.module.core.builder.FlowBuilder;
import org.mule.module.core.builder.FlowBuilderImpl;
import org.mule.module.core.builder.FlowRefBuilder;
import org.mule.module.core.builder.ForeachBuilder;
import org.mule.module.core.builder.InlineMessageProcessorBuilder;
import org.mule.module.core.builder.MapBuilder;
import org.mule.module.core.builder.OutboundEndpointBuilder;
import org.mule.module.core.builder.WhenBuilder;
import org.mule.module.core.builder.InboundEndpointBuilder;
import org.mule.module.core.builder.JavaBeanElementBuilder;
import org.mule.module.core.builder.LoggerBuilder;
import org.mule.module.core.builder.PollBuilder;
import org.mule.module.core.builder.SetPayloadBuilder;
import org.mule.module.core.filter.EmptyPayloadFilter;
import org.mule.module.core.filter.NullPayloadFilter;
import org.mule.routing.filters.ExpressionFilter;
import org.mule.routing.filters.logic.AndFilter;
import org.mule.routing.filters.logic.NotFilter;
import org.mule.routing.filters.logic.OrFilter;

import java.util.Arrays;
import java.util.Map;

public class Core
{

    public static LoggerBuilder log(String message)
    {
        return new LoggerBuilder(message);
    }

    public static ForeachBuilder foreach()
    {
        return new ForeachBuilder();
    }

    public static ForeachBuilder foreach(String collectionExpression)
    {
        return new ForeachBuilder(collectionExpression);
    }

    public static FlowBuilder flow(String name)
    {
        return new FlowBuilderImpl(name);
    }

    public static EnricherBuilder enrich(String target)
    {
        return new EnricherBuilder(target);
    }

    public static SetPayloadBuilder setPayload(String expression)
    {
        return new SetPayloadBuilder(expression);
    }

    public static PollBuilder poll(Builder<MessageProcessor> pollOver)
    {
        return new PollBuilder(pollOver);
    }

    public static ChoiceBuilder choice()
    {
        return new ChoiceBuilder();
    }

    public static WhenBuilder when(String expression)
    {
        return new WhenBuilder(expression(expression));
    }

    public static WhenBuilder when(Filter condition)
    {
        return new WhenBuilder(condition);
    }

    public static AsyncBuilder async(Builder<MessageProcessor>... messageProcessors)
    {
        return new AsyncBuilder(Arrays.asList(messageProcessors));
    }

    public static <T> JavaBeanElementBuilder<T> bean(Class<T> globalElementClass)
    {
        return new JavaBeanElementBuilder<T>(globalElementClass);
    }

    public static FlowRefBuilder callFlow(String flowName)
    {
        return new FlowRefBuilder(flowName);
    }

    public static InboundEndpointBuilder inbound(String address)
    {
        return new InboundEndpointBuilder(address);
    }

    public static OutboundEndpointBuilder outbound(String address)
    {
        return new OutboundEndpointBuilder(address);
    }

    public static <T extends MessageProcessor> CustomMessageProcessorBuilder<T> process(Class<T> clazz)
    {
        return new CustomMessageProcessorBuilderImpl<T>(clazz);
    }

    public static <T extends MessageProcessor> CustomMessageProcessorBuilder<T> process(Class<T> clazz, Map<String, Object> properties)
    {
        return new CustomMessageProcessorBuilderImpl<T>(clazz, properties);
    }

    public static InlineMessageProcessorBuilder process(MessageProcessor messageProcessor)
    {
        return new InlineMessageProcessorBuilder(messageProcessor);
    }


    public static <K, V> MapBuilder<K, V> map(K key)
    {
        return new MapBuilder<K, V>(key);
    }


    public static Filter and(Filter... filters)
    {
        return new AndFilter(filters);
    }

    public static Filter not(Filter filter)
    {
        return new NotFilter(filter);
    }

    public static Filter expression(String expression)
    {
        return new ExpressionFilter(expression);
    }

    public static Filter or(Filter... filters)
    {
        return new OrFilter(filters);
    }

    public static Filter nullPayload()
    {
        return new NullPayloadFilter();
    }

    public static Filter emptyPayload()
    {
        return new EmptyPayloadFilter();
    }
}
