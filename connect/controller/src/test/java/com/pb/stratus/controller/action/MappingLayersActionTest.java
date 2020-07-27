package com.pb.stratus.controller.action;

import com.mapinfo.midev.service.mapping.v1.DescribeNamedMapRequest;
import com.mapinfo.midev.service.mapping.v1.DescribeNamedMapResponse;
import com.mapinfo.midev.service.mapping.v1.NamedLayer;
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

public class MappingLayersActionTest extends ControllerActionTestBase {

    private MappingLayersAction mappingLayersAction;
    private MockHttpServletRequest mockRequest;
    private MockHttpServletResponse mockResponse;
    private MappingService mockMappingWebService;
    private MockSupport mockSupport;
    private ControllerConfiguration mockConfig;
    NamedLayer namedLayer = null;

    @Before
    public void setUp() {
        mockSupport = new MockSupport();
        mockMappingWebService = mockSupport.createMock(
                MappingService.class);
        mappingLayersAction = new MappingLayersAction(mockMappingWebService);
        mockConfig = mockSupport
                .createMock(ControllerConfiguration.class);
        mappingLayersAction.setConfig(mockConfig);
    }

    @Test
    public void shouldReturnIfRequestParamNamedMapIsEmpty() throws IOException, ServletException {
        mockRequest = new MockHttpServletRequest();
        mockResponse = new MockHttpServletResponse();
        mockRequest.addParameter("namedMap", "");
        Object result = null;
        try {
            result = mappingLayersAction.createObject(mockRequest);
        } catch (MapNotFoundException me) {
            System.out.println(me.getMessage());
        }
        assertNull(result);
    }


    @Test
    public void testCreateObjectIfMapExist() throws Exception {
        mockRequest = new MockHttpServletRequest();
        mockResponse = new MockHttpServletResponse();
        mockRequest.addParameter("namedMap", "testMap1");
        DescribeNamedMapResponse describeNamedMapResponse = createMapResponse();
        Capture<DescribeNamedMapRequest> capture = new Capture<DescribeNamedMapRequest>();
        expect(mockMappingWebService.describeNamedMap(capture(capture))).andReturn(describeNamedMapResponse);
        replay(mockMappingWebService);
        Object result = mappingLayersAction.createObject(mockRequest);
        assertTrue(((DescribeNamedMapResponse) result).getMap().getLayer().size() > 0);
    }

    @Test
    public void shouldThrowExceptionIfMapNameNotPresent() throws IOException, ServletException {
        mockRequest = new MockHttpServletRequest();
        mockRequest.addParameter("namedMap", "wrongMapName");
        Object result = null;
        try {
            result = mappingLayersAction.createObject(mockRequest);
        } catch (MapNotFoundException me) {
            System.out.println(me.getMessage());
        }
        assertNull(result);

    }

    private DescribeNamedMapResponse createMapResponse() throws Exception {

        DescribeNamedMapResponse response = JaxbUtil.createObject(
                "describeNamedMap.xml",
                DescribeNamedMapResponse.class, MappingLayersActionTest.class);

        return response;
    }

    @Test
    public void testCreateObjectIfMapExistWithRepositoryPath() throws Exception {
        mockRequest = new MockHttpServletRequest();
        mockResponse = new MockHttpServletResponse();
        mockRequest.addParameter("namedMap", "testMap1");
        mockRequest.addParameter("repositoryPath", "customerstratustenant1_noida/namedMap/testMap1");
        DescribeNamedMapResponse describeNamedMapResponse = createMapResponse();
        Capture<DescribeNamedMapRequest> capture = new Capture<DescribeNamedMapRequest>();
        expect(mockMappingWebService.describeNamedMap(capture(capture))).andReturn(describeNamedMapResponse);
        replay(mockMappingWebService);
        Object result = mappingLayersAction.createObject(mockRequest);
        assertTrue(((DescribeNamedMapResponse) result).getMap().getLayer().size() > 0);
    }
}
