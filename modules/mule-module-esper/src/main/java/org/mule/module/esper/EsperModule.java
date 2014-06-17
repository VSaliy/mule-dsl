/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

/**
 * This file was automatically generated by the Mule Development Kit
 */
package org.mule.module.esper;

import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.Module;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.Source;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;
import org.mule.api.callback.SourceCallback;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.SafeIterator;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

/**
 * A Mule Module for <a href="http://esper.codehaus.org/">Esper</a>, a GPL licensed complex event processing
 * engine.
 */
@Module(name = "esper", schemaVersion = "1.0", poolable = false, friendlyName = "Esper")
public class EsperModule
{

    protected transient Log logger = LogFactory.getLog(getClass());

    private EPServiceProvider esperServiceProvider;


    private Map<String, EPStatement> filterStatements = new HashMap<String, EPStatement>();

    /**
     * The location of the Esper config file.  All event types need to be defined here.
     */
    @Configurable
    private Map<String, Class> events;


    @PostConstruct
    public void initialize()
    {

        Configuration configuration = new Configuration();
        if (events != null)
        {
            for (Map.Entry<String, Class> eventEntry : events.entrySet())
            {
                configuration.addEventType(eventEntry.getKey(), eventEntry.getValue());
            }

        }
        esperServiceProvider = EPServiceProviderManager.getDefaultProvider(configuration);
    }

    @PreDestroy
    public void disconnect()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Destroying EsperServiceProvider");
        }
        esperServiceProvider.destroy();
    }

    /**
     * Sends events to an Esper event stream.
     * <p/>
     * Events can be Java objects, Maps or an XML <code>Node</code>.  All event types must be registered in the
     * Esper configuration.
     * <p/>
     * <p/>
     * {@sample.xml ../../../doc/Esper-connector.xml.sample esper:send-event}
     *
     * @param eventPayload The event to be injected into the event stream.
     * @param eventName    The name of the event in the case of a <code>Map</code> payload.
     */
    @Processor(name = "sendEvent")
    public void sendEvent(@Optional @Default("#[payload]") Object eventPayload, @Optional String eventName)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(String.format("Sending event %s to stream", eventPayload));
        }

        if (eventPayload instanceof Map)
        {
            if (StringUtils.isBlank(eventName))
            {
                throw new EsperException("The attribute eventName must be specified with map events.");
            }

            esperServiceProvider.getEPRuntime().sendEvent((Map) eventPayload, eventName);
        }
        else if (eventPayload instanceof Node)
        {
            esperServiceProvider.getEPRuntime().sendEvent((Node) eventPayload);
        }
        else
        {
            esperServiceProvider.getEPRuntime().sendEvent(eventPayload);
        }
    }

    /**
     * Listens for events matching the specified query statement.
     * <p/>
     * {@sample.xml ../../../doc/Esper-connector.xml.sample esper:listenEvent}
     *
     * @param statement The Esper statement to select events from a stream.
     * @param callback  The callback to be called when a message is received
     */
    @Source
    public void listenEvent(String statement, final SourceCallback callback)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Listening for events with statement: " + statement);
        }
        EPStatement epStatement = esperServiceProvider.getEPAdministrator().createEPL(statement);
        epStatement.addListener(new SourceCallbackUpdateListener(callback));
    }


    /**
     * Filters messages that match the supplied EPL query.  The EPL query <b>must</b> return a Boolean
     * value.
     * <p/>
     * {@sample.xml ../../../doc/Esper-connector.xml.sample esper:filter}
     *
     * @param eventPayload The event to be injected into the event stream.  This is useful if your filter statement
     *                     relies on the event being injected into the stream beforehand.
     * @param statement    the EPL statement
     * @param key          the key of the result to evaluate from the query
     * @param afterChain   the <code>SourceCallback</code>
     */
    @Processor(intercepting = true, name = "filter")
    public synchronized void filter(@Optional Object eventPayload, String statement, String key,
                                    SourceCallback afterChain)
    {

        /*
         ToDo it would be nice if we didn't have to synchronize this entire method.  Despite the use
         of SafeIterator, however, the queries don't seem to work unless they're guarded.
          */

        esperServiceProvider.getEPRuntime().sendEvent(eventPayload);

        EPStatement filterStatement;

        if (!filterStatements.containsKey(statement))
        {
            filterStatement = esperServiceProvider.getEPAdministrator().createEPL(statement);
            filterStatements.put(statement, filterStatement);
        }
        else
        {
            filterStatement = filterStatements.get(statement);
        }

        SafeIterator<EventBean> safeIterator = filterStatement.safeIterator();

        try
        {
            Boolean result = (Boolean) safeIterator.next().get(key);

            if (safeIterator.hasNext())
            {
                logger.warn("Statement contains more then one response.");
            }

            if (!result)
            {
                logger.debug("Not passing message, filter expression evaluated to true.");
            }
            else
            {
                afterChain.process();
            }

        }
        catch (Exception e)
        {
            throw new EsperException(e);
        }
        finally
        {
            safeIterator.close();
        }

    }

    //Getter setter methods
    public Map<String, Class> getEvents()
    {
        return events;
    }

    public void setEvents(Map<String, Class> events)
    {
        this.events = events;
    }

}
