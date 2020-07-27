/**
 * CustomQueryMetadataFactoryTest
 * User: GU003DU
 * Date: 7/1/14
 * Time: 2:54 PM
 */

package com.pb.stratus.controller.service;

import com.pb.stratus.controller.Constants;
import com.pb.stratus.controller.queryutils.CriteriaParams;
import com.pb.stratus.controller.queryutils.QueryMetadata;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class CustomQueryMetadataFactoryTest {

    private static final String TABLE = "table";
    private static final String COLUMN_NAME = "Name";
    private static final String COLUMN_TYPE = "String";
    private static final String COLUMN_VAL = "test";
    private static final String OPERATOR = "=";

    @Test
    public void testCreateQueryMetadata() throws Exception {
        SearchByQueryParams searchParams = mock(SearchByQueryParams.class);

        Map<String, Object> val = new HashMap<>();
        val.put(Constants.QUERY_COLUMN_NAME, COLUMN_NAME);
        val.put(Constants.QUERY_COLUMN_TYPE, COLUMN_TYPE);
        val.put(Constants.QUERY_COLUMN_VAL, COLUMN_VAL);
        val.put(Constants.QUERY_OPERATOR, OPERATOR);

        List<Map<String, Object>> customQueryParams = new ArrayList<>();
        customQueryParams.add(val);

        when(searchParams.getCustomQueryParams()).thenReturn(customQueryParams);
        when(searchParams.getQueryName()).thenReturn(Constants.CUSTOM_FILTER);
        when(searchParams.getTableName()).thenReturn(TABLE);

        CustomQueryMetadataFactory factory = new CustomQueryMetadataFactory();
        QueryMetadata metadata = factory.createQueryMetadata(searchParams);

        assertEquals(metadata.getQueryName(), Constants.CUSTOM_FILTER);
        assertEquals(metadata.getTableName(), TABLE);
        assertEquals(metadata.getCriteriaParams().size(), 1);

        CriteriaParams params = metadata.getCriteriaParams().get(0);
        assertEquals(params.getColumnName(), COLUMN_NAME);
        assertEquals(params.getColumnType(), COLUMN_TYPE);
        assertEquals(params.getValues().get(0), COLUMN_VAL);
        assertEquals(params.getOperator(), OPERATOR);
    }
}
