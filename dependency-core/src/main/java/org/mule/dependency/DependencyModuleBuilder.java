package org.mule.dependency;


public class DependencyModuleBuilder
{

    private String name;
    private String version;
    private String url;


    public DependencyModuleBuilder(String name)
    {
        this.name = name;
    }

    public DependencyModuleBuilder version(String version)
    {
        this.version = version;
        return this;
    }

    public DependencyModuleBuilder from(String url)
    {
        this.url = url;
        return this;
    }


    public DependencyModule create()
    {
        return new DependencyModule(name, version, url);
    }
}
