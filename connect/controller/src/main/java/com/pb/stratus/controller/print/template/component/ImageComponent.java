package com.pb.stratus.controller.print.template.component;

import com.pb.stratus.controller.print.RenderException;
import com.pb.stratus.controller.print.template.Component;
import com.pb.stratus.controller.print.template.XslFoUtils;
import com.pb.stratus.core.util.ObjectUtils;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import uk.co.graphdata.utilities.contract.Contract;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.pb.stratus.controller.print.template.XslFoUtils.*;

/**
 * Basic image component class that can be used to output simple images,
 * or extended for graphics-specific elements like logos or maps
 */
public class ImageComponent implements Component
{

    private BufferedImage image;
    
    private String width;
    
    private String height;
    
    public ImageComponent(BufferedImage image, String width, String height)
    {
        Contract.pre(image != null, "Image required");
        this.image = image;
        this.width = width;
        this.height = height;
    }

    /**
     * Very simple image graphics output with no special style or
     * markup.
     *
     * @param handler
     * @throws SAXException
     */
    public void generateSAXEvents(ContentHandler handler) throws SAXException
    {
        this.addImageMarkup(handler);
    }

    /**
     * Method that adds the image graphics as markup, which can be used
     * by sub-classes if they override their generateSAXEvents.
     *
     * @param handler
     * @throws SAXException
     */
    private void addImageMarkup(ContentHandler handler) throws SAXException
    {
        String encodedImage;
        try
        {
            encodedImage = encodeImageInBase64(image);
        }
        catch (IOException iox)
        {
            throw new RenderException(iox);
        }
        Attributes attrs = XslFoUtils.createAttribute(
                "src", encodedImage, 
                "content-width", width,
                "content-height", height);
        startElement(handler, EXTERNAL_GRAPHIC, attrs);
        endElement(handler, EXTERNAL_GRAPHIC);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof ImageComponent))
        {
            return false;
        }
        ImageComponent that = (ImageComponent) obj;
        if (!ObjectUtils.equals(this.image, that.image))
        {
            return false;
        }
        if (!ObjectUtils.equals(this.width, that.width))
        {
            return false;
        }
        if (!ObjectUtils.equals(this.height, that.height))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hc = ObjectUtils.SEED;
        hc = ObjectUtils.hash(hc, image);
        hc = ObjectUtils.hash(hc, width);
        hc = ObjectUtils.hash(hc, height);
        return hc;
    }
    
    

}
