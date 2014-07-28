package org.mule.module.core.builder;

import org.mule.api.exception.MessagingExceptionHandler;
import org.mule.api.processor.MessageProcessor;
import org.mule.config.dsl.Builder;
import org.mule.construct.Flow;


public interface PrivateFlowBuilder extends MessageProcessorBuilder<Flow>, PipelineSupport<PrivateFlowBuilder>
{

    MessageProcessorBuilder<Flow> onException(Builder<MessagingExceptionHandler>... exceptionBuilder);

}
