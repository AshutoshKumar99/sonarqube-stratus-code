package com.pb.stratus.controller.customtags;

import com.pb.stratus.controller.application.Application;
import com.pb.stratus.controller.application.ApplicationStartupFilter;
import com.pb.stratus.controller.configuration.TenantProfile;
import com.pb.stratus.controller.configuration.TenantProfileManager;
import com.pb.stratus.controller.print.config.LayerServiceType;
import com.pb.stratus.controller.print.config.MapConfig;
import com.pb.stratus.controller.print.config.MapConfig.ThirdPartyAPIKey;
import com.pb.stratus.controller.print.config.MapConfigRepositoryImpl;
import com.pb.stratus.core.common.Constants;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.util.Collections;

import static org.mockito.Mockito.*;


public class ScriptInserterTest
{
    private ScriptInserter inserter;
    private ServletContext mockServletContext;
    private MapConfigRepositoryImpl mockRepo;
    private TenantProfile mockTenantProfile;
    private PageContext m_pageContext;
    private final String m_tenantName = "stratus";

    @Before
    public void setUp() throws Exception
    {
        m_pageContext = mock(PageContext.class);
        setUpMapConfigRepo();
        setUpTenantProfile();
        setUpServletContext();
        setUpPageContext();
        inserter = new ScriptInserter();
        inserter.setPageContext(m_pageContext);
    }

    private void setUpMapConfigRepo()
    {
        MapConfig mockMapConfig = new MapConfig();

        MapConfig.MapDefinition mockMapDef = mockMapConfig.createMapDefinition();
        mockMapDef.setMapName("test");
        mockMapDef.setService(LayerServiceType.GOOGLE);
        mockMapConfig.setMapDefinitions(Collections.singletonList(mockMapDef));
        mockRepo = mock(MapConfigRepositoryImpl.class);
        when(mockRepo.getMapConfig(Matchers.any(String.class))).thenReturn(
                mockMapConfig);
    }

    private void setUpServletContext()
    {
        Application mockApp = mock(Application.class);
        TenantProfileManager tenantProfileManager = mock(TenantProfileManager.class);

        when(tenantProfileManager.getProfile(Matchers.any(String.class))).thenReturn(mockTenantProfile);
        when(mockApp.getTenantProfileManager()).thenReturn(tenantProfileManager);

        mockServletContext = mock(ServletContext.class);
        when(mockServletContext.getAttribute(Constants.TENANT_NAME))
            .thenReturn(m_tenantName);

        when(mockServletContext.getAttribute(ApplicationStartupFilter.APPLICATION_ATTRIBUTE_NAME))
            .thenReturn(mockApp);
    }

    private void setUpTenantProfile()
    {
        mockTenantProfile = mock(TenantProfile.class);
        when(mockTenantProfile.getMapConfigRepository())
                        .thenReturn(mockRepo);
        when(mockTenantProfile.getTenantName())
                        .thenReturn(m_tenantName);
    }

    private void setUpPageContext()
    {
        JspWriter mockWriter = mock(JspWriter.class);

        when(m_pageContext.getOut()).thenReturn(mockWriter);
        when(m_pageContext.getServletContext()).thenReturn(mockServletContext);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpSession mockSession = mock(HttpSession.class);

        when(mockSession.getServletContext()).thenReturn(mockServletContext);
        when(mockRequest.getSession(any(Boolean.class))).thenReturn(mockSession);
        when(mockRequest.getAttribute(Constants.TENANT_ATTRIBUTE_NAME)).thenReturn(m_tenantName);
        when(m_pageContext.getRequest()).thenReturn(mockRequest);
    }
    @Test
    public void shouldWriteGoogleScriptTagIfConfigContainsGoogle()
        throws Exception
    {
        Application mockApp = (Application)mockServletContext.getAttribute(ApplicationStartupFilter.APPLICATION_ATTRIBUTE_NAME);
        TenantProfileManager tpm = mockApp.getTenantProfileManager();
        TenantProfile tp = tpm.getProfile("stratus");
        MapConfig mockMapConfig = tp.getMapConfigRepository().getMapConfig("test");
        MapConfig.MapDefinition mockMapDef = mockMapConfig.getMapDefinitionByMapName("test");
        mockMapDef.setService(LayerServiceType.GOOGLE);
        ThirdPartyAPIKey key = mockMapConfig.createThirdPartyAPIKey();
        key.setKey("abc");
        mockMapConfig.setThirdPartyAPIKeys(Collections.singletonList(key));
        key.setService(LayerServiceType.GOOGLE);
        inserter.doEndTag();
        verify(m_pageContext.getOut()).println("<script type=\"text/javascript\" "
                + "src=\"http://maps.google.com/maps?file=api&amp;v=2&amp;"
                + "key=abc\"></script>");
    }
}
