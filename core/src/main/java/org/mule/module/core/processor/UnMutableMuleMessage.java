package org.mule.module.core.processor;


public interface UnMutableMuleMessage
{

    public Object getPayload();

    public Object getFlowVar(String name);

    public Object getSessionVar(String name);

    public Object getInboundProperty(String name);

    public Object getOutboundProperty(String name);


}
