package com.pb.stratus.controller.print;

import com.pb.stratus.core.configuration.ConfigReader;
import com.pb.stratus.core.configuration.ControllerConfiguration;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Create a locator marker by adding the coordinates of the locator marker and
 * add the marker icon as a bufferedimage
 */
public class LocatorMarkerParser
{
    public static final String ADDRESS_X_PARAM = "addressx";

    public static final String ADDRESS_Y_PARAM = "addressy";

    private ConfigReader configReader;
    
    private ControllerConfiguration config;
    

    public LocatorMarkerParser(ConfigReader configReader, 
            ControllerConfiguration config)
    {
        this.configReader = configReader;
        this.config = config;
    }

    /**
     * This method will create a Marker for either Locator or Callout. Earlier both images were read from Controller.properties but after fix of CONN-17277
     * We will be reading only the CalloutInfo from the properties whereas the Locator Image path will be provided during method invocation
     * @param addressx
     * @param addressy
     * @param isLocator
     * @param locatorImagePath
     * @return
     */
    public Marker createMarker(String addressx, String addressy, boolean isLocator, String locatorImagePath)
    {
        if (addressx == null ||
                addressy == null)
        {
            return null;
        }
        BufferedImage icon;
        try
        {
            InputStream is = null;
            if(isLocator)
            {
                if(locatorImagePath!=null)
                {
                    is = configReader.getConfigFile(locatorImagePath);
                }   else
                {
                is = configReader.getConfigFile(
                        config.getLocatorImageForPrint());
            }
            }
            else
            {
                is = configReader.getConfigFile(
                        config.getCallOutInfoImageForPrint());
            }
            icon = ImageIO.read(is);
        }
        catch (IOException iox)
        {
            throw new RenderException(iox);
        }
        //SIKHAR: CONN-17277 earlier, a different icon was used to print locator image on map. That icon was center aligned and hence the commented code below worked there
        //Now the new icon is left aligned with the arrow head is positioned exactly 22 pixels from left. Hence the anchor point is calculated accordingly
        Point anchorPoint = new Point(22,
                icon.getHeight() - 1);
//        Point anchorPoint = new Point((int) (icon.getWidth() / 2d),
//                icon.getHeight() - 1);

        Point2D.Double location = new Point2D.Double();
        location.setLocation(
                Double.parseDouble(addressx),
                Double.parseDouble(addressy));
        return new Marker(icon, anchorPoint, location);
    }

    
}