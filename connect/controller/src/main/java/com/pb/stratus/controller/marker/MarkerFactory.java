package com.pb.stratus.controller.marker;

import com.pb.stratus.controller.print.Marker;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class MarkerFactory
{
    
    public Marker createMarker(BufferedImage icon, Point anchorPoint, 
            Point2D location)
    {
        return new Marker(icon, anchorPoint, location);
    }
    

}
