package com.pb.stratus.controller.action;

import com.pb.stratus.controller.util.MockSupport;
import com.pb.stratus.core.configuration.ControllerConfiguration;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BaseControllerActionTest 
{
    
    TestAction action;
    
    @Before
    public void setUp()
    {
        action = new TestAction();
    }
    
    @Test
    public void testSetConfig()
    {
        MockSupport mockSupport = new MockSupport();
        ControllerConfiguration config = mockSupport
                .createMock(ControllerConfiguration.class);
        action.setConfig(config);
        assertTrue(config == action.getConfig());
    }
    
    @Test
    public void testMatches()
    {
        action.setPath("/some/path");
        assertTrue(action.matches("/some/path"));
        assertFalse(action.matches("/some/pathology"));
        assertTrue(action.matches("/some/path/to/enlightenment"));
        assertFalse(action.matches(null));
        assertFalse(action.matches("/some"));
    }
    
    @Test
    public void testMatchesEmptyString()
    {
        action.setPath("");
        assertTrue(action.matches(""));
    }
    
    private static class TestAction extends BaseControllerAction
    {
        public void execute(HttpServletRequest request,
                HttpServletResponse response)
        {
        }
        
        public ControllerConfiguration getConfig()
        {
            return super.getConfig();
        }
        
    }

}
