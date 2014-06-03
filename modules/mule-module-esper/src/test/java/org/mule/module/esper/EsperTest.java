package org.mule.module.esper;

import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.processor.MessageProcessor;
import org.mule.module.Core;
import org.mule.module.Esper;
import org.mule.module.core.Mule;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static org.mule.module.Core.*;

/**
 * Created by machaval on 5/30/14.
 */
public class EsperTest
{

    @Test
    public void basicEsperTest() throws MuleException, InterruptedException
    {

        final Integer[] result = new Integer[] {0};
        Mule mule = new Mule();
        mule.declare(Esper.config(map("SensorEvent").to(SensorEvent.class)).as("SensorEvent"));
        mule.declare(
                flow("TriggerEvent")
                        .on(Esper.listenEvent(withTemperatureMoreThan50()).using("SensorEvent"))
                        .then(process(
                                      new MessageProcessor()
                                      {
                                          public MuleEvent process(MuleEvent event) throws MuleException
                                          {
                                              final SensorEvent payload = event.getMessage().getPayload(SensorEvent.class);
                                              System.out.println("payload = " + payload.getTemperature());
                                              result[0]++;
                                              return event;
                                          }
                                      })
                        )

        );
        mule.declare(
                flow("DispatchEvent")
                        .then(Esper.sendEvent().using("SensorEvent"))
        );

        mule.start();

        Thread.sleep(1000L);


        int i = 0;
        while (i < 100)
        {
            mule.callFlow("DispatchEvent", new SensorEvent(i, "sensor1"));
            i++;
        }

        Thread.sleep(1000L);
        Assert.assertThat(result[0], CoreMatchers.is(50));


    }

    private String withTemperatureMoreThan50()
    {
        return "select * from SensorEvent.win:length(5) having temperature >= 50";
    }

}
