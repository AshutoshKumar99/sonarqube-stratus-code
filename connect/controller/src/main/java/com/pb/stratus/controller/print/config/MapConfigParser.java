package com.pb.stratus.controller.print.config;

import com.pb.stratus.controller.print.config.MapConfig.MapConfigDefinition;
import com.pb.stratus.controller.print.config.MapConfig.MapDefinition;
import com.pb.stratus.controller.print.config.MapConfig.ThirdPartyAPIKey;
import com.pb.stratus.controller.print.config.MapConfig.Watermark;
import com.pb.stratus.core.configuration.ConfigFileType;
import com.pb.stratus.core.configuration.ConfigReader;
import com.pb.stratus.core.exception.ParseException;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses and populates the information from a persistent Map configuration
 * (.XML) file.
 * @author sa021sh
 * @Revision: 03/05/2012 - Added Attribute information to the object
 * 
 */
public class MapConfigParser {
    private static final Logger logger = LogManager
            .getLogger(MapConfigParser.class);

    private static final String MAP_CONFIG_TAG = "map-config";
    private static final String MAP_PROJECT_TAG = "map-project";
    private static final String GAZETTEER_NAME_TAG = "gazetteer-name";
    private static final String INTERNATIONALGEOCODER_TARGET_COUNTRY_TAG = "international-geocoder-target-country";
    private static final String MAP_TAG = "map";
    private static final String API_KEYS_TAG = "key";
    private static final String WATERMARK_TAG = "watermark";
    private static final String COPYRIGHT_ATTR = "copyright";
    private static final String NAME_ATTR = "map-name";
    private static final String PATH_ATTR = "repository-path";
    private static final String URL_ATTR = "url";
    private static final String DEFAULT_COPYRIGHT_ATTR = "default-copyright";
    private static final String OPACITY_ATTR = "opacity";
    private static final String SERVICE_ATTR = "service";
    private static final String TILE_HEIGHT_ATTR = "tile-height";
    private static final String TILE_WIDTH_ATTR = "tile-width";
    private static final String FRIENDLY_NAME_ATTR = "friendly-name";
    private static final String IMAGE_MIME_TYPE_ATTR = "image-mime-type";
    
    private static final String MAX_BOUNDS_LEFT_ATTR = "max-bounds-left";
    private static final String MAX_BOUNDS_RIGHT_ATTR = "max-bounds-right";
    private static final String MAX_BOUNDS_TOP_ATTR = "max-bounds-top";
    private static final String MAX_BOUNDS_BOTTOM_ATTR = "max-bounds-bottom";

    private static final String INITIAL_X_ATTR = "initial-x";
    private static final String INITIAL_Y_ATTR = "initial-y";

    private static final String MAX_ZOOM_LEVELS_ATTR = "max-zoom-levels";
    private static final String UNITS_ATTR = "units";
    private static final String INITIAL_ZOOM_ATTR = "initial-zoom";
    private static final String PROJECTION_ATTR = "projection";
    private static final String SEARCH_ZOOM_ATTR = "searchzoom";
    private static final String LEGEND_URL="legend-url";
    private static final String LAYER="layer";

    private static final String GAZETTEER_TAG = "gazetteer";
    private static final String GAZETTEER_TAG_SERVICE_ATTR = "service";
    private static final String GAZETTEER_TAG_NAME_ATTR = "name";

    private static final String SEARCH_RESULT_COUNT = "addressSearchResultsCount";

    public MapConfig parseMapConfig(InputStream is) {
        Document doc = parseXml(is);
        return parseMapConfig(doc);
    }

    public MapConfig parseMapProject(InputStream is, ConfigReader reader) {
        Document doc = parseXml(is);
        return parseMapProject(doc, reader);
    }

    /**
     * @param doc
     * @param reader
     * @return
     */
    private MapConfig parseMapProject(Document doc, ConfigReader reader) {
        MapConfig mapConfig = new MapConfig();
        Element mapProjectElement = (Element) doc.getElementsByTagName(MAP_PROJECT_TAG).item(0);
        parseMapConfigDefinition(mapConfig, mapProjectElement);

        NodeList functionalityProfile = mapProjectElement.getElementsByTagName("functionality-profile");
        String functionalityProfileName = "";
        if(functionalityProfile.getLength() > 0){
            functionalityProfileName = functionalityProfile.item(0).getTextContent();
            parseFunctionalityProfile(mapConfig, functionalityProfileName, reader);
        }
        return mapConfig;
    }

