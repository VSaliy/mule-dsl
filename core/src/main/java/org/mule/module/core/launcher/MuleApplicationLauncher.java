package org.mule.module.core.launcher;


import org.mule.api.MuleException;
import org.mule.construct.Flow;
import org.mule.module.core.Mule;
import org.mule.module.core.StartListener;
import org.mule.module.core.application.MuleApplication;
import org.mule.module.core.application.MuleModule;

import java.util.List;

public class MuleApplicationLauncher
{

    private Object[] arguments;

    public MuleApplicationLauncher(Object... arguments)
    {
        this.arguments = arguments;
    }

    public void start(final MuleApplication muleApplication)
    {
        System.out.println("Starting -> " + muleApplication.getName());
        final Mule mule = new Mule(muleApplication.getAppHome());
        muleApplication.initialize();
        final ClassLoader applicationClassLoader = muleApplication.getApplicationClassLoader();
        if(applicationClassLoader != null)
        {
            Thread.currentThread().setContextClassLoader(applicationClassLoader);
        }
        final List<MuleModule> modules = muleApplication.getModules();
        for (MuleModule module : modules)
        {
            module.initialize();
        }

        for (MuleModule module : modules)
        {
            module.start(mule);
        }
        try
        {
            mule.start();
        }
        catch (MuleException e)
        {
           throw new RuntimeException(e);
        }

        mule.onStart(new StartListener()
        {
            @Override
            public void onStart(Mule mule)
            {
                final Object main = mule.get("main");
                if (main instanceof Flow)
                {
                    try
                    {
                        mule.callFlow("main", arguments);
                    }
                    catch (MuleException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            public void run()
            {
                try
                {
                    //Stop mule when killing the vm
                    System.out.println("Stoping -> " + muleApplication.getName());
                    mule.stop();
                }
                catch (MuleException e)
                {
                    throw new RuntimeException(e);
                }
            }
        });
    }

}
