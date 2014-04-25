package org.mule.module.builder;

import org.mule.api.MuleException;
import org.mule.module.Core;
import org.mule.module.core.Mule;

/**
 * Created by machaval on 4/24/14.
 */
public class Test
{

    public static void main(String[] args) throws MuleException
    {
        Mule mule = new Mule();
        mule.declare(Core.flow("test").on(Core.endpoint("tcp://localhost:1234")).then(Core.log("Response #[payload]")));
        mule.start();
    }
}
