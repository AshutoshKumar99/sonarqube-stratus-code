package com.pb.stratus.controller.print.template.component;

import com.pb.stratus.controller.print.template.XslFoUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;

import java.awt.image.BufferedImage;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ImageComponentTest 
{

    private ImageComponent imageComponent;
    
    private ContentHandler mockHandler;
    
    private BufferedImage expectedImage;

    @Before
    public void setUp() throws Exception
    {
        expectedImage = new BufferedImage(123, 456, 
                BufferedImage.TYPE_3BYTE_BGR);
        mockHandler = mock(ContentHandler.class);
        imageComponent = new ImageComponent(expectedImage, "3cm", "5cm");
    }

    @Test
    public void shouldEncodeImageDataCorrectly() throws Exception
    {
        Attributes attrs = generateSAXEventsAndCaptureAttributes();
        String expectedEncodedImage = XslFoUtils.encodeImageInBase64(
                expectedImage);
        String actualEncodedImage = new String(attrs.getValue("src"));
        assertEquals(expectedEncodedImage, actualEncodedImage);
    }
    
    @Test
    public void shouldUseWidthAndHeightPassedToConstructor() 
            throws Exception
    {
        Attributes attrs = generateSAXEventsAndCaptureAttributes();
        assertEquals("3cm", attrs.getValue("content-width"));
        assertEquals("5cm", attrs.getValue("content-height"));
    }
    
    private Attributes generateSAXEventsAndCaptureAttributes() throws Exception
    {
        imageComponent.generateSAXEvents(mockHandler);
        ArgumentCaptor<Attributes> arg = ArgumentCaptor.forClass(
                Attributes.class);
        verify(mockHandler).startElement((String) any(), 
                eq("external-graphic"), (String) any(), arg.capture());
        return arg.getValue();
    }
}