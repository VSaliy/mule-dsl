package org.mule.gulash;

import org.mule.module.core.Mule;

import java.io.File;
import java.io.IOException;

import groovy.lang.Binding;
import org.codehaus.groovy.tools.shell.IO;

import org.mule.gulash.Gulash;


public class InteractiveGroovyRunner extends AbstractGroovyRunner
{


    public void run(Mule mule) throws IOException
    {
        final Binding binding = new Binding();
        binding.setVariable("mule", mule);
        final Gulash interactiveShell = new Gulash(createGroovyShell(mule), new IO());
        interactiveShell.run(new String[0]);
    }


}
