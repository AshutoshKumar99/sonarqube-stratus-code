package com.pb.stratus.controller.marker;

import com.pb.stratus.core.configuration.ConfigReader;
import uk.co.graphdata.utilities.contract.Contract;

import java.io.FileNotFoundException;
import java.io.InputStream;

//FIXME should be called MarkerIconRepository or similar
/**
 * Repository for marker images 
 */
public class MarkerRepository
{
    
    public static final String MARKER_FOLDER = "/theme/images/catalog/marker/";

    public static final String MARKER_ICON_FOLDER 
            = "/theme/images/catalog/markericon/";

    public static final String MARKER_SHADOW_FOLDER 
            = "/theme/images/catalog/markershadow/";

    private ConfigReader configReader;
    
    
    public MarkerRepository(ConfigReader configReader)
    {
        this.configReader = configReader;
    }

    public InputStream getMarkerImage(String name, MarkerType type) 
            throws FileNotFoundException
    {
        Contract.pre(name != null, "Marker name required");
        Contract.pre(type != null, "Marker type required");
        switch(type)
        {
            case MARKER:
                return configReader.getConfigFile(MARKER_FOLDER + name);
            case ICON:
                return configReader.getConfigFile(MARKER_ICON_FOLDER + name);
            case SHADOW:
                return configReader.getConfigFile(MARKER_SHADOW_FOLDER + name);
        }
        throw new Error("Unreachable");
    }

}
