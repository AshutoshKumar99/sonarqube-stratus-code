package com.pb.stratus.controller.featuresearch;

import com.mapinfo.midev.service.feature.ws.v1.ServiceException;
import com.pb.stratus.controller.service.SearchByQueryParams;
import org.junit.Before;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: VI012GU
 * Date: 10/16/14
 * Time: 3:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class FeatureCountByGeometryQueryTest {

    private SearchByQueryParams mockParams;
    private String TABLE_NAME = "Test_Table";
    private FeatureCountByGeometryQuery featureCountByGeometryQuery;

    @Before
    public void setUp() throws ServiceException {
        mockParams = new SearchByQueryParams();
        mockParams.setTableName(TABLE_NAME);
        featureCountByGeometryQuery = new FeatureCountByGeometryQuery();
    }

    @Test
    public void testCreateSQLQuery() {
        String expectedResult = "SELECT " +
                "MI_GeometryType(Obj) as MI_FEATURETYPE, " +
                "COUNT(*) as FTR_COUNT " +
                "FROM \"Test_Table\" " +
                "GROUP BY MI_GeometryType(Obj)";

        String query = featureCountByGeometryQuery.createSQLQuery(null, mockParams, true, null, null);
        assert(expectedResult.equals(query));
    }
}
