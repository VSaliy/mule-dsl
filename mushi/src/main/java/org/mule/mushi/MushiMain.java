package org.mule.mushi;


import org.mule.module.core.launcher.MuleApplicationLauncher;
import org.mule.util.FileUtils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;

public class MushiMain
{

    public static final String HELP_OPTION = "help";
    public static final String DIRECTORY_OPTION = "d";
    public static final String ENVIRONMENT = "env";

    public static void main(String[] args) throws ParseException, IOException
    {

        final Options options = createOptions();
        // create the parser
        final CommandLineParser parser = new BasicParser();


        // parse the command line arguments
        final CommandLine line = parser.parse(options, args);

        String environment = null;
        if (line.hasOption(ENVIRONMENT))
        {
            environment = line.getOptionValue(ENVIRONMENT);
            System.out.println("Loading environment " + environment);
        }

        if (line.hasOption(HELP_OPTION))
        {
            printHelp(options);
        }
        else if (line.hasOption(DIRECTORY_OPTION))
        {
            final File appDirectory = new File(line.getOptionValue(DIRECTORY_OPTION));
            final String appName = FilenameUtils.getBaseName(appDirectory.getName());
            new MuleApplicationLauncher().start(new MushiApplication(appName, appDirectory, environment));
        }
        else if (line.getArgs().length > 0)
        {
            String[] argsArray = line.getArgs();
            final File appFile = new File(argsArray[line.getArgs().length - 1]);
            if (!appFile.exists())
            {
                throw new RuntimeException("The specified application does not exists " + appFile.getAbsolutePath());
            }
            final File appDirectory = createTempDirectory(appFile.getName());
            FileUtils.unzip(appFile, appDirectory);
            final String appName = FilenameUtils.getBaseName(appDirectory.getName());
            new MuleApplicationLauncher().start(new MushiApplication(appName, appDirectory, environment));
        }
        else
        {
            printHelp(options);
        }
    }


    public static File createTempDirectory(String name) throws IOException
    {
        final File temp = File.createTempFile(name, Long.toString(System.nanoTime()));

        if (!(temp.delete()))
        {
            throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
        }

        if (!(temp.mkdir()))
        {
            throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
        }

        return temp;
    }

    private static void printHelp(Options options)
    {
        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("mushi.sh <Option> <App>", options);
    }

    private static Options createOptions()
    {

        final Option help = OptionBuilder.withDescription("Print this message.").create(HELP_OPTION);
        final Option environment = OptionBuilder.withDescription("The environment to be executed.").hasArg().create(ENVIRONMENT);
        final Option directory = OptionBuilder.withDescription("The directory where the unzipped app is placed.").hasArg().create(DIRECTORY_OPTION);
        final Options options = new Options();
        options.addOption(help);
        options.addOption(directory);
        options.addOption(environment);
        return options;
    }
}
