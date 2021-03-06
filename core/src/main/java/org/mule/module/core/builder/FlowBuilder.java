package org.mule.module.core.builder;


import org.mule.api.source.MessageSource;
import org.mule.config.dsl.Builder;

public interface FlowBuilder extends PrivateFlowBuilder
{

    PrivateFlowBuilder on(Builder<? extends MessageSource> messageSourceBuilder);

}
