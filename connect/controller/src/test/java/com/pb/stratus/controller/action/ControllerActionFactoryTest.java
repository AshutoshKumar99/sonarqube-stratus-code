package com.pb.stratus.controller.action;

import com.pb.stratus.core.configuration.ControllerConfiguration;
import org.easymock.Capture;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class ControllerActionFactoryTest
{

    private ControllerConfiguration config;
    
    private ControllerActionFactory actionFactory;
    
    @Before
    public void setUp()
    {
        config = createMock(ControllerConfiguration.class);
        actionFactory = new ControllerActionFactory(config);
    }

    @Test
    public void testMapControllerAction()
    {
        ControllerAction mockAction = createMockControllerAction();
        replay(mockAction);
        actionFactory.mapControllerAction(mockAction, "/some/path");
        verify(mockAction);
    }
    
    @Test
    public void testGetControllerActionUnknownPath()
    {
        ControllerAction action = actionFactory.getControllerAction("/xyz");
        assertTrue(action instanceof NotFoundControllerAction);
    }

    
    @Test
    public void testGetControllerActionMatches()
    {
        ControllerAction mockAction = createMockControllerAction();
        Capture<String> actualPath = new Capture<String>();
        expect(mockAction.matches(capture(actualPath))).andReturn(true);
        replay(mockAction);
        actionFactory.mapControllerAction(mockAction, "/some/path");
        ControllerAction ca = actionFactory.getControllerAction("/some/path");
        assertEquals(mockAction, ca);
        assertEquals("/some/path", actualPath.getValue());
    }
    
    @Test
    public void testGetControllerActionNoMatch()
    {
        ControllerAction mockAction = createMockControllerAction();
        expect(mockAction.matches("/some/other/path")).andReturn(false);
        replay(mockAction);
        actionFactory.mapControllerAction(mockAction, "/some/path");
        ControllerAction ca = actionFactory.getControllerAction("/some/other/path");
        assertTrue(ca instanceof NotFoundControllerAction);
    }
    
    private ControllerAction createMockControllerAction()
    {
        ControllerAction action = createMock(ControllerAction.class);
        action.setConfig(config);
        action.setPath("/some/path");
        action.init();
        return action;
    }
    
//    private void assertCorrectType(String path, 
//            Class<? extends ControllerAction> type)
//    {
//        String[] suffixes = new String[] {"", "/asdf", "/asdf/asdf"};
//        for (String suffix : suffixes)
//        {
//            String newPath = path + suffix;
//            ControllerAction a = actionFactory.getControllerAction(newPath);
//            String msg = createWrongTypeMessage(type, a, newPath);
//            assertTrue(msg, a.getClass().isAssignableFrom(type));
//        }
//        String newPath = path + "asdf";
//        ControllerAction a = actionFactory.getControllerAction(newPath);
//        String msg = createWrongTypeMessage(NotFoundControllerAction.class, a, 
//                newPath);
//        assertTrue(msg, a instanceof NotFoundControllerAction);
//    }
//    
//    private String createWrongTypeMessage(Class<? extends ControllerAction> expectedType, 
//            ControllerAction actual, String path)
//    {
//        return "Expected an instance of " + expectedType.getName() 
//        + " from path " + path + " but got " 
//        + actual.getClass().getName(); 
//    }
}
