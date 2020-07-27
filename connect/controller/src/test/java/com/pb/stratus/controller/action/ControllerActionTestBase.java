package com.pb.stratus.controller.action;

import org.junit.Before;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class ControllerActionTestBase
{
    private MockHttpServletRequest request;
    
    private MockHttpServletResponse response;
    
    @Before
    public void setUp() throws Exception
    {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }
    
    protected MockHttpServletRequest getRequest()
    {
        return request;
    }
    
    protected MockHttpServletResponse getResponse()
    {
        return response;
    }
    
    

}
