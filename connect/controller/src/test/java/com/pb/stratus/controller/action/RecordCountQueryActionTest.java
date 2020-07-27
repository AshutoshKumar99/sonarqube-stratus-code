package com.pb.stratus.controller.action;

import com.pb.stratus.controller.QueryType;
import com.pb.stratus.controller.featuresearch.FeatureService;
import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.service.SearchByQueryParams;
import org.easymock.Capture;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: VI021CH
 * Date: 12/9/14
 * Time: 5:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class RecordCountQueryActionTest  extends FeatureSearchActionTestBase {

    MockHttpServletRequest request;
    RecordCountQueryAction action;
    private String TABLE_NAME = "testTable";
    private String COLUMN_NAME = "testColumn";

    private FeatureService mockFeatureService;
    private FeatureSearchResult expectedResult;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        request = getRequest();
        request.addParameter("tableName", TABLE_NAME);
        request.addParameter("columnName", COLUMN_NAME);
        mockFeatureService = createMock(FeatureService.class);
        expectedResult = createDummyResult();
    }

    @Test
    public void testCreateObject() throws Exception {

        Capture<SearchByQueryParams> searchByQueryParamsCapture = new Capture<>();
        expect(mockFeatureService.searchByQuery(capture(searchByQueryParamsCapture)))
                .andReturn(expectedResult);
        replay(mockFeatureService);

        action = new RecordCountQueryAction(mockFeatureService);
        Object actualResult = action.createObject(request);
        SearchByQueryParams actualParams = searchByQueryParamsCapture.getValue();

        assertEquals("testTable", actualParams.getTableName());
        assertEquals("testColumn", actualParams.getColumnName());
        assertEquals(QueryType.RecordCountQuery, actualParams.getQueryType());
        assertEquals(expectedResult, actualResult);
    }
}

