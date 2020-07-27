package com.pb.stratus.controller.service;

public interface MapRequestParams {

	public final static String INVALID_PARAMETER_STRING = "The following parameter was not found in request: ";
	public final static String INVALID_DOUBLE_STRING = "Invalid number for parameter: ";
	public static final String SRS_PARAM_NAME = "srs";
	public static final String X_PARAM_NAME = "x";
	public static final String Y_PARAM_NAME = "y";
	public static final String ZOOM_PARAM_NAME = "zoom";
	public static final String WIDTH_PARAM_NAME = "width";
	public static final String HEIGHT_PARAM_NAME = "height";
	public static final String MAPNAME_PARAM_NAME = "mapName";
	public static final String MAPTYPE_PARAM_NAME = "mapType";
	
	public enum MapType
	{
		MIDEV, BING
	}
	
	public String getMapName();
	
	public MapType getMapType();
	
	public void setMapType(MapType mapType);
 
    public void setMapName(String mapName);
   
    public String getSrs();
    
    public void setSrs(String srs);

    public double getXPos();

    public void setXPos(double pos);

    public void setXPos(String pos);
   
    public double getYPos();
   
    public void setYPos(double pos);

    public void setYPos(String pos);
    
    public double getWidth();
  
    public void setWidth(double width);
   
    public void setWidth(String width);
   
    public double getHeight();

    public void setHeight(double height);
 
    public void setHeight(String height);
   
    public double getZoom();
   
    public void setZoom(double zoom);
  
    public void setZoom(String zoom);

    public String getImageMimeType();

    public void setImageMimeType(String imageMimeType);
   
    public boolean isReturnImage();

    public void setReturnImage(boolean returnImage);


}
