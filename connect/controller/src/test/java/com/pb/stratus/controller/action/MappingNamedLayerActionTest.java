package com.pb.stratus.controller.action;

import com.mapinfo.midev.service.mapping.v1.DescribeNamedLayerRequest;
import com.mapinfo.midev.service.mapping.v1.DescribeNamedLayerResponse;
import com.pb.stratus.controller.MapNotFoundException;
import com.pb.stratus.controller.service.MappingService;
import com.pb.stratus.controller.util.MockSupport;
import com.pb.stratus.core.configuration.ControllerConfiguration;
import com.pb.stratus.util.JaxbUtil;
import org.easymock.Capture;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.easymock.EasyMock.*;

/**
 * Created with IntelliJ IDEA.
 * User: ma050si
 * Date: 11/11/13
 * Time: 3:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class MappingNamedLayerActionTest {
    private MappingNamedLayerAction mappingNamedLayerAction;
    private MockHttpServletRequest mockRequest;
    private MockHttpServletResponse mockResponse;
    private MappingService mockMappingWebService;
    private MockSupport mockSupport;
    private ControllerConfiguration mockConfig;

    @Before
    public void setUp() {
        mockSupport = new MockSupport();
        mockMappingWebService = mockSupport.createMock(
                MappingService.class);
        mappingNamedLayerAction = new MappingNamedLayerAction(mockMappingWebService);
        mockConfig = mockSupport
                .createMock(ControllerConfiguration.class);
        mappingNamedLayerAction.setConfig(mockConfig);
    }

    @Test
    public void shouldReturnIfRequestParamNamedLayerIsEmpty() throws IOException, ServletException {
        mockRequest = new MockHttpServletRequest();
        mockResponse = new MockHttpServletResponse();
        mockRequest.addParameter("namedLayer", "");
        Object result = null;
        try {
            result = mappingNamedLayerAction.createObject(mockRequest);
        } catch (MapNotFoundException me) {
            System.out.println(me.getMessage());
        }
        assertNull(result);
    }

    @Test
    public void testCreateObject() throws Exception {
        mockRequest = new MockHttpServletRequest();
        mockResponse = new MockHttpServletResponse();
        mockRequest.addParameter("namedMap", "testMap1");
        DescribeNamedLayerResponse describeNamedLayerResponse = createMapResponse();
        Capture<DescribeNamedLayerRequest> capture = new Capture<DescribeNamedLayerRequest>();
        expect(mockMappingWebService.describeNamedLayer(capture(capture))).andReturn(describeNamedLayerResponse);
        replay(mockMappingWebService);
        Object result = mappingNamedLayerAction.createObject(mockRequest);
        assertTrue(((DescribeNamedLayerResponse) result).getLayer().size() > 0);
    }

    private DescribeNamedLayerResponse createMapResponse() throws Exception {

        DescribeNamedLayerResponse response = JaxbUtil.createObject(
                "describeNamedLayer.xml",
                DescribeNamedLayerResponse.class, MappingNamedLayerActionTest.class);

        return response;
    }
}
