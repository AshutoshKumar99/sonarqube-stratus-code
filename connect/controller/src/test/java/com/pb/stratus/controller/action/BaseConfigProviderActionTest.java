package com.pb.stratus.controller.action;

import com.pb.stratus.core.configuration.ConfigFileType;
import com.pb.stratus.core.configuration.ConfigReader;
import com.pb.stratus.core.configuration.ControllerConfiguration;
import com.pb.stratus.security.core.util.AuthorizationUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class BaseConfigProviderActionTest extends ControllerActionTestBase
{

    private BaseConfigProviderAction action;
    private AuthorizationUtils mockAuthorizationUtils;

    private byte[] mockContent;

    private ConfigReader mockConfigReader;
    private Authentication authentication;
    private SecurityContext m_context;
    private ControllerConfiguration config;
    private final String xmlIn = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><map-config></map-config>";
    private final String xmlOutOne = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><map-config><bing-key>publicKey</bing-key><max-features>100</max-features></map-config>";
    private final String xmlOutTwo = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><map-config><bing-key>privateKey</bing-key><max-features>100</max-features></map-config>";
    private final String jsonOutOne = "/*{\"bing-key\":\"publicKey\",\"max-features\":\"100\"}*/";
    private final String jsonOutTwo = "/*{\"bing-key\":\"privateKey\",\"max-features\":\"100\"}*/";

    @Before
    public void setUpAction() throws Exception
    {
        mockConfigReader = mock(ConfigReader.class);
        mockAuthorizationUtils = mock(AuthorizationUtils.class);
        mockContent = xmlIn.getBytes();

        InputStream is = new ByteArrayInputStream(mockContent);
        when(mockConfigReader.getConfigFile(any(String.class))).thenReturn(is);
        when(
            mockConfigReader.getConfigFile(any(String.class),
                any(ConfigFileType.class))).thenReturn(is);
        config = mock(ControllerConfiguration.class);
        when(config.getBingServicesPrivateApiKey()).thenReturn("privateKey");
        when(config.getBingServicesPublicApiKey()).thenReturn("publicKey");
        when(config.getMaxFeatureSearchResults()).thenReturn(100);
        action = new BaseConfigProviderAction(mockConfigReader, config, mockAuthorizationUtils)
        {
            @Override
            protected String getMimeType()
            {
                return "someMimeType";
            }
        };
        // XXX this relies on super.setUp() being executed before setUpAction
        getRequest().addParameter("file", "someFile");
        getRequest().addParameter("type", "map");

        m_context = mock(SecurityContext.class);
        SecurityContextHolder.setContext(m_context);

    }

    @Test
    public void shouldSetCorrectMimeType() throws Exception
    {
        String headerResponseFormat = getRequest().getHeader("RESPONSE-FORMAT");
        action.execute(getRequest(), getResponse());
        if (headerResponseFormat != null && "application/xml".equals(headerResponseFormat))
        {   //xml
            assertEquals("application/xml", getResponse().getContentType());
        }
        else
        {   //json object
            assertEquals("text/json-comment-filtered", getResponse().getContentType());
        }
    }

    @Test
    public void shouldCopyConfigFileToResponse() throws Exception
    {
        when(mockAuthorizationUtils.isAnonymousUser()).thenReturn(true);

        //test for JSON object with no header
        String headerResponseFormat = getRequest().getHeader("RESPONSE-FORMAT");
        action.execute(getRequest(), getResponse());
        if (headerResponseFormat != null && "application/xml".equals(headerResponseFormat))
        {   //xml
            assertArrayEquals(xmlOutOne.getBytes(), getResponse().getContentAsByteArray());
        }
        else
        {   //json object
            assertArrayEquals(jsonOutOne.getBytes(), getResponse().getContentAsByteArray());
        }
    }

    @Test
    public void shouldCopyConfigFileToResponse_XML() throws Exception
    {
        when(mockAuthorizationUtils.isAnonymousUser()).thenReturn(true);

        //test for XML
        getRequest().addHeader("RESPONSE-FORMAT", "application/xml");
        String headerResponseFormat = getRequest().getHeader("RESPONSE-FORMAT");
        action.execute(getRequest(), getResponse());
        if (headerResponseFormat != null && "application/xml".equals(headerResponseFormat))
        {   //xml
            assertArrayEquals(xmlOutOne.getBytes(), getResponse().getContentAsByteArray());
        }
    }

    @Test
    public void shouldCopyConfigFileToResponse_JSONObject() throws Exception
    {
        when(mockAuthorizationUtils.isAnonymousUser()).thenReturn(true);

        //test for JSON object with json header
        getRequest().addHeader("RESPONSE-FORMAT", "application/json");
        String headerResponseFormat = getRequest().getHeader("RESPONSE-FORMAT");
        action.execute(getRequest(), getResponse());
        if (headerResponseFormat != null && "application/xml".equals(headerResponseFormat))
        {   //xml
            assertArrayEquals(xmlOutOne.getBytes(), getResponse().getContentAsByteArray());
        }
        else
        {   //json object
            assertArrayEquals(jsonOutOne.getBytes(), getResponse().getContentAsByteArray());
        }
    }

    @Test
    public void shouldCopyConfigFileToResponse_InvalidHeader() throws Exception
    {
        when(mockAuthorizationUtils.isAnonymousUser()).thenReturn(true);

        //test for JSON object with json header
        getRequest().addHeader("RESPONSE-FORMAT", "-");
        String headerResponseFormat = getRequest().getHeader("RESPONSE-FORMAT");
        action.execute(getRequest(), getResponse());
        if (headerResponseFormat != null && "application/xml".equals(headerResponseFormat))
        {   //xml
            assertArrayEquals(xmlOutOne.getBytes(), getResponse().getContentAsByteArray());
        }
        else
        {   //json object
            assertArrayEquals(jsonOutOne.getBytes(), getResponse().getContentAsByteArray());
        }
    }

    // Anonymous user
    private Authentication createAnonymousToken()
    {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new GrantedAuthorityImpl("ROLE_ANONYMOUS"));
        return new AnonymousAuthenticationToken("stratus", "Guest", authorities);
    }

   @Test
    public void shouldGetExpectedConfigFile() throws Exception
    {
        action.execute(getRequest(), getResponse());
        verify(mockConfigReader).getConfigFile("someFile", ConfigFileType.MAP);
    }

    @Test
    public void shouldReturnPrivateKey() throws Exception
    {
        //test for JSON object with no header
        String headerResponseFormat = getRequest().getHeader("RESPONSE-FORMAT");
        action.execute(getRequest(), getResponse());
        if (headerResponseFormat != null && "application/xml".equals(headerResponseFormat))
        {   //xml
            assertArrayEquals(xmlOutTwo.getBytes(), getResponse().getContentAsByteArray());
        }
        else
        {   //json object
            assertArrayEquals(jsonOutTwo.getBytes(), getResponse().getContentAsByteArray());
        }
    }

    @Test
    public void shouldReturnPrivateKey_XML() throws Exception
    {
        //test for XML
        getRequest().addHeader("RESPONSE-FORMAT", "application/xml");
        String headerResponseFormat = getRequest().getHeader("RESPONSE-FORMAT");
        action.execute(getRequest(), getResponse());
        if (headerResponseFormat != null && "application/xml".equals(headerResponseFormat))
        {   //XML
            assertArrayEquals(xmlOutTwo.getBytes(), getResponse().getContentAsByteArray());
        }
    }

    @Test
    public void shouldReturnPrivateKey_JSONObject() throws Exception
    {
        //test for JSON object with json header
        getRequest().addHeader("RESPONSE-FORMAT", "application/json");
        String headerResponseFormat = getRequest().getHeader("RESPONSE-FORMAT");
        action.execute(getRequest(), getResponse());
        if (headerResponseFormat != null && "application/xml".equals(headerResponseFormat))
        {   //xml
            assertArrayEquals(xmlOutTwo.getBytes(), getResponse().getContentAsByteArray());
        }
        else
        {   //json object
            assertArrayEquals(jsonOutTwo.getBytes(), getResponse().getContentAsByteArray());
        }
    }

    @Test
    public void shouldReturnPrivateKey_InvalidHeader() throws Exception
    {
        //test for JSON object with json header
        getRequest().addHeader("RESPONSE-FORMAT", "-");
        String headerResponseFormat = getRequest().getHeader("RESPONSE-FORMAT");
        action.execute(getRequest(), getResponse());
        if (headerResponseFormat != null && "application/xml".equals(headerResponseFormat))
        {   //xml
            assertArrayEquals(xmlOutTwo.getBytes(), getResponse().getContentAsByteArray());
        }
        else
        {   //json object
            assertArrayEquals(jsonOutTwo.getBytes(), getResponse().getContentAsByteArray());
        }
    }

    @Test
    public void shouldSetCorrectContentType_XML() throws Exception
    {
        getRequest().addHeader("RESPONSE-FORMAT", "application/xml");
        String headerResponseFormat = getRequest().getHeader("RESPONSE-FORMAT");
        action.execute(getRequest(), getResponse());
        if (headerResponseFormat != null && "application/xml".equals(headerResponseFormat))
        {   //XML
            assertEquals("application/xml", getResponse().getContentType());
        }
    }

    @Test
    public void shouldSetCorrectContentType_JSON() throws Exception
    {
        getRequest().addHeader("RESPONSE-FORMAT", "application/json");
        String headerResponseFormat = getRequest().getHeader("RESPONSE-FORMAT");
        action.execute(getRequest(), getResponse());
        if (headerResponseFormat != null && "application/xml".equals(headerResponseFormat))
        {   //xml
            assertEquals("application/xml", getResponse().getContentType());
        }
        else
        {   //json object
            assertEquals("text/json-comment-filtered", getResponse().getContentType());
        }
    }

}
