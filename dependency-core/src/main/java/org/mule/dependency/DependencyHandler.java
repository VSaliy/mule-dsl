package org.mule.dependency;

import java.io.File;
import java.util.List;

/**
 * Created by machaval on 4/21/14.
 */
public interface DependencyHandler
{


    File getModule(DependencyModule dependencyModule, File appHome);

    List<String> listVersions(String module, File appHome);
}
