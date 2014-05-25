package org.mule.module.tcp;


import static org.mule.module.Core.inbound;
import static org.mule.module.Core.flow;
import static org.mule.module.Core.setPayload;
import static org.mule.module.Tcp.tcpConfig;
import static org.mule.module.Tcp.direct;
import org.mule.api.MuleException;
import org.mule.module.core.Mule;


public class TCPServerExample
{

    public static void main(String[] args) throws MuleException
    {
        final Mule mule = new Mule();
        mule.declare(tcpConfig().protocol(direct()).as("tcp"));
        mule.declare(flow("Test")
                             .on(inbound("tcp://localhost:4444").requestResponse().using("tcp"))
                             .then(setPayload("Ok -> #[new String(payload)]"))
        );
        mule.start();
    }

}
