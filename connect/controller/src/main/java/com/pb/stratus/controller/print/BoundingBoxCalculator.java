package com.pb.stratus.controller.print;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * A helper class to calculate the best bounding box for a given image size 
 * and an initial bounding box that should be contained in the resulting 
 * bounding box.  
 */
public class BoundingBoxCalculator 
{
    
    /**
     * Returns a bounding box that fulfils the following criteria:
     * <ul>
     *     <li>It has the same centre point as the initial bounding box</li>
     *     <li>It contains the entire initial bounding box</li>
     *     <li>It has the same aspect ratio as the given image size</li>
     *     <li>
     *         It is the smallest bounding box that fulfils the above criteria
     *     </li>
     * </ul>
     * 
     * @param initialBoundingBox an arbitrary bounding box
     * @param imageSize the size of the image the returned bounding box should
     *        subsequently be rendered into.
     * @return a new BoundingBox that meets the above criteria
     */
    public BoundingBox calculate(BoundingBox initialBoundingBox, 
            Dimension imageSize)
    {
        double resolution = calculateBestResolution(initialBoundingBox, 
                imageSize);
        return createBoundingBox(initialBoundingBox, imageSize, resolution);
    }

    private double calculateBestResolution(BoundingBox initialBoundingBox,
            Dimension imageSize)
    {
        if (boundingBoxAspectRatioIsLessThanImageAspectRatio(
                initialBoundingBox, imageSize))
        {           
            return initialBoundingBox.getHeight() / imageSize.getHeight();
        }
        else
        {
            return initialBoundingBox.getWidth() / imageSize.getWidth();
        }
    }
    
    private boolean boundingBoxAspectRatioIsLessThanImageAspectRatio(
            BoundingBox initialBoundingBox, Dimension imageSize)
    {
        double boundingBoxAspectRatio = initialBoundingBox.getWidth() 
                / initialBoundingBox.getHeight();
        double imageAspectRatio = imageSize.getWidth() / imageSize.getHeight(); 
        return boundingBoxAspectRatio < imageAspectRatio;
    }

    private BoundingBox createBoundingBox(BoundingBox initialBoundingBox,
            Dimension imageSize, double resolution)
    {
        double width = imageSize.getWidth() * resolution;
        double height = imageSize.getHeight() * resolution;
        Point2D center = initialBoundingBox.getCenter();
        double north = center.getY() + height / 2; 
        double south = center.getY() - height / 2; 
        double west = center.getX() - width / 2;
        double east = center.getX() + width / 2;
        return new BoundingBox(north, south, west, east, 
                initialBoundingBox.getSrs());
    }

}
