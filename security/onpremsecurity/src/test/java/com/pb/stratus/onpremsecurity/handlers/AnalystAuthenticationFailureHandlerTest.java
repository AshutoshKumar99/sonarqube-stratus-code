package com.pb.stratus.onpremsecurity.handlers;

import com.pb.stratus.core.configuration.ControllerConfiguration;
import com.pb.stratus.core.configuration.TenantConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 3/23/14
 * Time: 3:53 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(TenantConfiguration.class)
public class AnalystAuthenticationFailureHandlerTest {

    private AnalystAuthenticationFailureHandler target;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private AuthenticationException mockException;
    private ControllerConfiguration mockTenantConfig;
    //private Class mockClazz = mock(Class.class);
    private final String SSO_URL = "http://some/sso/url";
    private final String SSO_URL_WITH_PARAM = "http://some/sso/url?param=value";

    @Before
    public void setup(){
        target = new AnalystAuthenticationFailureHandler();
        Map<String,String> errorTypeMap = new HashMap<>();
        errorTypeMap.put("BadCredentialsException", "1");
        errorTypeMap.put("AuthenticationServiceException", "2");
        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
        mockTenantConfig = mock(ControllerConfiguration.class);

       // when(mockException.getClass()).thenReturn(mockClazz);
        target.setErrorParam("login_error");
    }

    @Test
    public void testRedirectionAuthenticationFailure() throws IOException, ServletException {

      /*  Need to check hoe to mock getClass method.
      when(mockTenantConfig.getSsoStartUrl()).thenReturn(SSO_URL);
        mockStatic(TenantConfiguration.class);
        when(TenantConfiguration.getTenantConfiguration(mockRequest)).thenReturn(mockTenantConfig);
        mockException = PowerMockito.mock(AuthenticationException.class);
        MockGateway.MOCK_GET_CLASS_METHOD = true;
        final BadCredentialsException classBadCredEx = spy(new BadCredentialsException(""));
        when(mockException.getClass()).thenReturn(classBadCredEx);
        target.onAuthenticationFailure(mockRequest, mockResponse, mockException);
        MockGateway.MOCK_GET_CLASS_METHOD = false;
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(mockResponse).sendRedirect(argument.capture());
        assertEquals(SSO_URL + "?login_error=1", argument.getValue());*/


    }

    @Test
    public void testRedirectionIfSsoUrlHaveParam() throws IOException, ServletException {
       /* when(mockTenantConfig.getSsoStartUrl()).thenReturn(SSO_URL_WITH_PARAM);
        mockStatic(TenantConfiguration.class);
        when(TenantConfiguration.getTenantConfiguration(mockRequest)).thenReturn(mockTenantConfig);
        target.onAuthenticationFailure(mockRequest, mockResponse, mockException);
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(mockResponse).sendRedirect(argument.capture());
        assertEquals(SSO_URL_WITH_PARAM + "&login_error=2", argument.getValue());*/
    }

}
