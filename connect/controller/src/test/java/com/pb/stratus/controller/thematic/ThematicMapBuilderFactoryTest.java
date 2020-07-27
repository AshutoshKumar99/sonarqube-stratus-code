package com.pb.stratus.controller.thematic;


import com.mapinfo.midev.service.mapping.v1.FeatureLayer;
import com.mapinfo.midev.service.mapping.v1.Map;
import com.mapinfo.midev.service.style.v1.MapBasicCompositeStyle;
import com.mapinfo.midev.service.theme.v1.IndividualValueTheme;
import com.mapinfo.midev.service.theme.v1.RangeTheme;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.IOException;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: ra007gi
 * Date: 10/7/14
 * Time: 5:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class ThematicMapBuilderFactoryTest {
    private ThematicMapBuilderFactory factory;

    @Before
    public void setUp() throws Exception
    {
        factory = new ThematicMapBuilderFactory();

    }

    @Test
    public void shouldGetMapForRangeThematicPolygon()
    {
        String json = "{\"thematicMapType\": \"Range\",\"table\": \"/QA-Maps/NamedTables/ListedBuildings\",\"tableColumn\": \"LB_No\",\"thematicMetaData\": {\"geometryType\": \"Polygon\",\"rangeThemeType\":\"EqualRange\",\"rangeCount\": \"5\",\"style\": {\"polygonStyleStartColor\": \"yellow\",\"polygonStyleEndColor\": \"blue\",\"polygonStylePattern\": \"2\",\"lineStyleColor\": \"red\",\"lineStylePattern\": \"1\",\"lineStyleWidth\": \"2\"}}}";

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addParameter("thematicMapJson",json);
        Map map = null;
        try {
            map = factory.createThematicMap(mockRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(map);
        assertTrue(map.getLayer().get(0) instanceof FeatureLayer);
        FeatureLayer layer = (FeatureLayer) map.getLayer().get(0);
        assertTrue(layer.getThemeList().getTheme().get(0) instanceof RangeTheme);
        RangeTheme theme = (RangeTheme) layer.getThemeList().getTheme().get(0);
        assertTrue(theme.getStartStyle() instanceof MapBasicCompositeStyle);
    }

    @Test
    public void shouldGetMapForRangeThematicLine()
    {
        String json = "{\"thematicMapType\": \"Range\",\"table\": \"/QA-Maps/NamedTables/ListedBuildings\",\"tableColumn\": \"LB_No\",\"thematicMetaData\": {\"geometryType\": \"Line\",\"rangeThemeType\":\"EqualRange\",\"rangeCount\": \"5\",\"style\": {\"lineStyleStartColor\": \"red\",\"lineStyleEndColor\": \"red\",\"lineStylePattern\": \"1\",\"lineStyleWidth\": \"2\"}}}";

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addParameter("thematicMapJson",json);
        Map map = null;
        try {
            map = factory.createThematicMap(mockRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(map);
        assertTrue(map.getLayer().get(0) instanceof FeatureLayer);
        FeatureLayer layer = (FeatureLayer) map.getLayer().get(0);
        assertTrue(layer.getThemeList().getTheme().get(0) instanceof RangeTheme);
        RangeTheme theme = (RangeTheme) layer.getThemeList().getTheme().get(0);
        assertTrue(theme.getStartStyle() instanceof MapBasicCompositeStyle);
    }

    @Test
    public void shouldGetMapForRangeThematicPoint()
    {
        String json = "{\"thematicMapType\": \"Range\",\"table\": \"/QA-Maps/NamedTables/ListedBuildings\",\"tableColumn\": \"LB_No\",\"thematicMetaData\": {\"geometryType\": \"Point\",\"rangeThemeType\":\"EqualRange\",\"rangeCount\": \"5\",\"style\": {\"pointStyleShape\": \"33\",\"pointStyleStartColor\": \"red\",\"pointStyleEndColor\": \"blue\",\"pointStyleFontSize\": \"12\",\"pointStyleFontName\": \"Mapinfo Symbols\",\"pointStyleBorder\": \"none\"}}}";

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addParameter("thematicMapJson",json);
        Map map = null;
        try {
            map = factory.createThematicMap(mockRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(map);
        assertTrue(map.getLayer().get(0) instanceof FeatureLayer);
        FeatureLayer layer = (FeatureLayer) map.getLayer().get(0);
        assertTrue(layer.getThemeList().getTheme().get(0) instanceof RangeTheme);
        RangeTheme theme = (RangeTheme) layer.getThemeList().getTheme().get(0);
        assertTrue(theme.getStartStyle() instanceof MapBasicCompositeStyle);

    }

    @Test
    public void shouldGetMapForIndividualThematicPolygon()
    {
        String json = "{\"thematicMapType\":\"IndividualValueTheme\",\"table\":\"/Samples/NamedTables/USA\",\"tableName\":\"USA\",\"tableColumn\":\"State\",\"tableColumnType\":\"INT\",\"thematicMetaData\":{\"geometryType\":\"Polygon\",\"count\":4,\"binList\":[{\"expressionValue\":\"CA\",\"style\":{\"polygonStyleColor\":\"#00ff00\",\"polygonStylePattern\":\"2\",\"lineStyleColor\":\"#000000\",\"lineStylePattern\":\"1\",\"lineStyleWidth\":\"3\"}},{\"expressionValue\":\"WY\",\"style\":{\"polygonStyleColor\":\"#0000FF\",\"polygonStylePattern\":\"2\",\"lineStyleColor\":\"#000000\",\"lineStylePattern\":\"1\",\"lineStyleWidth\":\"3\"}},{\"expressionValue\":\"CO\",\"style\":{\"polygonStyleColor\":\"#00ffff\",\"polygonStylePattern\":\"2\",\"lineStyleColor\":\"#000000\",\"lineStylePattern\":\"1\",\"lineStyleWidth\":\"3\"}},{\"expressionValue\":\"TX\",\"style\":{\"polygonStyleColor\":\"#ffff00\",\"polygonStylePattern\":\"2\",\"lineStyleColor\":\"#000000\",\"lineStylePattern\":\"1\",\"lineStyleWidth\":\"3\"}}],\"allOthers\":{\"style\":{\"polygonStyleColor\":\"#FF0000\",\"polygonStylePattern\":\"2\",\"lineStyleColor\":\"#000000\",\"lineStylePattern\":\"1\",\"lineStyleWidth\":\"3\"}}}}";

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addParameter("thematicMapJson",json);
        Map map = null;
        try {
            map = factory.createThematicMap(mockRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(map);
        assertTrue(map.getLayer().get(0) instanceof FeatureLayer);
        FeatureLayer layer = (FeatureLayer) map.getLayer().get(0);
        assertEquals(layer.getTable().getName(), "/Samples/NamedTables/USA");
        assertTrue(layer.getThemeList().getTheme().get(0) instanceof IndividualValueTheme);
        IndividualValueTheme theme = (IndividualValueTheme) layer.getThemeList().getTheme().get(0);
        assertEquals(theme.getExpression(), "State");
        assertNotNull(theme.getBinList());
        assertTrue(theme.getBinList().getBin().size() > 0);
        assertTrue(theme.getAllOthers() instanceof MapBasicCompositeStyle);
        assertNotNull(theme.getExpression());
    }

    @Test
    public void shouldGetMapForIndividualThematicPoint()
    {
        String json = "{\"thematicMapType\":\"IndividualValueTheme\",\"table\":\"/Samples/NamedTables/USA\",\"tableName\":\"USA\",\"tableColumn\":\"State\",\"tableColumnType\":\"STRING\",\"thematicMetaData\":{\"geometryType\":\"Point\",\"binList\":[{\"expressionValue\":\"CA\",\"style\":{\"pointStyleShape\":\"34\",\"pointStyleColor\":\"#FFFF00\",\"pointStyleFontSize\":30,\"pointStyleFontName\":\"MapInfoOilandGas\",\"pointStyleBorder\":\"normal\"}},{\"expressionValue\":\"WY\",\"style\":{\"pointStyleShape\":\"34\",\"pointStyleColor\":\"#0000FF\",\"pointStyleFontSize\":32,\"pointStyleFontName\":\"MapInfoOilandGas\",\"pointStyleBorder\":\"normal\"}},{\"expressionValue\":\"CO\",\"style\":{\"pointStyleShape\":\"34\",\"pointStyleColor\":\"#CA0000\",\"pointStyleFontSize\":33,\"pointStyleFontName\":\"MapInfoOilandGas\",\"pointStyleBorder\":\"normal\"}},{\"expressionValue\":\"TX\",\"style\":{\"pointStyleShape\":\"34\",\"pointStyleColor\":\"#00FF00\",\"pointStyleFontSize\":35,\"pointStyleFontName\":\"MapInfoOilandGas\",\"pointStyleBorder\":\"normal\"}}],\"allOthers\":{\"style\":{\"pointStyleShape\":\"34\",\"pointStyleColor\":\"#FF0000\",\"pointStyleFontSize\":35,\"pointStyleFontName\":\"MapInfoOilandGas\",\"pointStyleBorder\":\"normal\"}}}}";

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addParameter("thematicMapJson",json);
        Map map = null;
        try {
            map = factory.createThematicMap(mockRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(map);
        assertTrue(map.getLayer().get(0) instanceof FeatureLayer);
        FeatureLayer layer = (FeatureLayer) map.getLayer().get(0);
        assertEquals(layer.getTable().getName(), "/Samples/NamedTables/USA");
        assertTrue(layer.getThemeList().getTheme().get(0) instanceof IndividualValueTheme);
        IndividualValueTheme theme = (IndividualValueTheme) layer.getThemeList().getTheme().get(0);
        assertEquals(theme.getExpression(), "State");
        assertNotNull(theme.getBinList());
        assertTrue(theme.getBinList().getBin().size() > 0);
        assertTrue(theme.getAllOthers() instanceof MapBasicCompositeStyle);
        assertNotNull(theme.getExpression());
    }

    @Test
    public void shouldGetMapForIndividualThematicLine()
    {
        String json = "{\"thematicMapType\":\"IndividualValueTheme\",\"table\":\"/Samples/NamedTables/USA\",\"tableName\":\"USA\",\"tableColumn\":\"State\",\"tableColumnType\":\"STRING\",\"thematicMetaData\":{\"geometryType\":\"Line\",\"binList\":[{\"expressionValue\":\"CA\",\"style\":{\"lineStyleColor\":\"#0000FF\",\"lineStylePattern\":\"1\",\"lineStyleWidth\":2}},{\"expressionValue\":\"WY\",\"style\":{\"lineStyleColor\":\"#00FF00\",\"lineStylePattern\":\"1\",\"lineStyleWidth\":2}},{\"expressionValue\":\"CO\",\"style\":{\"lineStyleColor\":\"#00AA00\",\"lineStylePattern\":\"1\",\"lineStyleWidth\":2}},{\"expressionValue\":\"TX\",\"style\":{\"lineStyleColor\":\"#00CC00\",\"lineStylePattern\":\"1\",\"lineStyleWidth\":2}}],\"allOthers\":{\"style\":{\"lineStyleColor\":\"#FF0000\",\"lineStylePattern\":\"1\",\"lineStyleWidth\":2}}}}";

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addParameter("thematicMapJson",json);
        Map map = null;
        try {
            map = factory.createThematicMap(mockRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(map);
        assertTrue(map.getLayer().get(0) instanceof FeatureLayer);
        FeatureLayer layer = (FeatureLayer) map.getLayer().get(0);
        assertEquals(layer.getTable().getName(), "/Samples/NamedTables/USA");
        assertTrue(layer.getThemeList().getTheme().get(0) instanceof IndividualValueTheme);
        IndividualValueTheme theme = (IndividualValueTheme) layer.getThemeList().getTheme().get(0);
        assertEquals(theme.getExpression(), "State");
        assertTrue(theme.getBinList().getBin().size() > 0);
        assertTrue(theme.getAllOthers() instanceof MapBasicCompositeStyle);
        assertNotNull(theme.getExpression());
    }

    @Test
    public void shouldGetMapForIndividualThematicPolygonWithoutAllOthersNode()
    {
        String json = "{\"thematicMapType\":\"IndividualValueTheme\",\"table\":\"/Samples/NamedTables/USA\",\"tableName\":\"USA\",\"tableColumn\":\"State\",\"tableColumnType\":\"STRING\",\"thematicMetaData\":{\"geometryType\":\"Polygon\",\"count\":4,\"binList\":[{\"expressionValue\":\"CA\",\"style\":{\"polygonStyleColor\":\"#00ff00\",\"polygonStylePattern\":\"2\",\"lineStyleColor\":\"#000000\",\"lineStylePattern\":\"1\",\"lineStyleWidth\":\"3\"}},{\"expressionValue\":\"WY\",\"style\":{\"polygonStyleColor\":\"#0000FF\",\"polygonStylePattern\":\"2\",\"lineStyleColor\":\"#000000\",\"lineStylePattern\":\"1\",\"lineStyleWidth\":\"3\"}},{\"expressionValue\":\"CO\",\"style\":{\"polygonStyleColor\":\"#00ffff\",\"polygonStylePattern\":\"2\",\"lineStyleColor\":\"#000000\",\"lineStylePattern\":\"1\",\"lineStyleWidth\":\"3\"}},{\"expressionValue\":\"TX\",\"style\":{\"polygonStyleColor\":\"#ffff00\",\"polygonStylePattern\":\"2\",\"lineStyleColor\":\"#000000\",\"lineStylePattern\":\"1\",\"lineStyleWidth\":\"3\"}}]}}";

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addParameter("thematicMapJson",json);
        Map map = null;
        try {
            map = factory.createThematicMap(mockRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(map);
        assertTrue(map.getLayer().get(0) instanceof FeatureLayer);
        FeatureLayer layer = (FeatureLayer) map.getLayer().get(0);
        assertEquals(layer.getTable().getName(), "/Samples/NamedTables/USA");
        assertTrue(layer.getThemeList().getTheme().get(0) instanceof IndividualValueTheme);
        IndividualValueTheme theme = (IndividualValueTheme) layer.getThemeList().getTheme().get(0);
        assertEquals(theme.getExpression(), "State");
        assertNotNull(theme.getBinList());
        assertTrue(theme.getBinList().getBin().size() > 0);
        assertTrue(theme.getAllOthers() instanceof MapBasicCompositeStyle);
        assertNotNull(theme.getExpression());
    }
}
