package com.pb.stratus.controller.featuresearch;

import com.mapinfo.midev.service.feature.ws.v1.ServiceException;
import com.pb.stratus.controller.service.SearchByQueryParams;
import org.junit.Before;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: VI021CH
 * Date: 12/11/14
 * Time: 2:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class RecordCountQueryTest {

    private SearchByQueryParams mockParams;
    private String TABLE_NAME = "Test_Table";
    private String COLUMN_NAME= "Test_Column";
    private RecordCountQuery recordCountQuery;

    @Before
    public void setUp() throws ServiceException {
        mockParams = new SearchByQueryParams();
        mockParams.setTableName(TABLE_NAME);
        mockParams.setColumnName(COLUMN_NAME);
        recordCountQuery = new RecordCountQuery();
    }

    @Test
    public void testCreateSQLQuery() {
        String expectedResult = "SELECT " +
                "\"Test_Column\"as FTR_VALUE," +
                "COUNT(*) as FTR_COUNT " +
                "FROM \"Test_Table\" " +
                "GROUP BY \"Test_Column\"";

        String query = recordCountQuery.createSQLQuery(null, mockParams, true, null, null);
        assert(expectedResult.equals(query));
    }
}
