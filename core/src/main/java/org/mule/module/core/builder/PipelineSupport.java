package org.mule.module.core.builder;

import org.mule.api.processor.MessageProcessor;
import org.mule.config.dsl.Builder;

/**
 * Created by machaval on 7/3/14.
 */
public interface PipelineSupport<T extends Builder>
{

    T then(Builder<? extends MessageProcessor>... builder);
}
