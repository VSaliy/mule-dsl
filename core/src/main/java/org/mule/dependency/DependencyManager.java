package org.mule.dependency;

import org.mule.module.core.Mule;

import java.io.File;
import java.util.List;

/**
 * Created by machaval on 4/21/14.
 */
public interface DependencyManager
{


    File installModule(Module module, Mule mule);

    List<String> listVersions(String module);
}
