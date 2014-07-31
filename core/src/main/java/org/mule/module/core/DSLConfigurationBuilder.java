package org.mule.module.core;

import org.mule.api.config.MuleProperties;
import org.mule.api.config.ThreadingProfile;
import org.mule.api.registry.MuleRegistry;
import org.mule.api.registry.RegistrationException;
import org.mule.config.ChainedThreadingProfile;
import org.mule.config.builders.DefaultsConfigurationBuilder;
import org.mule.module.core.builder.ThreadingProfileBuilder;


public class DSLConfigurationBuilder extends DefaultsConfigurationBuilder
{

    private ThreadingProfileBuilder threadingProfile;
    private ThreadingProfileBuilder dispatcherThreadingProfile;
    private ThreadingProfileBuilder receiverThreadingProfile;
    private ThreadingProfileBuilder requesterThreadingProfile;

    public DSLConfigurationBuilder(ThreadingProfileBuilder threadingProfile, ThreadingProfileBuilder dispatcherThreadingProfile, ThreadingProfileBuilder receiverThreadingProfile, ThreadingProfileBuilder requesterThreadingProfile)
    {
        this.threadingProfile = threadingProfile;
        this.dispatcherThreadingProfile = dispatcherThreadingProfile;
        this.receiverThreadingProfile = receiverThreadingProfile;
        this.requesterThreadingProfile = requesterThreadingProfile;
    }

    @Override
    protected void configureThreadingProfiles(MuleRegistry registry) throws RegistrationException
    {
        final ThreadingProfile defaultThreadingProfile;
        if (threadingProfile != null)
        {
            defaultThreadingProfile = threadingProfile.create();
        }
        else
        {
            defaultThreadingProfile = new ChainedThreadingProfile();
        }

        registry.registerObject(MuleProperties.OBJECT_DEFAULT_THREADING_PROFILE, defaultThreadingProfile);

        if (receiverThreadingProfile != null)
        {
            registry.registerObject(MuleProperties.OBJECT_DEFAULT_MESSAGE_RECEIVER_THREADING_PROFILE, receiverThreadingProfile.create());
        }
        else
        {
            registry.registerObject(MuleProperties.OBJECT_DEFAULT_MESSAGE_RECEIVER_THREADING_PROFILE, new ChainedThreadingProfile(defaultThreadingProfile));
        }

        if (requesterThreadingProfile != null)
        {
            registry.registerObject(MuleProperties.OBJECT_DEFAULT_MESSAGE_REQUESTER_THREADING_PROFILE, requesterThreadingProfile.create());
        }
        else
        {
            registry.registerObject(MuleProperties.OBJECT_DEFAULT_MESSAGE_REQUESTER_THREADING_PROFILE, new ChainedThreadingProfile(defaultThreadingProfile));
        }

        if (dispatcherThreadingProfile != null)
        {
            registry.registerObject(MuleProperties.OBJECT_DEFAULT_MESSAGE_DISPATCHER_THREADING_PROFILE, dispatcherThreadingProfile.create());
        }
        else
        {
            registry.registerObject(MuleProperties.OBJECT_DEFAULT_MESSAGE_DISPATCHER_THREADING_PROFILE, new ChainedThreadingProfile(defaultThreadingProfile));
        }

        //Service is not used on DSL!!
        registry.registerObject(MuleProperties.OBJECT_DEFAULT_SERVICE_THREADING_PROFILE, new ChainedThreadingProfile(defaultThreadingProfile));
    }


}
