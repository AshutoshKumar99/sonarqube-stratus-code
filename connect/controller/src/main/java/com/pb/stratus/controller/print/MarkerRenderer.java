package com.pb.stratus.controller.print;

import com.pb.stratus.controller.marker.MarkerComparator;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Renders an image of a given size with markers identifying a number of 
 * locations. The generated image is intended to be overlayed over a map image
 * to provide context to the marker locations. The pixel locations of the 
 * markers are calculated by relating the marker locations to the given 
 * bounding box and scaling the relative locations to the image dimensions.
 * Those calculations are based on euclidean geometry, which is a reasonable 
 * assumption as long as the generated image is overlayed over a map that has
 * been projected into a plane with the same SRS that the bounding box and 
 * marker coordinates are expressed in.
 * In addition to the icons provided by the Marker class, every marker is 
 * augmented with a number starting from 1 up to the number of markers passed
 * into {@link #renderMarkers(Dimension, BoundingBox, List)}. This number
 * is necessary to relate the marker to more detailed information that might
 * be provided separately from the actual map image. 
 */
public class MarkerRenderer
{
    
    public BufferedImage renderMarkers(Dimension imageSize, 
            BoundingBox boundingBox, List<Marker> markers) 
            throws RenderException
    {
        BufferedImage canvas = new BufferedImage(imageSize.width, 
                imageSize.height, BufferedImage.TYPE_4BYTE_ABGR);
        markers = reorderMarkers(markers);
        Graphics2D g = canvas.createGraphics();
        try
        {
            for (Marker marker : markers)
            {
                renderMarker(marker, g, boundingBox, imageSize);
            }
        }
        finally
        {
            g.dispose();
        }
        return canvas;
    }

    private List<Marker> reorderMarkers(List<Marker> markers)
    {
        List<Marker> reordered = new LinkedList<Marker>(markers);
        Collections.sort(reordered, new MarkerComparator());
        return reordered;
    }

    private void renderMarker(Marker marker, Graphics2D g,
            BoundingBox boundingBox, Dimension imageSize)
    {
        Point offset = calculateMarkerOffset(marker, boundingBox, imageSize);
        g.drawImage(marker.getIcon(), null, offset.x, offset.y);
    }

    private Point calculateMarkerOffset(Marker marker, BoundingBox boundingBox,
            Dimension imageSize)
    {
        Point2D location = marker.getLocation();
        Point bl = calculateOffsetFromBottomLeft(boundingBox, location,
                imageSize);
        int x = bl.x;
        int y = imageSize.height - bl.y - 1;
        Point anchorPoint = marker.getAnchorPoint();
        x -= anchorPoint.x;
        y -= anchorPoint.y;
        return new Point(x,y);
    }
    
    private Point calculateOffsetFromBottomLeft(BoundingBox boundingBox, 
            Point2D location, Dimension imageSize)
    {
        double offsetX = (location.getX() - boundingBox.getWest()) 
                / (boundingBox.getEast() - boundingBox.getWest()) 
                * imageSize.width;
        double offsetY = (location.getY() - boundingBox.getSouth()) 
                / (boundingBox.getNorth() - boundingBox.getSouth()) 
                * imageSize.height;
        return new Point((int) offsetX, (int) offsetY);
    }


}
