package com.pb.stratus.controller.featuresearch;

import com.mapinfo.midev.service.feature.ws.v1.ServiceException;
import com.pb.stratus.controller.service.SearchByQueryParams;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: ar009sh
 * Date: 14/1/15
 * Time: 1:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataBindingQueryTest {


    private SearchByQueryParams mockParams;
    private String TABLE_NAME = "Test_Table";
    private String BIND_COLUMN= "Test_Column";
    private DataBindingQuery sqlQuery;


    @Before
    public void setUp() throws ServiceException {
        mockParams = new SearchByQueryParams();
        mockParams.setTableName(TABLE_NAME);
        mockParams.setColumnName(BIND_COLUMN);
        mockParams.setParams("'132','133'");
        sqlQuery = new DataBindingQuery();
    }

    @Test
    public void testDataBindingSQLQueryNotNull() throws Exception {
        String query = sqlQuery.createSQLQuery(null, mockParams, true, null,null);
        Assert.assertNotNull(query);
    }

    @Test
    public void testDataBindingSQLQuery() throws Exception {
        String expectedQuery = "SELECT * FROM \"Test_Table\" WHERE Test_Column IN ('132','133')";
        String query = sqlQuery.createSQLQuery(null, mockParams, true, null,null);
        assert(expectedQuery.equals(query));
      }
 }
