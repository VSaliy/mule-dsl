package org.mule.dependency;

import org.mule.module.core.Mule;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Created by machaval on 4/24/14.
 */
public class DummyDependencyManagerImpl implements DependencyManager
{

    @Override
    public File installModule(Module module, Mule mule)
    {
        return null;
    }

    @Override
    public List<String> listVersions(String module)
    {
        return Collections.emptyList();
    }
}
