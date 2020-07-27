package com.pb.stratus.controller.action;

import com.pb.stratus.controller.featuresearch.FeatureService;
import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.service.SearchByExpressionParams;
import org.easymock.Capture;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FeatureSearchByExpressionActionTest extends FeatureSearchActionTestBase 
{
    @Test
    public void testCreateObject() throws Exception
    {
        MockHttpServletRequest request = getRequest();
        request.addParameter("tableName", "testTable");
        request.addParameter("searchExpression", "searchExpression");
        request.addParameter("includeGeometry", "true");
        request.addParameter("targetSrs", "someTargetSrs");
        
        FeatureService mockFeatureService = createMock(FeatureService.class);

        FeatureSearchResult expectedResult = createDummyResult();

        Capture<SearchByExpressionParams> capture = new Capture<SearchByExpressionParams>();
        expect(mockFeatureService.searchByExpression(capture(capture)))
                .andReturn(expectedResult);
        replay(mockFeatureService);
        FeatureSearchByExpressionAction action 
                = new FeatureSearchByExpressionAction(mockFeatureService);
        Object actualResult = action.createObject(request);
        SearchByExpressionParams actualParams = capture.getValue();
        assertEquals("testTable", actualParams.getTable());
        assertTrue(actualParams.isIncludeGeometry());
        assertEquals("searchExpression", actualParams.getExpression());
        assertEquals("someTargetSrs", actualParams.getTargetSrs());
        assertEquals(expectedResult, actualResult);
    }

}
