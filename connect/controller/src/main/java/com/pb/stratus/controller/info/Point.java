//FIXME package name
package com.pb.stratus.controller.info;

//FIXME comments
/**
 * A simple Point object with x,y values.
 * @author KA001YE
 *
 */
public class Point extends Geometry
{
  private float x;
  private float y;
  
  /**
   * Constructor for the Point object.
   * @param x
   * @param y
   */
  public Point(float x, float y)
  {
      this.x = x;
      this.y = y;
  }
}
