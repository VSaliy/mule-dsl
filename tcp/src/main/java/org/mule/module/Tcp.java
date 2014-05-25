package org.mule.module;

import org.mule.module.tcp.builder.DirectProtocolBuilder;
import org.mule.module.tcp.builder.LengthProtocolBuilder;
import org.mule.module.tcp.builder.PollingTcpConfigurationBuilder;
import org.mule.module.tcp.builder.TcpConfigurationBuilder;

/**
 * Created by machaval on 5/4/14.
 */
public class Tcp
{

    public static TcpConfigurationBuilder tcpConfig()
    {
        return new TcpConfigurationBuilder();
    }

    public static PollingTcpConfigurationBuilder tcpPollingConfig()
    {
        return new PollingTcpConfigurationBuilder();
    }

    public static DirectProtocolBuilder direct()
    {
        return new DirectProtocolBuilder();
    }

    public static LengthProtocolBuilder length()
    {
        return new LengthProtocolBuilder();
    }


}
