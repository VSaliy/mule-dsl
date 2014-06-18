package org.mule.module.core.application;

import java.io.File;
import java.util.List;


public interface MuleApplication
{

    List<MuleModule> getModules();

    String getName();

    File getAppHome();

}
