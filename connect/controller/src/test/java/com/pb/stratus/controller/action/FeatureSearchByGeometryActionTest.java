package com.pb.stratus.controller.action;

import com.pb.stratus.controller.featuresearch.FeatureService;
import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.json.geojson.GeoJsonParser;
import com.pb.stratus.controller.json.geojson.GeometryTestUtil;
import com.pb.stratus.controller.service.SearchWithinGeometryParams;
import com.pb.stratus.controller.service.SearchWithinGeometryParams.SpatialOperation;
import org.easymock.Capture;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FeatureSearchByGeometryActionTest extends
        FeatureSearchActionTestBase
{
    String srsName;
    GeoJsonParser parser;
    MockHttpServletRequest request;
    FeatureSearchByGeometryAction action;

    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        srsName = "epsg:27700";
        parser = new GeoJsonParser(srsName);
        request = getRequest();
    }

    @Test
    public void testCreateObject() throws Exception
    {
        request.addParameter("tableName", "testTable");
        request.addParameter("spatialOperation", "ENTIRELYWITHIN");
        request.addParameter("includeGeometry", "true");
        request.addParameter("sourceSrs", srsName);
        request.addParameter("targetSrs", "someTargetSrs");
        String polygon = "{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100,100],[100,200],[200,200],[200,100],[100,100]]]]}";
        request.addParameter("polygon", polygon);

        FeatureService mockFeatureService = createMock(FeatureService.class);
        FeatureSearchResult expectedResult = createDummyResult();

        Capture<SearchWithinGeometryParams> capture = new Capture<SearchWithinGeometryParams>();
        expect(mockFeatureService.searchByGeometry(capture(capture))).andReturn(
                expectedResult);

        replay(mockFeatureService);
        action = new FeatureSearchByGeometryAction(mockFeatureService, null);
        Object actualResult = action.createObject(request);

        SearchWithinGeometryParams actualParams = capture.getValue();
		actualParams.setGeometry(parser.parseGeometry(polygon));

        assertEquals("testTable", actualParams.getTable());
        assertTrue(actualParams.isIncludeGeometry());
        assertEquals(SpatialOperation.ENTIRELYWITHIN, actualParams
                .getSpatialOperation());
        GeometryTestUtil.assertGeometry(parser.parseGeometry(polygon),
                actualParams.getGeometry());
        assertEquals(srsName, actualParams.getSourceSrs());
        assertEquals("someTargetSrs", actualParams.getTargetSrs());
        assertEquals(expectedResult, actualResult);
    }
}
