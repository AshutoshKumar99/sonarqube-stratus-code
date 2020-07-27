package com.pb.stratus.controller.print.config;

import com.pb.stratus.controller.wmsprofile.WMSProfile;
import com.pb.stratus.controller.wmsprofile.WMSProfileParser;
import com.pb.stratus.core.configuration.ConfigReader;

import javax.xml.stream.XMLStreamException;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Manages access to map configurations
 */
public class MapConfigRepositoryImpl implements MapConfigRepository {
    
    private ConfigReader configReader;
    private MapConfigParser mapConfigParser;
    private WMSProfileParser wmsProfileParser;

    public MapConfigRepositoryImpl(ConfigReader configReader, MapConfigParser mapConfigParser, WMSProfileParser wmsProfileParser)
    {
        this.configReader = configReader;
        this.mapConfigParser = mapConfigParser;
        this.wmsProfileParser = wmsProfileParser;
    }
    
    public MapConfig getMapConfig(String mapConfigName)
    {
        String path = String.format("/project/%s.xml", mapConfigName);
        try
        {
            InputStream is = configReader.getConfigFile(path);
            return mapConfigParser.parseMapProject(is, this.configReader);
            
        }
        catch (FileNotFoundException fnfx)
        {
            throw new IllegalArgumentException("A map configuration " 
                    + "with the name '" + mapConfigName 
                    + "' doesn't exist", fnfx);
        }
    }



    public WMSProfile getWMSProfile(String wmsProfileName) {
        String path = String.format("/webmappingservice/%s.xml", wmsProfileName);
        try {
            InputStream is = configReader.getConfigFile(path);
            return wmsProfileParser.getWMSProfile(is);
        } catch (FileNotFoundException fnfx) {
            throw new IllegalArgumentException("A WMS profile "
                    + "with the name '" + wmsProfileName
                    + "' doesn't exist", fnfx);
        } catch (XMLStreamException e) {
            throw new IllegalArgumentException("A WMS profile "
                    + "with the name '" + wmsProfileName
                    + "' doesn't exist", e);
        }
    }
}
