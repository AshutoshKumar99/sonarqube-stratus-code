/**
 * 
 */
package com.pb.stratus.controller.print.render;

import com.pb.stratus.controller.annotation.Annotation;
import com.pb.stratus.controller.print.BoundingBox;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * This will return a rendered Image of Map with List of Geometries on it
 * 
 * @author SI001JY
 * 
 */
public interface AnnotationRenderer {
	public BufferedImage renderAnnotations(
			Dimension imageSize, BoundingBox boundingBox, List<Annotation> annotations, String displayUnit);
}
