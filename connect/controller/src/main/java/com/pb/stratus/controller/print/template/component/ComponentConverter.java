package com.pb.stratus.controller.print.template.component;

import com.pb.stratus.controller.print.Resolution;
import com.pb.stratus.controller.print.content.FmnResult;
import com.pb.stratus.controller.print.template.Component;

import java.awt.image.BufferedImage;

/**
 * Converts arbitrary Java objects into template components
 */
public class ComponentConverter
{
    
    private Resolution resolution = new Resolution(150);
    
    public Component convertToComponent(Object obj)
    {
        if (obj == null)
        {
            return NullComponent.INSTANCE;
        }
        else if (obj instanceof BufferedImage)
        {
            BufferedImage image = (BufferedImage) obj;
            double width = resolution.calculatePixelWidthInCm(
                    image.getWidth());
            double height = resolution.calculatePixelWidthInCm(
                    image.getHeight());
            return new ImageComponent((BufferedImage) obj, width + "cm", 
                    height + "cm");
        }
        else if (obj instanceof FmnResult)
        {
            return new FmnResultComponent((FmnResult) obj);
        }
        else
        {
            return new TextComponent(obj.toString());
        }
    }

}
