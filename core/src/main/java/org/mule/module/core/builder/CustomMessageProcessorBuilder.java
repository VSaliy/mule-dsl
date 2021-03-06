package org.mule.module.core.builder;

import org.mule.api.processor.MessageProcessor;

import java.util.Map;


public interface CustomMessageProcessorBuilder<T extends MessageProcessor> extends MessageProcessorBuilder<T>
{

    CustomMessageProcessorBuilder<T> using(Map<String, Object> properties);
}
