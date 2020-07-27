package com.pb.stratus.controller.print;

import com.pb.stratus.controller.print.config.LayerServiceType;
import com.pb.stratus.controller.print.config.MapConfig;
import com.pb.stratus.controller.print.config.MapConfig.MapDefinition;
import com.pb.stratus.controller.print.config.MapConfigParser;
import com.pb.stratus.core.exception.ParseException;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MapConfigParserTest
{
    
    private MapConfigParser parser;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        parser = new MapConfigParser();
    }
    
    @Test
    public void shouldParseAllLayers() throws Exception
    {

        String content = "<map map-name=\"overlay1\" " 
                + "service=\"Envinsa-Mapping\"/>" 
                + "<map map-name=\"overlay2\" " 
                + "service=\"Envinsa-Mapping\"/>"
                + "<map map-name=\"basemap1\" " 
                + "service=\"Envinsa-Tile\"/>";
        MapConfig mapConfig = createMapConfig(content);
        
        List<MapDefinition> layers = mapConfig.getMapDefinitions(); 
        assertEquals(3, layers.size());
        assertEquals("overlay1", layers.get(0).getMapName());
        assertEquals("overlay2", layers.get(1).getMapName());
        assertEquals("basemap1", layers.get(2).getMapName());
    }

    @Test
    public void shouldParseFriendlyNameInMapDefinition() throws Exception
    {
    	String content = "<map service=\"Envinsa-Mapping\" "
    		+ "friendly-name=\"Some Friendly Name\" "
    		+ "map-name=\"Actual Map Name\"/>"
	        + "<map map-name=\"basemap1\" "
	        + "service=\"Envinsa-Tile\"/>";
        MapConfig mapConfig = createMapConfig(content);
        List<MapDefinition> layers = mapConfig.getMapDefinitions();
        assertEquals(2, layers.size());
        assertEquals("Some Friendly Name", layers.get(0).getFriendlyName());
    }
 
    @Test
    // SJB: CONN-12265: New test to ensure old Bing map Virtual Earth styles are converted
    public void convertVEMapTypeNameToBing() throws Exception
    {
        String content = "<map service=\"Bing-Map\" "
            + "friendly-name=\"Aerial\" "
            + "map-name=\"VEMapStyle.Aerial\"/> "
	        + "<map service=\"Bing-Map\" "
            + "map-name=\"VEMapStyle.Shaded\" " 
            + "friendly-name=\"Roads\" /> "
			+ "<map map-name=\"VEMapStyle.Hybrid\" "
	        + "service=\"Bing-Map\" "
			+ "friendly-name=\"Hybrid\"/>";
        MapConfig mapConfig = createMapConfig(content);
        List<MapDefinition> layers = mapConfig.getMapDefinitions();
        assertEquals(3, layers.size());
        assertEquals("Aerial", layers.get(0).getFriendlyName());
        assertEquals("Roads", layers.get(1).getFriendlyName());
        assertEquals("Hybrid", layers.get(2).getFriendlyName());
        assertEquals("Bing Aerial", layers.get(0).getMapName());
        assertEquals("Bing Roads", layers.get(1).getMapName());
        assertEquals("Bing Hybrid", layers.get(2).getMapName());
    }

    @Test
    public void shouldAcceptFileWithoutLayers() throws Exception
    {
        MapConfig mapConfig = createMapConfig("");
        assertEquals(0, mapConfig.getMapDefinitions().size());
    }
    
    @Test
    public void shouldParseCopyright() throws Exception
    {
        String content = "<map map-name=\"overlay\" " 
            + "service=\"Envinsa-Mapping\" copyright=\"someCopyrightText\"/>"; 
        MapConfig mapConfig = createMapConfig(content);
        assertEquals("someCopyrightText", mapConfig.getMapDefinitions()
                .get(0).getCopyright());
    }
    
    @Test
    public void shouldParseOpacity() throws Exception
    {
        String content = "<map map-name=\"overlay\" " 
            + "service=\"Envinsa-Mapping\" opacity=\"0.123\"/>"; 
        MapConfig mapConfig = createMapConfig(content);
        assertEquals(0.123, mapConfig.getMapDefinitions()
                .get(0).getOpacity(), 0.0000001d);
    }
    
    @Test
    public void shouldParseService() throws Exception
    {
        String content = "<map map-name=\"overlay\" " 
            + "service=\"Envinsa-Mapping\"/>"; 
        MapConfig mapConfig = createMapConfig(content);
        assertEquals(LayerServiceType.MAPPING, 
                mapConfig.getMapDefinitions().get(0).getService());
    }
    
    @Test
    public void shouldFailIfServiceIsMissingFromLayer() throws Exception
    {
        String content = "<map map-name=\"overlay\"/>"; 
        try
        {
            createMapConfig(content);
        }
        catch (ParseException px)
        {
            // expected
        }
    }
    
    @Test
    public void shouldFailIfUnknownServiceOnLayer() throws Exception
    {
        String content = "<map map-name=\"overlay\" " 
                + "service=\"unknownService\"/>"; 
        try
        {
            createMapConfig(content);
        }
        catch (ParseException px)
        {
            // expected
        }
    }
    
    @Test
    public void shouldAssume1IfOpacityIsMissing() throws Exception
    {
        String content = "<map map-name=\"overlay\" " 
            + "service=\"Envinsa-Mapping\"/>"; 
        MapConfig mapConfig = createMapConfig(content);
        assertEquals(1, mapConfig.getMapDefinitions()
                .get(0).getOpacity(), 0d);
    }
    
    @Test
    public void shouldParseApiKey() throws Exception
    {
        String content = "<api-keys><key service=\"Google-Map\">" 
            + "someKeyValue</key></api-keys>";
        MapConfig mapConfig = createMapConfig(content);
        assertEquals("someKeyValue", 
                mapConfig.getThirdPartyAPIKeys().get(0).getKey());
    }
    
    @Test
    public void shouldParseApiKeyService() throws Exception
    {
        String content = "<api-keys><key service=\"Google-Map\">" 
            + "someKeyValue</key></api-keys>";
        MapConfig mapConfig = createMapConfig(content);
        assertEquals(LayerServiceType.GOOGLE, 
                mapConfig.getThirdPartyAPIKeys().get(0).getService());
    }
    
    @Test
    public void shouldParseWatermarkUrl() throws Exception
    {
        /*String content = "<watermark url=\"someImageUrl\" "
                + "tile-height=\"300\" tile-width=\"300\"/>";
        MapConfig mapConfig = createMapConfig(content);
        assertEquals("someImageUrl", mapConfig.getWatermark().getImagePath());*/
    }
    
    @Test
    public void shouldParseWatermarkOpacity() throws Exception
    {
        /*String content = "<watermark url=\"someImageUrl\" opacity=\"1.23\"/>";
        MapConfig mapConfig = createMapConfig(content);
        assertEquals(1.23, mapConfig.getWatermark().getOpacity(), 0.000001d);*/
    }
    
    @Test
    public void shouldUseDefaultSizeIfNoWatermarkSizeSpecified()
            throws Exception
    {
        /*String content = "<watermark url=\"someImageUrl\" opacity=\"1.23\"/>";
        MapConfig mapConfig = createMapConfig(content);
        assertEquals(256, mapConfig.getWatermark().getTileWidth());
        assertEquals(256, mapConfig.getWatermark().getTileHeight());*/
        
    }

    @Test
    public void shouldParseGazetteerName() throws Exception
    {
        String content = "<gazetteer-name>myGazetteer</gazetteer-name>";
        MapConfig mapConfig = createMapConfig(content);
        assertEquals("myGazetteer", mapConfig.getDefaultGazetteerName());
    }

	@Test
	public void shouldAllowNoGazetteerName() throws Exception
	{
		String content = "";
		MapConfig mapConfig = createMapConfig(content);
		assertEquals(null, mapConfig.getDefaultGazetteerName());
	}

    private MapConfig createMapConfig(String content) throws Exception
    {
        return parser.parseMapConfig(createInputStream(content));
    }

    private InputStream createInputStream(String content) throws Exception
    {
        content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><map-config>" 
            + content + "</map-config>";
        byte[] b = content.getBytes("UTF-8");
        return new ByteArrayInputStream(b);
    }

    @Test
    public void shouldParseAllLayersWithRepoPath() throws Exception
    {
        String content = "<map map-name=\"overlay1\" "
                + "service=\"Envinsa-Mapping\" "
                + "repository-path=\"customerstratustenant1_noida/NamedMaps/namedMap1\"/>"
                + "<map map-name=\"overlay2\" "
                + "service=\"Envinsa-Mapping\" "
                + "repository-path=\"customerstratustenant1_noida/NamedMaps/namedMap2\"/>"
                + "<map map-name=\"basemap1\" "
                + "service=\"Envinsa-Tile\"/>";
        MapConfig mapConfig = createMapConfig(content);

        List<MapDefinition> layers = mapConfig.getMapDefinitions();
        assertEquals(3, layers.size());
        assertEquals("overlay1", layers.get(0).getMapName());
        assertEquals("overlay2", layers.get(1).getMapName());
        assertEquals("basemap1", layers.get(2).getMapName());
    }

}
