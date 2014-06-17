package org.mule.module.core.builder;

import org.mule.api.MuleContext;
import org.mule.api.processor.LoggerMessageProcessor;

import org.apache.commons.lang.StringUtils;


public class LoggerBuilder implements MessageProcessorBuilder<LoggerMessageProcessor>
{

    private String message;
    private LoggerLevel level;

    public LoggerBuilder(String message)
    {
        this.message = message;
    }

    public LoggerBuilder as(LoggerLevel level)
    {
        this.level = level;
        return this;
    }


    @Override
    public LoggerMessageProcessor create(MuleContext muleContext)
    {
        LoggerMessageProcessor logger = new LoggerMessageProcessor();
        if (!StringUtils.isEmpty(message))
        {
            logger.setMessage(message);
        }
        if (level != null)
        {
            logger.setLevel(level.getLevel());
        }
        return logger;
    }
}
