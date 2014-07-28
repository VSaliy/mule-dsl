package org.mule.module.core.builder;

import org.mule.api.MuleContext;
import org.mule.api.processor.MessageProcessor;
import org.mule.config.dsl.Builder;
import org.mule.enricher.MessageEnricher;

import java.util.ArrayList;
import java.util.List;


public class EnricherBuilder implements MessageProcessorBuilder<MessageEnricher>
{

    private List<MessageEnricher.EnrichExpressionPair> enrichmentExpressions = new ArrayList<MessageEnricher.EnrichExpressionPair>();
    private ChainBuilder chainBuilder = new ChainBuilder();

    public EnricherBuilder(String target)
    {
        this.enrichmentExpressions.add(new MessageEnricher.EnrichExpressionPair(target));
    }

    public EnricherBuilder(String target, String source)
    {
        this.enrichmentExpressions.add(new MessageEnricher.EnrichExpressionPair(target, source));
    }

    public EnricherBuilder with(Builder<? extends MessageProcessor>... mps)
    {
        chainBuilder.chain(mps);
        return this;
    }


    @Override
    public MessageEnricher create(MuleContext muleContext)
    {
        final MessageEnricher result = new MessageEnricher();
        for (MessageEnricher.EnrichExpressionPair enrichmentExpression : enrichmentExpressions)
        {
            result.addEnrichExpressionPair(enrichmentExpression);
        }
        result.setEnrichmentMessageProcessor(chainBuilder.create(muleContext));
        return result;
    }
}
