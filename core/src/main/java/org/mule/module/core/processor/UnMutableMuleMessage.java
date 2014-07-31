package org.mule.module.core.processor;


public interface UnMutableMuleMessage
{

    Object getPayload();

    <T> T getPayloadAs(Class<T> type);

    Object getFlowVar(String name);

    Object getSessionVar(String name);

    Object getInboundProperty(String name);

    Object getOutboundProperty(String name);

}
