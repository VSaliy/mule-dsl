package org.mule.module.tcp;

import static org.mule.module.Core.inbound;
import static org.mule.module.Core.flow;
import static org.mule.module.Core.log;
import static org.mule.module.Tcp.direct;
import static org.mule.module.Tcp.tcpPollingConfig;
import org.mule.api.MuleException;
import org.mule.module.core.Mule;

/**
 * Created by machaval on 5/25/14.
 */
public class TCPConsumerExample
{

    public static void main(String[] args) throws MuleException
    {
        final Mule mule = new Mule();
        mule.declare(tcpPollingConfig().protocol(direct()).as("tcp"));
        mule.declare(flow("Test")
                             .on(inbound("tcp://ota.iambic.com:17").requestResponse().using("tcp"))
                             .then(log("Ok -> #[new String(payload)]").as("WARN"))
        );
        mule.start();
    }

}
