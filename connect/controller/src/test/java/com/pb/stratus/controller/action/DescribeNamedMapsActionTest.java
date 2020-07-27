package com.pb.stratus.controller.action;

import com.mapinfo.midev.service.mapping.v1.*;
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
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

/**
 * Created with IntelliJ IDEA.
 * User: VI021CH
 * Date: 1/30/15
 * Time: 11:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class DescribeNamedMapsActionTest extends ControllerActionTestBase {

    private DescribeNamedMapsAction describeNamedMapsAction;
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
        describeNamedMapsAction = new DescribeNamedMapsAction(mockMappingWebService);
        mockConfig = mockSupport
                .createMock(ControllerConfiguration.class);
        describeNamedMapsAction.setConfig(mockConfig);
    }

    @Test
    public void shouldReturnIfRequestParamRepositoryPathIsEmpty() throws IOException, ServletException {
        mockRequest = new MockHttpServletRequest();
        mockResponse = new MockHttpServletResponse();
        mockRequest.addParameter("repositoryPath", "");
        Object result = null;
        try {
            result = describeNamedMapsAction.createObject(mockRequest);
        } catch (MapNotFoundException me) {
            System.out.println(me.getMessage());
        }
        assertNull(result);
    }

    @Test
    public void shouldThrowExceptionIfMapNameNotPresent() throws IOException, ServletException {
        mockRequest = new MockHttpServletRequest();
        String[] wrongValues = new String []{"wrongName1","wrongName2"};
        mockRequest.addParameter("repositoryPath",wrongValues);
        Object result = null;
        try {
            result = describeNamedMapsAction.createObject(mockRequest);
        } catch (MapNotFoundException me) {
            System.out.println(me.getMessage());
        }
        assertNull(result);

    }

    @Test
    public void testCreateObjectIfMapExist() throws Exception {
        mockRequest = new MockHttpServletRequest();
        mockResponse = new MockHttpServletResponse();
        String[] correctValues = new String []{"customerstratustenant1_noida/namedMap/testMap1","customerstratustenant1_noida/namedMap/testMap2"};
        mockRequest.addParameter("repositoryPath", correctValues);
        DescribeNamedMapsResponse describeNamedMapsResponse = createMapResponse();
        Capture<DescribeNamedMapsRequest> capture = new Capture<DescribeNamedMapsRequest>();
        expect(mockMappingWebService.describeNamedMaps(capture(capture))).andReturn(describeNamedMapsResponse);
        replay(mockMappingWebService);
        Object result = describeNamedMapsAction.createObject(mockRequest);
        assertTrue(((DescribeNamedMapsResponse) result).getMapDescriptions().getMapDescription().get(0).getMap().getLayer().size()>0);
    }

    private DescribeNamedMapsResponse createMapResponse() throws Exception {

        DescribeNamedMapsResponse response = JaxbUtil.createObject(
                "describeNamedMaps.xml",
                DescribeNamedMapsResponse.class, DescribeNamedMapsActionTest.class);

        return response;
    }
}