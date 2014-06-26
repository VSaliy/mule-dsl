package org.mule.module.builder;

import static org.mule.module.Core.flow;
import static org.mule.module.Core.process;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.processor.MessageProcessor;
import org.mule.module.Core;
import org.mule.module.core.Mule;
import org.mule.module.core.builder.InlineMessageProcessorBuilder;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by machaval on 2/20/14.
 */
public class CoreTest
{

    public static final String OK_PAYLOAD = "Ok";
    public static final String BAD_PAYLOAD = "Bad";

    @Test
    public void whenCallingAFlowItShouldBeExecuted() throws MuleException
    {

        Mule mule = new Mule();
        mule.declare(
                flow("Test").then(
                        setPayload(OK_PAYLOAD)
                )
        );
        mule.start();
        final MuleEvent muleEvent = mule.callFlow("Test", "Bad");
        Assert.assertThat(muleEvent.getMessage().getPayload().toString(), CoreMatchers.is(OK_PAYLOAD));
    }


    @Test
    public void whenAnExceptionHappenItShouldBeHandledByTheCorrectExceptionHandler() throws Exception
    {

        Mule mule = new Mule();
        mule.declare(
                flow("exception").
                        then(throwNullPointerException()).
                        onException(Core.catch_(NullPointerException.class)
                                            .then(setPayload(OK_PAYLOAD)))
        );

        mule.start();
        final MuleEvent muleEvent = mule.callFlow("exception", "Bad");
        Assert.assertThat(muleEvent.getMessage().getPayload().toString(), CoreMatchers.is(OK_PAYLOAD));

    }

    @Test
    public void whenHavingMultipleCachItShouldBeRoutedToTheCorrectOne() throws Exception
    {

        Mule mule = new Mule();
        mule.declare(
                flow("exception").
                        then(throwNullPointerException()).
                        onException(Core.catch_(NullPointerException.class)
                                            .then(setPayload(OK_PAYLOAD)),
                                    Core.catch_(IllegalAccessException.class)
                                            .then(setPayload(BAD_PAYLOAD))
                        )
        );

        mule.start();
        final MuleEvent muleEvent = mule.callFlow("exception", BAD_PAYLOAD);
        Assert.assertThat(muleEvent.getMessage().getPayload().toString(), CoreMatchers.is(OK_PAYLOAD));

    }

    private static InlineMessageProcessorBuilder setPayload(final String payload)
    {
        return process(new MessageProcessor()
        {
            @Override
            public MuleEvent process(MuleEvent event) throws MuleException
            {
                event.getMessage().setPayload(payload);
                return event;
            }
        });
    }

    private static InlineMessageProcessorBuilder throwNullPointerException()
    {
        return process(new MessageProcessor()
        {

            @Override
            public MuleEvent process(MuleEvent event) throws MuleException
            {
                throw new NullPointerException();

            }
        });
    }


}
