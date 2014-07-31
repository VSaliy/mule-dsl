package org.mule.module.core.builder;

import org.mule.api.MuleContext;
import org.mule.api.config.ThreadingProfile;
import org.mule.config.ChainedThreadingProfile;
import org.mule.config.dsl.Builder;
import org.mule.module.core.TimePeriod;

/**
 * Created by machaval on 6/26/14.
 */
public class ThreadingProfileBuilder
{

    private Integer maxThreadsActive;
    private Integer maxThreadsIdle;
    private Long threadTTL;
    private Long threadWaitTimeout;


    public ThreadingProfileBuilder maxThreadsActive(int maxThreadsActive)
    {
        this.maxThreadsActive = maxThreadsActive;
        return this;
    }

    public ThreadingProfileBuilder maxThreadsIdle(int maxThreadsIdle)
    {
        this.maxThreadsIdle = maxThreadsIdle;
        return this;
    }

    public ThreadingProfileBuilder threadTTL(long threadTTL)
    {
        this.threadTTL = threadTTL;
        return this;
    }

    public ThreadingProfileBuilder threadWaitTimeout(int duration, TimePeriod period)
    {
        this.threadWaitTimeout = period.toMillis(duration);
        return this;
    }


    //TODO later
    //public ThreadingProfileBuilder rejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler)
    //{
    //    return this;
    //}
    //
    //public ThreadingProfileBuilder threadFactory(ThreadFactory threadFactory)
    //{
    //    return this;
    //}


    public ThreadingProfile create()
    {
        final ChainedThreadingProfile chainedThreadingProfile = new ChainedThreadingProfile();
        if (maxThreadsActive != null)
        {
            chainedThreadingProfile.setMaxThreadsActive(maxThreadsActive);
        }
        if (maxThreadsIdle != null)
        {
            chainedThreadingProfile.setMaxThreadsIdle(maxThreadsIdle);
        }

        if (threadTTL != null)
        {
            chainedThreadingProfile.setThreadTTL(threadTTL);
        }
        if (threadWaitTimeout != null)
        {
            chainedThreadingProfile.setThreadWaitTimeout(threadWaitTimeout);
        }
        return chainedThreadingProfile;
    }
}
