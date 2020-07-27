package com.pb.stratus.controller.print.image;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class ImageReaderTest
{
    @Test
    public void shouldReadImageCorrectly() throws Exception
    {
        File f = copyTestImageToTempFile();
        URL u = f.toURL();
        BufferedImage actual = new ImageReader().readFromUrl(u);
        assertImagesAreEqual(getTestImage(), actual);
    }
    
    private void assertImagesAreEqual(BufferedImage expected, BufferedImage actual)
    {
        assertEquals(expected.getHeight(), actual.getHeight());
        assertEquals(expected.getWidth(), actual.getWidth());
        assertEquals(expected.getType(), actual.getType());
    }
    
    //XXX I don't love the fact that we have to go to the file system, but URLs 
    //    are always a pain when it comes to unit testing
    private File copyTestImageToTempFile() throws Exception
    {
        File f = File.createTempFile("stratus", ".jpg");
        f.deleteOnExit();
        InputStream is = getTestImageInputStream();
        OutputStream os = new FileOutputStream(f);
        IOUtils.copy(is, os);
        is.close();
        os.close();
        return f;
    }
    
    private BufferedImage getTestImage() throws Exception
    {
        return ImageIO.read(getTestImageInputStream());
    }
    
    private InputStream getTestImageInputStream()
    {
        return ImageReaderTest.class.getResourceAsStream("portrait-image.jpg");        
    }
}
