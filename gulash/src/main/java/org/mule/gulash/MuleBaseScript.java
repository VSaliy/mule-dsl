package org.mule.gulash;

import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.client.MuleClient;
import org.mule.api.config.ConfigurationException;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.lifecycle.Stoppable;
import org.mule.api.registry.RegistrationException;
import org.mule.common.Result;
import org.mule.common.TestResult;
import org.mule.common.Testable;
import org.mule.config.dsl.Builder;
import org.mule.dependency.DependencyModuleBuilder;
import org.mule.dependency.ModuleDependencyManager;
import org.mule.module.core.Mule;
import org.mule.module.core.StartListener;

import java.io.File;

import groovy.lang.Closure;
import groovy.lang.Script;


/**
 * Created by machaval on 4/13/14.
 */
public abstract class MuleBaseScript extends Script
{


    public Mule declare(Builder<?> builder)
    {
        return getMule().declare(builder);
    }

    public MuleClient client()
    {
        return getMule().client();
    }

    public MuleEvent callFlow(String name, Object payload) throws MuleException
    {
        return getMule().callFlow(name, payload);
    }

    public File getMuleHome()
    {
        return getMule().getMuleAppHome();
    }

    public Mule onStart(final Closure listener)
    {
        return getMule().onStart(new StartListener()
        {
            @Override
            public void onStart(Mule mule)
            {
                listener.run();
            }
        });
    }

    public Mule start() throws MuleException
    {
        return getMule().start();
    }

    public Mule down() throws MuleException
    {
        return getMule().stop();
    }

    public <T> T lookup(Class<T> clazz) throws RegistrationException
    {
        return getMule().lookup(clazz);
    }

    public DependencyModuleBuilder require(String moduleName)
    {
        return getDependencyManager().require(moduleName);
    }

    public Mule getMule()
    {
        return (Mule) getBinding().getVariable("mule");
    }


    public ModuleDependencyManager getDependencyManager()
    {
        return (ModuleDependencyManager) getBinding().getVariable("dependency");
    }


}
