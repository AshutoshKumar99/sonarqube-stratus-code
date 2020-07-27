/**
 * 
 */
package com.pb.stratus.controller.print;

import com.mapinfo.midev.service.geometries.v1.Geometry;

/**
 * The POJO that will contain Geometry as well as Styling information for that
 * Geometry
 * 
 * @author SI001JY
 * 
 */
public class GeometryStyleDO {
	private Geometry geometry;
	// incase we use some predefined NamedStyle we will just need its name and
	// nothing else. Keeping options open now
	private String namedStyle;
	private String color;
	private String strokeColor;
	private float opacity;

	public Geometry getGeometry() {
		return geometry;
	}

	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	public String getNamedStyle() {
		return namedStyle;
	}

	public void setNamedStyle(String namedStyle) {
		this.namedStyle = namedStyle;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getStrokeColor() {
		return strokeColor;
	}

	public void setStrokeColor(String strokeColor) {
		this.strokeColor = strokeColor;
	}

	public float getOpacity() {
		return opacity;
	}

	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}
}
