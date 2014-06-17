package org.mule.module.esper;

import static org.mule.module.Core.flow;
import static org.mule.module.Core.log;
import static org.mule.module.Core.map;
import org.mule.module.Esper;
import org.mule.module.core.Mule;
import org.mule.module.core.builder.LoggerLevel;

/**
 * Created by machaval on 6/4/14.
 */
public class EsperExample
{

    public static void main(String[] args) throws Exception
    {
        Mule mule = new Mule();
        mule.declare(Esper.config(map("SensorEvent").to(SensorEvent.class)).as("SensorEvent"));
        mule.declare(
                flow("TriggerEvent")
                        .on(Esper.listenEvent(withTemperatureMoreThan50()).using("SensorEvent"))
                        .then(log("Message Received at ---> #[payload.temperature]").as(LoggerLevel.INFO))

        );
        mule.declare(
                flow("DispatchEvent")
                        .then(Esper.sendEvent().using("SensorEvent"))
        );

        mule.start();

        Thread.sleep(1000L);


        int i = 0;
        while (i < 100000000)
        {
            mule.callFlow("DispatchEvent", new SensorEvent(i, "sensor1"));
            i++;
            Thread.sleep(1000L);
        }


    }


    private static String withTemperatureMoreThan50()
    {
        return "select * from SensorEvent.win:length(5) having temperature >= 50";
    }


}
