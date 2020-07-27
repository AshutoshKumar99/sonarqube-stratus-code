package com.pb.stratus.controller.action;

import com.pb.stratus.controller.print.BoundingBox;
import com.pb.stratus.controller.print.content.LayerBean;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class MapImageRequestParametersTest
{

    private MapImageRequestParameters params;

    @Before
    public void setUp()
    {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addParameter(PrintAction.TEMPLATE_NAME_PARAM, 
                "someTemplate");
        mockRequest.addParameter(MapImageRequestParameters.LAYERS_PARAM,
                "[{name:'basemap1'},{name:'overlay1'},{name:'overlay2'}]");
        mockRequest.addParameter(MapImageRequestParameters.NORTH_PARAM, "2");
        mockRequest.addParameter(MapImageRequestParameters.SOUTH_PARAM, "1");
        mockRequest.addParameter(MapImageRequestParameters.WEST_PARAM, "1");
        mockRequest.addParameter(MapImageRequestParameters.EAST_PARAM, "2");
        mockRequest.addParameter(MapImageRequestParameters.SRS_PARAM, 
                "someSrs");
        mockRequest.addParameter(MapImageRequestParameters.LAYERS_PARAM, 
                "basemap1,overlay1,overlay2");
        mockRequest.addParameter(
                MapImageRequestParameters.MAP_CONFIG_NAME_PARAM, 
                "someMapConfig");
        mockRequest.addParameter(MapImageRequestParameters.RESOLUTION_PARAM,"150");
        params = new MapImageRequestParameters(mockRequest);
    }
    
    @Test
    public void shouldProvideExpectedBoundingBox()
    {
        BoundingBox expected = new BoundingBox(2, 1, 1, 2, "someSrs");
        assertEquals(expected, params.getBoundingBox());
    }

    @Test
    public void shouldProvideParsedLayers()
    {
        List<LayerBean> expected = Arrays.asList(new LayerBean("basemap1"), new LayerBean("overlay1"),
                new LayerBean("overlay2"));
        assertEquals(expected.get(0).getName(), params.getLayers().get(0).getName());
        assertEquals(expected.get(1).getName(), params.getLayers().get(1).getName());
        assertEquals(expected.get(2).getName(), params.getLayers().get(2).getName());
    }
    
    @Test
    public void shouldProvideMapConfigName()
    {
        assertEquals("someMapConfig", params.getMapConfigName());
    }

    @Test
    public void shouldProvidePrintResolution()
    {
        assertEquals(150, params.getPrintResolution());
    }

}
