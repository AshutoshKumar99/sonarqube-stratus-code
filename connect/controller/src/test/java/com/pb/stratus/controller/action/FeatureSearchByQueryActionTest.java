package com.pb.stratus.controller.action;

import com.pb.stratus.controller.application.Application;
import com.pb.stratus.controller.application.ApplicationStartupFilter;
import com.pb.stratus.controller.configuration.TenantProfile;
import com.pb.stratus.controller.configuration.TenantProfileManager;
import com.pb.stratus.controller.featuresearch.Query;
import com.pb.stratus.controller.featuresearch.FeatureService;
import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.json.geojson.GeoJsonParser;
import com.pb.stratus.controller.json.geojson.GeometryTestUtil;
import com.pb.stratus.controller.legend.MockServletContext;
import com.pb.stratus.controller.service.SearchByQueryParams;
import com.pb.stratus.controller.service.SearchWithinGeometryParams.SpatialOperation;
import com.pb.stratus.controller.util.helper.SpatialServicesHelper;
import com.pb.stratus.core.common.Constants;
import com.pb.stratus.core.configuration.FileSystemConfigReader;
import com.pb.stratus.security.core.resourceauthorization.ResourceAuthorization;
import com.pb.stratus.security.core.resourceauthorization.ResourceAuthorizationConfig;
import com.pb.stratus.security.core.resourceauthorization.ResourceType;
import com.pb.stratus.security.core.util.AuthorizationUtils;
import org.easymock.Capture;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.File;
import java.util.*;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FeatureSearchByQueryActionTest extends FeatureSearchActionTestBase
{

    private String srsName;
    private GeoJsonParser parser;
    private MockHttpServletRequest request;
    private FeatureSearchByQueryAction action;
    private FileSystemConfigReader mockConfigReader = null;
    private MockServletContext m_context;
    private Application m_application;
    private MockHttpSession m_session;
    private TenantProfileManager m_tenantProfileManager;
    private TenantProfile m_tenantProfile;
    private ResourceAuthorization m_resourceAuthorization;
    private SecurityContext m_securityContext;
    private Authentication m_authentication;
    private SpatialServicesHelper m_spatialHelper;
    private AuthorizationUtils m_authorizationUtils;
    private String TENANT_NAME = "tenant1";
    private String TABLE_NAME = "PlanningApplications";
    private String QUERY_NAME = "queryByName";
    private String ADMIN_ROLE = "Administrators";
    private String PUBLIC_ROLE = "Public";


    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        srsName = "epsg:27700";
        parser = new GeoJsonParser(srsName);
        request = getRequest();
        mockConfigReader = new FileSystemConfigReader(new File(
                "src\\test\\resources\\com\\pb\\stratus\\controller\\action")
                .getAbsolutePath(), "stratus");
        request = getRequest();
        m_context = new MockServletContext();
        m_application = mock(Application.class);
        m_context.setAttribute(ApplicationStartupFilter.APPLICATION_ATTRIBUTE_NAME, m_application);
        m_session = new MockHttpSession(m_context);
        request.setSession(m_session);
        m_tenantProfileManager = mock(TenantProfileManager.class);
        m_tenantProfile = mock(TenantProfile.class);
        m_resourceAuthorization = mock(ResourceAuthorization.class);
        m_securityContext = mock(SecurityContext.class);
        m_authentication = mock(Authentication.class);
        m_authorizationUtils = mock(AuthorizationUtils.class);
        m_spatialHelper = mock(SpatialServicesHelper.class);
        SecurityContextHolder.setContext(m_securityContext);
    }

    @Test
    public void testCreateObjectForAuthorizedUser() throws Exception
    {
        when(m_application.getTenantProfileManager()).thenReturn(m_tenantProfileManager);
        when(m_tenantProfileManager.getProfile(TENANT_NAME )).thenReturn(m_tenantProfile);
        when(m_tenantProfile.getResourceAuthorization(ResourceType.QUERY_CONFIG)).thenReturn(m_resourceAuthorization);
        when(m_securityContext.getAuthentication()).thenReturn(m_authentication);
        Collection<GrantedAuthority> userAuthority = new ArrayList<GrantedAuthority>();

//        when(m_authentication.getAuthorities()).thenReturn(userAuthority);
        Mockito.doReturn(userAuthority).when(m_authentication).getAuthorities();
        List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        grantedAuthorities.add(new GrantedAuthorityImpl("ROLE_"+ADMIN_ROLE));
        Set<ResourceAuthorizationConfig> configs = new HashSet<ResourceAuthorizationConfig>();
        configs.add(new ResourceAuthorizationConfig(QUERY_NAME, grantedAuthorities));
        when(m_resourceAuthorization.getAuthorizationConfigs(grantedAuthorities,TABLE_NAME)).thenReturn(configs);

        request.addParameter("tableName", TABLE_NAME);
        request.addParameter("queryName", QUERY_NAME);
        request.addParameter("includeGeometry", "true");
        request.addParameter("sourceSrs", srsName);
        request.addParameter("targetSrs", "someTargetSrs");
        request.addParameter("spatialOperation", "INTERSECTS");
        request.addParameter("totalCount", "12");
        request.setAttribute(Constants.TENANT_ATTRIBUTE_NAME, TENANT_NAME);
        String polygon = "{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100,100],[100,200],[200,200],[200,100],[100,100]]]]}";
        request.addParameter("polygon", polygon);
        FeatureService mockFeatureService = createMock(FeatureService.class);

        FeatureSearchResult expectedResult = createDummyResult();
        Capture<SearchByQueryParams> captureParams = new Capture<SearchByQueryParams>();
        expect(mockFeatureService.searchByQuery(capture(captureParams)))
                .andReturn(expectedResult);
        replay(mockFeatureService);
        action
                = new FeatureSearchByQueryAction(mockFeatureService,mockConfigReader, m_authorizationUtils, m_spatialHelper);

        when(m_authorizationUtils.getAuthorizeConfigs(request,
                ResourceType.QUERY_CONFIG, TABLE_NAME)).thenReturn(configs);
        when(m_authorizationUtils.configsContains(QUERY_NAME, configs)).thenReturn(true);
        Object actualResult = action.createObject(request);
        SearchByQueryParams actualParams = captureParams.getValue();
        actualParams.setGeometry(parser.parseGeometry(polygon));
        assertEquals("PlanningApplications", actualParams.getTableName());
        assertEquals(SpatialOperation.INTERSECTS, actualParams
                .getSpatialOperation());
        GeometryTestUtil.assertGeometry(parser.parseGeometry(polygon),
                actualParams.getGeometry());
        assertTrue(actualParams.isIncludeGeometry());
        assertEquals(srsName, actualParams.getSourceSrs());
        assertEquals("someTargetSrs", actualParams.getTargetSrs());
        assertEquals("12", actualParams.getTotalCount());
        assertEquals(expectedResult, actualResult);
    }

    @Test (expected=AccessDeniedException.class)
    public void testCreateObjectForUnAuthorizedUser() throws Exception
    {
        when(m_application.getTenantProfileManager()).thenReturn(m_tenantProfileManager);
        when(m_tenantProfileManager.getProfile(TENANT_NAME )).thenReturn(m_tenantProfile);
        when(m_tenantProfile.getResourceAuthorization(ResourceType.QUERY_CONFIG)).thenReturn(m_resourceAuthorization);
        when(m_securityContext.getAuthentication()).thenReturn(m_authentication);
        Collection<GrantedAuthority> userAuthority = new ArrayList<GrantedAuthority>();
        userAuthority.add(new GrantedAuthorityImpl(PUBLIC_ROLE +"_"+TENANT_NAME));
        //when(m_authentication.getAuthorities()).thenReturn(userAuthority);

        Mockito.doReturn(userAuthority).when(m_authentication).getAuthorities();

        List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        grantedAuthorities.add(new GrantedAuthorityImpl(ADMIN_ROLE));
        Set<ResourceAuthorizationConfig> configs = new HashSet<ResourceAuthorizationConfig>();
        configs.add(new ResourceAuthorizationConfig(QUERY_NAME, grantedAuthorities));
        when(m_resourceAuthorization.getAuthorizationConfigs(grantedAuthorities,TABLE_NAME)).thenReturn(configs);
        request.addParameter("tableName", TABLE_NAME);
        request.addParameter("queryName", QUERY_NAME);
        request.setAttribute(Constants.TENANT_ATTRIBUTE_NAME, TENANT_NAME);
        FeatureService mockFeatureService = createMock(FeatureService.class);
        action = new FeatureSearchByQueryAction(mockFeatureService,mockConfigReader, m_authorizationUtils, m_spatialHelper);
        when(m_authorizationUtils.getAuthorizeConfigs(request,
                ResourceType.QUERY_CONFIG, TABLE_NAME)).thenReturn(new HashSet<ResourceAuthorizationConfig>());
        action.createObject(request);
    }

}
