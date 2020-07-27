package com.pb.stratus.controller.action;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: HA008SA
 * Date: 4/3/14
 * Time: 6:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class BuildRevisionDetailsActionTest {

    private BuildRevisionDetailsAction buildRevisionDetailsAction;
    private MockHttpServletRequest mockRequest;

    @Before
    public void setUp()
    {
        buildRevisionDetailsAction = new BuildRevisionDetailsAction();
    }

    @Test
    public void testCreateObject() throws Exception
    {
        mockRequest = new MockHttpServletRequest();
        Object response = buildRevisionDetailsAction.createObject(mockRequest);
        assertNotNull(response);
        assertTrue(response instanceof String);
    }

}