    private MapConfig parseMapConfig(Document doc) {
        MapConfig mapConfig = new MapConfig();
        Element mapConfigElement = (Element) doc.getElementsByTagName(
                MAP_CONFIG_TAG).item(0);

        parseMapConfigDefinition(mapConfig, mapConfigElement);
        // default copyright
        mapConfig.setDefaultCopyright(mapConfigElement
                .getAttribute(DEFAULT_COPYRIGHT_ATTR));

        // default gazetteer
        // old mapcfg's don't have this, so let it be optional (and use the service's default)
        NodeList gazetteerNodeList = mapConfigElement
                .getElementsByTagName(GAZETTEER_NAME_TAG);
        if(gazetteerNodeList.getLength()>0){
            mapConfig.setDefaultGazetteerName(gazetteerNodeList.item(0)
                    .getTextContent());
            mapConfig.setDefaultGazetteerService(gazetteerNodeList.item(0)
                    .getTextContent());
        }else{
            try {
                Element gazetteerElement = (Element) mapConfigElement.getElementsByTagName(GAZETTEER_TAG).item(0);
                //populate proper gazetteer ServiceName
                mapConfig.setDefaultGazetteerName(gazetteerElement.getAttribute(GAZETTEER_TAG_NAME_ATTR));
                mapConfig.setDefaultGazetteerService(gazetteerElement.getAttribute(GAZETTEER_TAG_SERVICE_ATTR));
            }catch(Exception ex){
                logger.error("Problem while retrieving gazetteerElement: "+ex.getMessage());
            }
        }

        NodeList countryNameNodeList = mapConfigElement.getElementsByTagName(INTERNATIONALGEOCODER_TARGET_COUNTRY_TAG);
        if(countryNameNodeList.getLength()>0){
            mapConfig.setInternationalGeocoderTargetCountry(countryNameNodeList.item(0)
                    .getTextContent());
        }
        // API Keys
        NodeList apiNodeList = mapConfigElement
                .getElementsByTagName(API_KEYS_TAG);
        parseAPIKeys(mapConfig, apiNodeList);

        // MapDefinition
        NodeList mapNodeList = mapConfigElement.getElementsByTagName(MAP_TAG);
        parseMapDefinitions(mapConfig, mapNodeList);

        return mapConfig;
    }

    public void parseFunctionalityProfile(MapConfig mapConfig, String functionalityProfile, ConfigReader reader)
    {
        try
        {
            InputStream is = reader.getConfigFile(functionalityProfile + ".xml", ConfigFileType.FUNCTIONALITY_PROFILE);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();

            parseWatermark(mapConfig, doc.getElementsByTagName("watermark"));
        }
        catch (Exception ex)
        {
            throw new IllegalArgumentException("A configuration "
                    + "with the name '" + functionalityProfile  + "' doesn't exist", ex);
        }
    }

    /**
     * Parse the information from the configruation file and populate
     * 
     * @param mapConfig
     * @param mapConfigElement
     */
    private void parseMapConfigDefinition(MapConfig mapConfig, Element mapConfigElement) {
        MapConfigDefinition mapConfigDefinition = new MapConfigDefinition();
        try {

            mapConfigDefinition.setInitialX(Double.parseDouble(mapConfigElement
                    .getAttribute(INITIAL_X_ATTR)));

            mapConfigDefinition.setInitialY(Double.parseDouble(mapConfigElement
                    .getAttribute(INITIAL_Y_ATTR)));

            mapConfigDefinition
                    .setInitialZoom(Integer.parseInt(mapConfigElement
                            .getAttribute(INITIAL_ZOOM_ATTR)));

            mapConfigDefinition.setMaxBoundsBottom(Double
                    .parseDouble(mapConfigElement
                            .getAttribute(MAX_BOUNDS_BOTTOM_ATTR)));

            mapConfigDefinition.setMaxBoundsLeft(Double
                    .parseDouble(mapConfigElement
                            .getAttribute(MAX_BOUNDS_LEFT_ATTR)));

            mapConfigDefinition.setMaxBoundsRight(Double
                    .parseDouble(mapConfigElement
                            .getAttribute(MAX_BOUNDS_RIGHT_ATTR)));

            mapConfigDefinition.setMaxBoundsTop(Double
                    .parseDouble(mapConfigElement
                            .getAttribute(MAX_BOUNDS_TOP_ATTR)));

            mapConfigDefinition.setMaxZoomLevels(Integer
                    .parseInt(mapConfigElement
                            .getAttribute(MAX_ZOOM_LEVELS_ATTR)));

            mapConfigDefinition.setProjection(mapConfigElement
                    .getAttribute(PROJECTION_ATTR));

            mapConfigDefinition.setUnits(mapConfigElement
                    .getAttribute(UNITS_ATTR));

            mapConfigDefinition.setSearchZoom(Integer.parseInt(mapConfigElement
                    .getAttribute(SEARCH_ZOOM_ATTR)));

            mapConfigDefinition.setAddressSearchResultsCount(Integer.parseInt(mapConfigElement
                    .getAttribute(SEARCH_RESULT_COUNT)));

            mapConfig.setMapConfigDefinition(mapConfigDefinition);
        } catch (NumberFormatException ex) {
            logger.error(" Invalid configuration " + ex.getMessage());
        }

    }

