package com.pb.stratus.controller.print.content;

import com.pb.stratus.controller.print.Marker;

import java.awt.image.BufferedImage;

/**
 * Encapsulates the information of a single FMN result
 */
public class FmnResult
{
    
    private String title;
    
    private String description;
    
    private String keyValue;
    
    private String link;
    
    private BufferedImage image;
    
    private Marker marker;
    
    public FmnResult(String title, String description, String keyValue, 
            String link, BufferedImage image, Marker marker)
    {
        this.title = title;
        this.description = description;
        this.keyValue = keyValue;
        this.link = link;
        this.image = image;
        this.marker = marker;
    }

    public String getTitle()
    {
        return title;
    }

    public String getDescription()
    {
        return description;
    }

    public String getKeyValue()
    {
        return keyValue;
    }

    public String getLink()
    {
        return link;
    }

    public BufferedImage getImage()
    {
        return image;
    }
    
    public Marker getMarker()
    {
        return marker;
    }

}
