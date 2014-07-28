package org.mule.module.core.processor;

public interface Expression<T>
{

    T eval(UnMutableMuleMessage muleMessage);

}