    private void parseAPIKeys(MapConfig mapConfig, NodeList apiNodeList) {
        List<ThirdPartyAPIKey> thirdPartyAPIKeys = new ArrayList<ThirdPartyAPIKey>();

        if (isValidNodeList(apiNodeList)) {
            for (int i = 0; i < apiNodeList.getLength(); i++) {
                Element apiList = (Element) apiNodeList.item(i);
                ThirdPartyAPIKey thirdPartyAPIKey = mapConfig
                        .createThirdPartyAPIKey();
                
                String serviceName = apiList.getAttribute("service");
                thirdPartyAPIKey.setService(LayerServiceType
                        .forName(serviceName));

                thirdPartyAPIKey.setKey(apiList.getTextContent());
                thirdPartyAPIKeys.add(thirdPartyAPIKey);
            }
        }
        mapConfig.setThirdPartyAPIKeys(thirdPartyAPIKeys);
    }

    private void parseMapDefinitions(MapConfig mapConfig, NodeList mapNodeList) {
        List<MapDefinition> mapDefinitionList = new ArrayList<MapDefinition>();
        if (isValidNodeList(mapNodeList)) {

            for (int i = 0; i < mapNodeList.getLength(); i++) {
                Element map = (Element) mapNodeList.item(i);
                MapDefinition mapDefinition = mapConfig.createMapDefinition();
                mapDefinition.setCopyright(map.getAttribute(COPYRIGHT_ATTR));
                mapDefinition.setMapName(map.getAttribute(NAME_ATTR));
                mapDefinition.setFriendlyName(map
                        .getAttribute(FRIENDLY_NAME_ATTR));
                mapDefinition.setImageMimeType(map
                        .getAttribute(IMAGE_MIME_TYPE_ATTR));
                mapDefinition.setRepositoryPath(map.getAttribute(PATH_ATTR));
                String serviceName = map.getAttribute(SERVICE_ATTR);
                try {
                    mapDefinition.setService(LayerServiceType
                            .forName(serviceName));
                } catch (IllegalArgumentException iax) {
                    throw new ParseException(iax.getMessage());
                }
                  
                // SJB: CONN-12265 - Deal with old Bing map Virtual Earth layer
                // styles (VEMapStyle.xxx)
                // Note that if you change this you'll need to look at the
                // equivalent in ConfigurationFactory.js.
                //                   We really should only be parsing these files in one place...
                String mn = map.getAttribute(NAME_ATTR);
                if ((serviceName.equals("Bing-Map"))
                        && (mn.startsWith("VEMapStyle."))) {
                    if (mn.equals("VEMapStyle.Aerial")) {
                        mn = "Bing Aerial";
                    } else if (mn.equals("VEMapStyle.Hybrid")) {
                        mn = "Bing Hybrid";
                    } else {
                        mn = "Bing Roads"; 
                    }
                }
                mapDefinition.setMapName(mn);

                String opacityString = map.getAttribute(OPACITY_ATTR);
                if (StringUtils.isBlank(opacityString)) {
                    mapDefinition.setOpacity(1);
                } else {
                    try {
                        mapDefinition.setOpacity(Float
                                .parseFloat(opacityString));
                    } catch (NumberFormatException e) {
                        throw new ParseException("Cannot parse opacity '" 
                                + opacityString+ "'");
                    }
                }

                mapDefinition.setUrl(map.getAttribute(URL_ATTR));
                NodeList layerList =  map.getElementsByTagName(LAYER);
                List <String> legendUrlList = new ArrayList<String>();
                for (int j = 0; j < layerList.getLength(); j++) {
                    Element layer =  (Element)layerList.item(j);
                    legendUrlList.add(layer.getAttribute(LEGEND_URL));
                }
                mapDefinition.setLegendUrlList(legendUrlList);
                mapDefinitionList.add(mapDefinition);
            }
        }
        mapConfig.setMapDefinitions(mapDefinitionList);

    }

    private void parseWatermark(MapConfig mapConfig, NodeList nodeList) {
        if (!isValidNodeList(nodeList)) {
            return; 
        }
        Watermark watermark = mapConfig.createWatermark();
        Element watermarkElement = (Element) nodeList.item(0);
        watermark.setImagePath(watermarkElement.getAttribute(URL_ATTR));
        try {
                watermark.setOpacity(Float.parseFloat(watermarkElement
                        .getAttribute(OPACITY_ATTR)));
        } catch (NumberFormatException e) {
                watermark.setOpacity(1.0f);
            }
        try {
                watermark.setTileHeight(Integer.parseInt(watermarkElement
                        .getAttribute(TILE_HEIGHT_ATTR)));
        } catch (NumberFormatException e) {
                watermark.setTileHeight(256);
            }
        try {
                watermark.setTileWidth(Integer.parseInt(watermarkElement
                        .getAttribute(TILE_WIDTH_ATTR)));
        } catch (NumberFormatException e) {
                watermark.setTileWidth(256);
            }
        }

    private boolean isValidNodeList(NodeList nodeList) {
        return !(nodeList == null || nodeList.getLength() == 0);
    }

    private Document parseXml(InputStream is) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(is);
            doc.normalize();
            return doc;
        } catch (Exception x) {
            throw new ParseException(x);
        }
    }

}
