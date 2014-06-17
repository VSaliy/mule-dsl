package org.mule.module.core.builder;

/**
 * Created by machaval on 6/6/14.
 */
public enum LoggerLevel
{
    WARNING
            {
                @Override
                String getLevel()
                {
                    return "WARN";
                }
            }, ERROR
        {
            @Override
            String getLevel()
            {
                return "ERROR";
            }
        }, INFO
        {
            @Override
            String getLevel()
            {
                return "INFO";
            }
        }, DEBUG
        {
            @Override
            String getLevel()
            {
                return "DEBUG";
            }
        };

    abstract String getLevel();
}
