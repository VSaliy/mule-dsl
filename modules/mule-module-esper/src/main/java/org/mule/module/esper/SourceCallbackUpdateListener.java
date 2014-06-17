/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.esper;

import org.mule.api.callback.SourceCallback;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>UpdateListener</code> implementation that invokes the <code>SourceCallback</code> facilitate the
 * dispatch of events as they are read off the event stream.
 */
public class SourceCallbackUpdateListener implements UpdateListener
{

    protected transient Log logger = LogFactory.getLog(getClass());

    SourceCallback sourceCallback;

    public SourceCallbackUpdateListener(SourceCallback sourceCallback)
    {
        this.sourceCallback = sourceCallback;
    }

    // ToDo Figure out how to deal with newEvents vs. oldEvents intelligently
    public void update(EventBean[] newEvents, EventBean[] oldEvents)
    {


        if (newEvents == null)
        {
            logger.debug("Null events collection received");
            return;
        }

        for (EventBean event : newEvents)
        {
            try
            {
                logger.debug("Processing received event: " + event);
                final HashMap<String, Object> variables = new HashMap<String, Object>();
                variables.put("esper.EventType", event.getEventType());
                sourceCallback.process(event.getUnderlying(), variables);
            }
            catch (Exception e)
            {
                logger.error("Could not process event: " + event, e);
            }
        }

    }
}
