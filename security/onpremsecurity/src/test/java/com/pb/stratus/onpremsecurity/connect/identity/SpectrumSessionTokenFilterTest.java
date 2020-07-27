package com.pb.stratus.onpremsecurity.connect.identity;

import com.pb.stratus.onpremsecurity.Constants;
import com.pb.stratus.onpremsecurity.token.SpectrumSessionTokenHolder;
import com.pb.stratus.onpremsecurity.token.SpectrumToken;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Test the Spectrum Session Token Filter.
 *
 * @author Guru Dutt
 */

public class SpectrumSessionTokenFilterTest {
    private static final String accessToken = "uqewuuuweuywueyuwq";
    private static final String sessionId = "uqewu-u123weuy-wueyuwq";
    private static final String userId = "user1";

    private SpectrumSessionTokenFilter spectrumSessionTokenFilter;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private HttpSession mockSession;
    /**
     * Session's attribute map.
     */
    private Map attributes;

    @Before
    public void setup() {
        spectrumSessionTokenFilter = new SpectrumSessionTokenFilter();
        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
        mockSession = mock(HttpSession.class);
        attributes = new HashMap<>();

        when(mockRequest.getSession(false)).thenReturn(mockSession);

        Mockito.doAnswer(new Answer() {
            /**
             * @see org.mockito.stubbing.Answer#answer(org.mockito.invocation.InvocationOnMock)
             */
            @Override
            public Object answer(InvocationOnMock aInvocation) throws Throwable {
                String key = (String) aInvocation.getArguments()[0];
                Object value = aInvocation.getArguments()[1];
                attributes.put(key, value);
                return null;
            }
        }).when(mockSession).setAttribute(anyString(), anyObject());
    }

    @Test
    public void shouldSaveTokenInformation() throws Exception {
        when(mockRequest.getParameter(Constants.SSO_ACCESS_TOKEN)).thenReturn(accessToken);
        when(mockRequest.getParameter(Constants.SSO_SESSION)).thenReturn(sessionId);
        when(mockRequest.getParameter(Constants.SSO_USER_NAME)).thenReturn(userId);

        FilterChain mockChain = mock(FilterChain.class);
        spectrumSessionTokenFilter.doFilter(mockRequest, mockResponse, mockChain);

        // validations.
        SpectrumSessionTokenHolder spectrumTokenHolder = (SpectrumSessionTokenHolder) attributes.
                get(SpectrumSessionTokenHolder.class.getName());
        SpectrumToken spectrumToken = spectrumTokenHolder.getToken();
        assertEquals(sessionId, spectrumToken.getSession());
        assertEquals(userId, spectrumToken.getUserId());
        assertEquals(accessToken, spectrumToken.getAccessToken());

        verify(mockChain, atLeastOnce()).doFilter(mockRequest, mockResponse);
    }

    @Test
    public void shouldExecuteChainIfThereIsNoToken() throws Exception {
        FilterChain mockChain = mock(FilterChain.class);
        spectrumSessionTokenFilter.doFilter(mockRequest, mockResponse, mockChain);
        assertEquals(true, attributes.isEmpty());
        verify(mockChain, atLeastOnce()).doFilter(mockRequest, mockResponse);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenUserNameMissing() throws Exception {
        when(mockRequest.getParameter(Constants.SSO_ACCESS_TOKEN)).thenReturn(accessToken);
        when(mockRequest.getParameter(Constants.SSO_SESSION)).thenReturn(sessionId);
        FilterChain mockChain = mock(FilterChain.class);
        spectrumSessionTokenFilter.doFilter(mockRequest, mockResponse, mockChain);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenTokenMissing() throws Exception {
        when(mockRequest.getParameter(Constants.SSO_SESSION)).thenReturn(sessionId);
        when(mockRequest.getParameter(Constants.SSO_USER_NAME)).thenReturn(userId);
        FilterChain mockChain = mock(FilterChain.class);
        spectrumSessionTokenFilter.doFilter(mockRequest, mockResponse, mockChain);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenSessionMissing() throws Exception {
        when(mockRequest.getParameter(Constants.SSO_USER_NAME)).thenReturn(userId);
        when(mockRequest.getParameter(Constants.SSO_ACCESS_TOKEN)).thenReturn(accessToken);
        FilterChain mockChain = mock(FilterChain.class);
        spectrumSessionTokenFilter.doFilter(mockRequest, mockResponse, mockChain);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenOnlyUserNameIsPresent() throws Exception {
        when(mockRequest.getParameter(Constants.SSO_USER_NAME)).thenReturn(userId);
        FilterChain mockChain = mock(FilterChain.class);
        spectrumSessionTokenFilter.doFilter(mockRequest, mockResponse, mockChain);
    }
}