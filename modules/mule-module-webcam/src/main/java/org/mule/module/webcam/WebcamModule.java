package org.mule.module.webcam;

import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.Module;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.Source;
import org.mule.api.annotations.lifecycle.Start;
import org.mule.api.annotations.lifecycle.Stop;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.video.capture.VideoCapture;
import org.openimaj.video.capture.VideoCaptureException;

/**
 * Created by machaval on 3/16/14.
 */
@Module(name = "Webcam")
public class WebcamModule
{

    private VideoCapture vc;
    @Configurable
    @Default("640")
    private int width;
    @Configurable
    @Default("480")
    private int height;

    @Start
    public void onStart()
    {
        try
        {
            vc = new VideoCapture(width, height);
        }
        catch (VideoCaptureException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Takes a picture using the default camera.;
     *
     * @return The input stream of the picture that was taken.
     * @throws IOException
     */
    @Processor
    public InputStream takePicture() throws IOException
    {
        final MBFImage snapshot = vc.getNextFrame();
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageUtilities.write(snapshot, "PNG", output);
        return new ByteArrayInputStream(output.toByteArray());
    }

    /**
     * Find a face if possible at the given image.
     *
     * @param inputStream The image where to find the face
     * @return The face.
     * @throws IOException
     */
    @Processor
    public InputStream detectFace(@Optional @Default("#[payload]") InputStream inputStream) throws IOException
    {
        final MBFImage fImages = ImageUtilities.readMBF(inputStream);

        //Todo finish implementation
        return null;
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    @Stop
    public void onStop()
    {
        vc.close();
    }

}
