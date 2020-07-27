package com.pb.stratus.controller.action;

import com.mapinfo.midev.service.units.v1.DistanceUnit;
import com.pb.stratus.controller.IllegalRequestException;
import com.pb.stratus.controller.featuresearch.FeatureService;
import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.service.SearchNearestParams;
import com.pb.stratus.controller.util.MockSupport;
import org.easymock.Capture;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;

/*
 * Test the feature search nearest action class.
 */
public class FeatureSearchNearestActionTest extends FeatureSearchActionTestBase
{

    private FeatureSearchNearestAction searchNearestAction;

    private MockSupport mockSupport;
    
    private FeatureSearchResult expectedResult;
    
    private Capture<SearchNearestParams> capture;
    

    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        mockSupport = new MockSupport();
        programRequest();
        FeatureService featureService = mockSupport.createMock(
                FeatureService.class);
        expectedResult = createDummyResult();
        capture = new Capture<SearchNearestParams>();
        expect(featureService.searchNearest(capture(capture)))
                .andReturn(expectedResult);
        mockSupport.replayAllMocks();
        searchNearestAction = new FeatureSearchNearestAction(featureService);
    }

    @Test
    public void testCreateObject() throws Exception
    {
        Object actualResult = searchNearestAction.createObject(getRequest());
        assertEquals(expectedResult, actualResult);
        
        //FIXME more asserts
        SearchNearestParams params = capture.getValue();
        assertEquals("testTable", params.getTable());
        assertFalse(params.isIncludeGeometry());
        
        mockSupport.verifyAllMocks();
    }
    
    @Test
    public void testCreateObjectCustomDistanceUnit() throws Exception
    {
        MockHttpServletRequest request = getRequest();
        request.addParameter("distanceUnit", "MiLE");
        searchNearestAction.createObject(request);
        SearchNearestParams params = capture.getValue();
        assertEquals(DistanceUnit.MILE, params.getDistance().getUom());
    }
    
    @Test
    public void testCreateObjectUnknownDistanceUnit() throws Exception
    {
        MockHttpServletRequest request = getRequest();
        request.addParameter("distanceUnit", "clicks");
        try
        {
            searchNearestAction.createObject(request);
            fail("No IllegalRequestException thrown");
        }
        catch (IllegalRequestException irx)
        {
            // expected
        }
    }
    
    @Test
    public void testCreateObjectNoDistanceButDistanceUnit() throws Exception
    {
        MockHttpServletRequest request = getRequest();
        request.addParameter("distanceUnit", "MILE");
        request.removeParameter("maxDistance");
        searchNearestAction.createObject(request);
        SearchNearestParams params = capture.getValue();
        assertNull(params.getDistance());
    }

    @Test
    public void testCreateObjectUnknownReturnedDistanceUnit() throws Exception
    {
        MockHttpServletRequest request = getRequest();
        request.addParameter("returnedDistanceUnit", "clicks");
        try
        {
            searchNearestAction.createObject(request);
            fail("No IllegalRequestException thrown");
        }
        catch (IllegalRequestException irx)
        {
            // expected
        }
    }
    
    @Test
    public void testCreateObjectNoReturnedDistanceUnit() throws Exception
    {
        MockHttpServletRequest request = getRequest();
        searchNearestAction.createObject(request);
        SearchNearestParams params = capture.getValue();
        assertEquals(DistanceUnit.METER, params.getReturnedDistanceUnit()); 
    }
    
    @Test
    public void testCreateObjectMaxResults() throws Exception
    {
        MockHttpServletRequest request = getRequest();
        searchNearestAction.createObject(request);
        SearchNearestParams params = capture.getValue();
        assertEquals(123, params.getMaxResults()); 
    }
    
    private void programRequest()
    {
        MockHttpServletRequest request = getRequest();
        request.addParameter("x", "1");
        request.addParameter("y", "2");
        request.addParameter("sourceSrs", "testSrs");
        request.addParameter("tableName", "testTable");
        request.addParameter("includeGeometry", "false");
        request.addParameter("targetSrs", "testSrs");
        request.addParameter("maxResults", "123");
        request.addParameter("maxDistance", "0");
    }

}
