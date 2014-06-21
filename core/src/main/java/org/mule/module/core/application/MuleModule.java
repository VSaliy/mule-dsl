package org.mule.module.core.application;

import org.mule.module.core.Mule;


public interface MuleModule
{


    void initialize();

    void build(Mule mule, MuleEnvironment environment);

}
