package org.mule.mushi;

import java.io.File;

import org.apache.maven.artifact.Artifact;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.zip.ZipArchiver;

/**
 * Creates the structure and archive for a Mule Application
 */
public class MushiArchiver extends ZipArchiver
{

    public final static String LIB_LOCATION = "lib" + File.separator;
    public final static String ROOT_LOCATION = "";

    private boolean prependGroupId;

    public MushiArchiver(boolean prependGroupId)
    {
        super();
        this.prependGroupId = prependGroupId;
    }

    public void addResources(final File directoryName) throws ArchiverException
    {
        addDirectory(directoryName, ROOT_LOCATION);
    }

    public void addLib(final File file) throws ArchiverException
    {
        addFile(file, LIB_LOCATION + file.getName());
    }

    public void addLibraryArtifact(final Artifact artifact) throws ArchiverException
    {
        String filename = generateFilenameFromArchive(artifact);
        addFile(artifact.getFile(), filename);
    }

    private String generateFilenameFromArchive(Artifact artifact)
    {
        StringBuilder buf = new StringBuilder(LIB_LOCATION);
        if (prependGroupId)
        {
            buf.append(artifact.getGroupId());
            buf.append(".");
        }
        buf.append(artifact.getFile().getName());
        return buf.toString();
    }


}