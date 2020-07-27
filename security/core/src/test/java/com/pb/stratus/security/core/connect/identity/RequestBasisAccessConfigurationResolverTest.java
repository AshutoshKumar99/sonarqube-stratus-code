package com.pb.stratus.security.core.connect.identity;

//import com.pb.stratus.controller.application.Application;
//import com.pb.stratus.controller.application.ApplicationStartupFilter;
//import com.pb.stratus.controller.configuration.TenantProfile;
//import com.pb.stratus.controller.configuration.TenantProfileManager;
import com.pb.spectrum.platform.server.common.security.token.TokenInfo;
import com.pb.spectrum.platform.server.common.security.token.TokenManagerService;
import com.pb.stratus.core.common.Constants;
import com.pb.stratus.core.configuration.ControllerConfiguration;
import com.pb.stratus.core.configuration.SystemPropertyCustomerConfigDirHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.File;

import static com.pb.stratus.core.configuration.SystemPropertyCustomerConfigDirHolder.DIR_PROPERTY_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.powermock.api.mockito.PowerMockito.*;

//@RunWith(PowerMockRunner.class)
//@PrepareForTest({System.class})
public class RequestBasisAccessConfigurationResolverTest
{

    private HttpServletRequest m_request;
    private String m_tenant = "customerstratustenant1_noida";
    private String m_auth_type = "secured";
    private RequestBasisAccessConfigurationResolver target = new RequestBasisAccessConfigurationResolver();
    private ControllerConfiguration m_config;

    @After
    public void tearDown() {
        System.getProperties().remove(DIR_PROPERTY_NAME);
    }

    @Test
    public void shouldGetCustomerConfigDirFromSystemProperty() throws Exception
    {
        File f = File.createTempFile("test", ".txt");
        f.delete();
        File dir = f.getParentFile();
        System.setProperty(DIR_PROPERTY_NAME, dir.getCanonicalPath());
        SystemPropertyCustomerConfigDirHolder dirHolder
                = new SystemPropertyCustomerConfigDirHolder();
        assertEquals(dir.getCanonicalPath(),
                dirHolder.getCustomerConfigDir().toString());
    }

//    @Test
//    public void testGetAnonymousUserName() {
//        //mockSystem();
//        System.setProperty(DIR_PROPERTY_NAME, "/some/non/existent/dir");
//        when(m_config.getAnonymousUserName()).thenReturn("customerstratustenant1_noida guest");
//        assertEquals(target.getAnonymousUserName(), expected_amonymous_username);
//    }
//
//    @Test
//    public void testGetAnonymousPassword() {
//        //mockSystem();
//        System.setProperty(DIR_PROPERTY_NAME, "/some/non/existent/dir");
//        when(m_config.getAnonymousPassword()).thenReturn(expected_amonymous_password);
//        assertEquals(target.getAnonymousPassword(), expected_amonymous_password);
//    }
//
//    @Test
//    public void testIsAnonymousLoginAllowed() {
//        //mockSystem();
//        System.setProperty(DIR_PROPERTY_NAME, "/some/non/existent/dir");
//        assertEquals(target.isAnonymousLoginAllowed(),false);
//    }
//
//    @Test
//    public void testOnlyAdmin() {
//        //mockSystem();
//        System.setProperty(DIR_PROPERTY_NAME, "/some/non/existent/dir");
//        when(m_config.getAuthType()).thenReturn("public");
//        assertEquals(target.onlyAdminAccessAllowed(),true);
//    }
}
