package com.pb.stratus.controller.action;

import com.pb.stratus.controller.featuresearch.FeatureService;
import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.service.SearchAtPointParams;
import org.easymock.Capture;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Arrays;

import static com.pb.stratus.controller.action.FeatureSearchAction.*;
import static com.pb.stratus.controller.action.FeatureSearchAtPointAction.TABLE_NAMES_PARAM;
import static com.pb.stratus.controller.action.FeatureSearchAtPointAction.WIDTH_PARAM;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FeatureSearchAtPointActionTest extends FeatureSearchActionTestBase 
{
    
    @Test
    public void testExecute() throws Exception
    {
        String[] expectedTables = new String[] {"table1, table2"};
        String[] expectedProps = new String[] {"prop1", "prop2"};
        MockHttpServletRequest request = getRequest();
        request.addParameter(X_PARAM, "1");
        request.addParameter(Y_PARAM, "2");
        request.addParameter(TABLE_NAMES_PARAM, expectedTables);
        request.addParameter(PROPERTIES_PARAM, expectedProps);
        request.addParameter(INCLUDE_GEOMETRY_PARAM, "true");
        request.addParameter(SOURCE_SRS_PARAM, "someSourceSrs");
        request.addParameter(TARGET_SRS_PARAM, "someTargetSrs");
        request.addParameter(WIDTH_PARAM, new String[]{"123"});
        request.addParameter("viewTableSQL", new String[]{"NA"});
        
        FeatureService mockFeatureService = createMock(FeatureService.class);
        
        FeatureSearchResult expectedResult = createDummyResult();
        Capture<SearchAtPointParams> capture = new Capture<SearchAtPointParams>();
        expect(mockFeatureService.searchAtPoint(capture(capture)))
                .andReturn(expectedResult);
        replay(mockFeatureService);
        FeatureSearchAction action = new FeatureSearchAtPointAction(
                mockFeatureService);
        Object actualResult = action.createObject(request);
        SearchAtPointParams actualParams = capture.getValue();
        assertTrue(actualParams.getTables().containsAll(
                Arrays.asList(expectedTables)));
        assertTrue(actualParams.getAttributes().containsAll(
                Arrays.asList(expectedProps)));
        assertEquals(1, actualParams.getPoint().getPos().getX(), 0d);
        assertEquals(2, actualParams.getPoint().getPos().getY(), 0d);
        assertTrue(actualParams.isIncludeGeometry());
        assertEquals("someSourceSrs", actualParams.getPoint().getSrsName());
        assertEquals("someTargetSrs", actualParams.getTargetSrs());
        assertEquals(expectedResult, actualResult);
    }

}
