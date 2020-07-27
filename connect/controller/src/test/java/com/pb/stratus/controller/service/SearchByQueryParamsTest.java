package com.pb.stratus.controller.service;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.pb.stratus.controller.Constants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SearchByQueryParamsTest {

    SearchByQueryParams object = new SearchByQueryParams();

    @Test
    public void checkParamMapvalue() {
        String params = "{\"0\":[\"Test1\",\"Test2\"],\"1\":[\"28\"]}";
        object.setParams(params);

        Map<String, List<String>> map = object.getIdValueMap();
        assertNotNull(map);
        assertNotNull(map.get("0"));
        assertNotNull(map.get("1"));
        assertEquals("Test1", map.get("0").get(0));
        assertEquals("Test2", map.get("0").get(1));
        assertEquals("28", map.get("1").get(0));
    }

    /**
     *  This tests verifies that 'params' request parameter is parsed correctly for
     *  Custom query.
     */
    @Test
    public void verifyCustomQueryParamsParsing() {
        final int maxSize = 5;
        JSONArray params = new JSONArray();
        for (int i = 1; i <= maxSize; i++) {
            JSONObject condition = new JSONObject();
            condition.put(QUERY_COLUMN_NAME, "Name" + i);
            condition.put(QUERY_COLUMN_TYPE, "String");
            condition.put(QUERY_COLUMN_VAL, "some name" + i);
            condition.put(QUERY_OPERATOR, "=");
            params.add(condition);
        }
        object.setParams(params.toString());
        List<Map<String, Object>> customQueryParams = object.getCustomQueryParams();

        // verification
        assertNotNull(customQueryParams);
        assertEquals(maxSize, customQueryParams.size());
        int j = 1;
        for (Map<String, Object> condition : customQueryParams) {
            assertEquals("Name" + j, condition.get(QUERY_COLUMN_NAME));
            assertEquals("String", condition.get(QUERY_COLUMN_TYPE));
            assertEquals("some name" + j, condition.get(QUERY_COLUMN_VAL));
            assertEquals("=", condition.get(QUERY_OPERATOR));
            j++;
        }
    }
}
