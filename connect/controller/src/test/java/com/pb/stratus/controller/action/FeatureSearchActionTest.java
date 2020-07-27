package com.pb.stratus.controller.action;

import com.pb.stratus.controller.service.SearchParams;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class FeatureSearchActionTest extends ControllerActionTestBase
{

    private FeatureSearchAction action;

    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        action = new FeatureSearchAction(null)
        {
            @Override
            protected Object createObject(HttpServletRequest request)
                    throws ServletException, IOException
            {
                return null;
            }
        };
    }

    @Test
    public void shouldPopulateAttributesFromAttributeFieldsRequestParameter()
    {
        SearchParams params = new SearchParams();
        getRequest().addParameter("attributes", "attr1");
        getRequest().addParameter("attributes", "attr2");
        getRequest().addParameter("attributes", "attr3");
        action.populateParams(params, getRequest());
        assertEquals(Arrays.asList("attr1", "attr2", "attr3"),
                params.getAttributes());
    }

    @Test
    public void shouldPopulateOrderByFromOrderByRequestParameter()
    {
        SearchParams params = new SearchParams();
        getRequest().addParameter("orderByColumns", "col1");
        getRequest().addParameter("orderByColumns", "col2");
        getRequest().addParameter("orderByColumns", "col3");
        action.populateParams(params, getRequest());
        assertEquals(Arrays.asList("col1", "col2", "col3"),
                params.getOrderByList());
    }

    @Test
    public void shouldPopulateNegativeOneForWrongPageParametersInRequest()
    {
        SearchParams params = new SearchParams();
        getRequest().addParameter("pageLength", "a25");
        getRequest().addParameter("pageNumber", "a1");
        action.populateParams(params, getRequest());
        assertEquals(-1, params.getPageLength());
        assertEquals(-1, params.getPageNumber());
    }

}
