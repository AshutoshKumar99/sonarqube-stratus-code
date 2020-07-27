package com.pb.stratus.controller.action;

import com.pb.stratus.core.configuration.ConfigReader;
import com.pb.stratus.core.configuration.ControllerConfiguration;
import com.pb.stratus.security.core.util.AuthorizationUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class CalloutInfoTemplateActionTest extends ControllerActionTestBase
{
    
    private CalloutInfoTemplateAction action;
    private ControllerConfiguration config;
    private AuthorizationUtils authorizationUtils;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @Before
    public void setUpAction() throws Exception
    {
        ConfigReader mockConfigReader = mock(ConfigReader.class);
        config = mock(ControllerConfiguration.class);
        authorizationUtils = mock(AuthorizationUtils.class);
        request = new MockHttpServletRequest();
        request.setParameter("file","CalloutInfoTemplateDefinitions");
        request.setParameter("type","infotemplates");
        request.setParameter("mapconfig","Default Map");
        request.setParameter("tables","/analyst/NamedTables/Upper_Assam_AC_2011,/QA-Maps/NamedTables/OpenSpaces");
        response = new MockHttpServletResponse();
        action = new CalloutInfoTemplateAction(mockConfigReader, config, authorizationUtils);
    }
    
    @Test
    public void shouldBeABaseConfigProviderAction()
    {
        assertTrue(action instanceof BaseConfigProviderAction);
    }
    
    @Test
    public void shouldSetMimeTypeToApplicationXml()
    {
        assertEquals("application/xml", action.getMimeType());
    }
/*    @Test
    public void testExecute(){
        try {
            action.execute(request,response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    
}
