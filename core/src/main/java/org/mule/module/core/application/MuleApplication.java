package org.mule.module.core.application;

import java.io.File;
import java.util.List;


public interface MuleApplication
{

    void initialize();

    List<MuleModule> getModules();

    String getName();

    File getAppHome();

    ClassLoader getApplicationClassLoader();

}
