package com.pb.stratus.controller.action;

import com.pb.stratus.controller.catalog.CatalogService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OverlaysNamedTablesActionTest extends ControllerActionTestBase
{
    private OverlaysNamedTablesAction action;
    
    private CatalogService mockCatalogService;

    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        mockCatalogService = mock(CatalogService.class);
        action = new OverlaysNamedTablesAction(mockCatalogService);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateObject() throws Exception
    {
        MockHttpServletRequest request = getRequest();
        
        request.addParameter("overlays", "overlay1");
        request.addParameter("overlays", "overlay2");
        Map<String, List<String>> mockTableNames = Collections.singletonMap(
                "map1", Arrays.asList("table1", "table2", "table3"));
        when(mockCatalogService.getTableNames(any(Collection.class)))
                .thenReturn(mockTableNames);
        Object actual = action.createObject(request);
        assertEquals(mockTableNames, actual);
    }
}
