package com.pb.stratus.controller.service;

import com.pb.stratus.controller.IllegalRequestException;
import org.apache.commons.lang.StringUtils;

public class BaseMapRequestParams implements MapRequestParams
{

    // Properties
    private String mapName;   
    private MapType mapType;
    private String srs;
    private double xPos;
    private double yPos;
    private double width;
    private double height;
    private double zoom;
    private String imageMimeType;
    private boolean returnImage;
    

    public String getMapName()
    {
        return mapName;
    }
    
    public void setMapName(String mapName)
    {
        if (StringUtils.isEmpty(mapName))
        {
            throw new IllegalRequestException(INVALID_PARAMETER_STRING
                    + MAPNAME_PARAM_NAME);
        }
        this.mapName = mapName;
    }

    
    public MapType getMapType()
    {
    	return mapType;
    }
    
    public void setMapType(MapType mapType)
    {
    	this.mapType = mapType;
    }

    
    public String getSrs()
    {
        return srs;
    }

    public void setSrs(String srs)
    {
        if (StringUtils.isEmpty(srs))
        {
            throw new IllegalRequestException(INVALID_PARAMETER_STRING
                    + SRS_PARAM_NAME);
        }
        this.srs = srs;
    }

    public double getXPos()
    {
        return xPos;
    }

    public void setXPos(double pos)
    {
        xPos = pos;
    }

    public void setXPos(String pos)
    {
        if (StringUtils.isEmpty(pos))
        {
            throw new IllegalRequestException(INVALID_PARAMETER_STRING
                    + X_PARAM_NAME);
        }
        try
        {
            xPos = Double.parseDouble(pos);
        } catch (NumberFormatException exception)
        {
            throw new IllegalRequestException(INVALID_DOUBLE_STRING
                    + X_PARAM_NAME);
        }
    }

    public double getYPos()
    {
        return yPos;
    }

    public void setYPos(double pos)
    {
        yPos = pos;
    }

    public void setYPos(String pos)
    {
        if (StringUtils.isEmpty(pos))
        {
            throw new IllegalRequestException(INVALID_PARAMETER_STRING
                    + Y_PARAM_NAME);
        }
        try
        {
            yPos = Double.parseDouble(pos);
        } catch (NumberFormatException exception)
        {
            throw new IllegalRequestException(INVALID_DOUBLE_STRING
                    + Y_PARAM_NAME);
        }
    }

    public double getWidth()
    {
        return width;
    }

    public void setWidth(double width)
    {
        this.width = width;
    }

    public void setWidth(String width)
    {
        if (StringUtils.isEmpty(width))
        {
            throw new IllegalRequestException(INVALID_PARAMETER_STRING
                    + WIDTH_PARAM_NAME);
        }
        try
        {
            this.width = Double.parseDouble(width);
        } catch (NumberFormatException exception)
        {
            throw new IllegalRequestException(INVALID_DOUBLE_STRING
                    + WIDTH_PARAM_NAME);
        }
    }

    public double getHeight()
    {
        return height;
    }

    public void setHeight(double height)
    {
        this.height = height;
    }

    public void setHeight(String height)
    {
        if (StringUtils.isEmpty(height))
        {
            throw new IllegalRequestException(INVALID_PARAMETER_STRING
                    + HEIGHT_PARAM_NAME);
        }
        try
        {
            this.height = Double.parseDouble(height);
        } catch (NumberFormatException exception)
        {
            throw new IllegalRequestException(INVALID_DOUBLE_STRING
                    + HEIGHT_PARAM_NAME);
        }
    }

    public double getZoom()
    {
        return zoom;
    }

    public void setZoom(double zoom)
    {
        this.zoom = zoom;
    }

    public void setZoom(String zoom)
    {
        if (StringUtils.isEmpty(zoom))
        {
            throw new IllegalRequestException(INVALID_PARAMETER_STRING
                    + HEIGHT_PARAM_NAME);
        }
        try
        {
            this.zoom = Double.parseDouble(zoom);
        } catch (NumberFormatException exception)
        {
            throw new IllegalRequestException(INVALID_DOUBLE_STRING
                    + ZOOM_PARAM_NAME);
        }
    }

    public String getImageMimeType()
    {
        return imageMimeType;
    }

    public void setImageMimeType(String imageMimeType)
    {
        if (StringUtils.isEmpty(imageMimeType))
        {
            imageMimeType = "image/png";
        }
        this.imageMimeType = imageMimeType;
    }

    public boolean isReturnImage()
    {
        return returnImage;
    }

    public void setReturnImage(boolean returnImage)
    {
        this.returnImage = returnImage;
    }

}
