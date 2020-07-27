package com.pb.stratus.controller.action;


import net.sf.json.JSONObject;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.net.MalformedURLException;

import static org.junit.Assert.assertEquals;


/**
 * Created by ar009sh on 13-07-2015.
 */
public class DeploymentIdentificationActionTest extends ControllerActionTestBase {

    private DeploymentIdentificationAction mockAction;
    private MockHttpServletRequest mockRequest;



    @Override
    public void setUp() throws MalformedURLException {
        mockAction = new DeploymentIdentificationAction();
        mockRequest = new MockHttpServletRequest();
    }

    @Test
    public void testAnalystLoadedProfile() throws Exception
    {
        System.setProperty("profile","analyst");
        Object actionReturn = mockAction.createObject(mockRequest);
        JSONObject json = (JSONObject) actionReturn;
        assertEquals("analyst", json.get("profile"));
    }

    @Test
    public void testSaasLoadedProfile() throws Exception
    {
        System.setProperty("profile","saas");
        Object actionReturn = mockAction.createObject(mockRequest);
        JSONObject json = (JSONObject) actionReturn;
        assertEquals("saas", json.get("profile"));
    }



}
