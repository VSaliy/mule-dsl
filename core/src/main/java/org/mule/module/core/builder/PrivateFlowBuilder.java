package org.mule.module.core.builder;

import org.mule.api.exception.MessagingExceptionHandler;
import org.mule.api.processor.MessageProcessor;
import org.mule.config.dsl.Builder;
import org.mule.construct.Flow;


public interface PrivateFlowBuilder extends MessageProcessorBuilder<Flow>
{

    PrivateFlowBuilder then(Builder<? extends MessageProcessor>... builder);

    MessageProcessorBuilder<Flow> onException(Builder<MessagingExceptionHandler>... exceptionBuilder);

}
