package org.mule.module.tcp;

import static org.mule.module.Core.inbound;
import static org.mule.module.Core.flow;
import static org.mule.module.Core.log;
import static org.mule.module.Tcp.direct;
import static org.mule.module.Tcp.tcpPollingConfig;
import static org.mule.module.core.builder.LoggerLevel.*;
import org.mule.api.MuleException;
import org.mule.module.core.Mule;
import org.mule.module.core.TimePeriod;


/**
 * Created by machaval on 5/25/14.
 */
public class TCPConsumerExample
{

    public static void main(String[] args) throws MuleException
    {
        final Mule mule = new Mule();
        mule.declare(tcpPollingConfig()
                             .every(10, TimePeriod.SECONDS)
                             .protocol(direct())
                             .as("tcp"));
        mule.declare(flow("Test")
                             .on(
                                     inbound("tcp://ota.iambic.com:17")
                                             .requestResponse()
                                             .using("tcp")
                             )
                             .then(
                                     log("Ok -> #[new String(payload)]").as(WARNING)
                             )
        );
        mule.start();
    }

}
