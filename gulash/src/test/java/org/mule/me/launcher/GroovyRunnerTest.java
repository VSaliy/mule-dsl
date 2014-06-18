package org.mule.me.launcher;


import org.mule.gulash.GulashApplication;
import org.mule.module.core.launcher.MuleApplicationLauncher;

import java.io.File;
import java.net.URL;

import org.junit.Test;

/**
 * Created by machaval on 2/11/14.
 */
public class GroovyRunnerTest
{

    @Test
    public void simpleTest() throws Exception
    {
        URL resource = getClass().getClassLoader().getResource("test.groovy");
        new MuleApplicationLauncher().start(new GulashApplication(new File(resource.toURI())));
    }


}
