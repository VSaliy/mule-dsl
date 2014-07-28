package org.mule.module.tcp;


import static org.mule.module.Core.inbound;
import static org.mule.module.Core.flow;
import static org.mule.module.Core.payload;

import static org.mule.module.Tcp.tcpConfig;
import static org.mule.module.Tcp.direct;
import org.mule.api.MuleException;
import org.mule.module.core.Mule;
import org.mule.module.core.processor.Expression;
import org.mule.module.core.processor.UnMutableMuleMessage;


public class TCPServerExample
{

    public static void main(String[] args) throws MuleException
    {
        final Mule mule = new Mule();
        mule.declare(tcpConfig().protocol(direct()).as("tcp"));
        mule.declare(flow("Test")
                             .on(inbound("tcp://localhost:4444").requestResponse().using("tcp"))
                             .then(payload(new Expression<String>()
                             {

                                 @Override
                                 public String eval(UnMutableMuleMessage muleMessage)
                                 {
                                     return "Ok";
                                 }
                             }))
        );
        mule.start();
    }

}
