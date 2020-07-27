package com.pb.stratus.controller.configuration;

import com.pb.stratus.controller.action.ControllerActionFactory;
import com.pb.stratus.controller.application.Application;
import com.pb.stratus.controller.application.ApplicationStartupFilter;
import com.pb.stratus.controller.infrastructure.cache.TenantCacheable;
import com.pb.stratus.controller.print.config.MapConfigRepository;
import com.pb.stratus.controller.service.RESTAnalystProxy;
import com.pb.stratus.controller.service.RESTLocatorProxy;
import com.pb.stratus.core.common.Constants;
import com.pb.stratus.core.configuration.ControllerConfiguration;
import com.pb.stratus.security.core.resourceauthorization.ResourceAuthorization;
import com.pb.stratus.security.core.resourceauthorization.ResourceType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class TenantProfileManagerImplTest
{
    private MockHttpServletRequest mockRequest;
	TenantProfileManager mockTenantProfileManager;

    @Before
    public void Setup() {
		mockTenantProfileManager = mock(TenantProfileManager.class);
		when(mockTenantProfileManager.getProfile(anyString())).thenAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				return new MockTenantProfile((String) invocationOnMock.getArguments()[0]);
			}
		});

		Application mockApplication = mock(Application.class);
		when(mockApplication.getTenantProfileManager()).thenReturn(mockTenantProfileManager);

        mockRequest = new MockHttpServletRequest();
		mockRequest.getSession().getServletContext().setAttribute(ApplicationStartupFilter.APPLICATION_ATTRIBUTE_NAME, mockApplication);
    }

    @Test
    public void testGetRequestProfile() throws Exception {
		mockRequest.setAttribute(Constants.TENANT_ATTRIBUTE_NAME, "tenant");
        TenantProfile profile = TenantProfileManagerImpl.getRequestProfile(mockRequest);
        assertNotNull(profile);
        assertEquals("tenant", profile.getTenantName());
    }

	@Test
	public void testTenantNameIsLowerCase()
	{
		mockRequest.setAttribute(Constants.TENANT_ATTRIBUTE_NAME, "MixedCaseTenantName");
        TenantProfile profile = TenantProfileManagerImpl.getRequestProfile(mockRequest);
        assertEquals("mixedcasetenantname", profile.getTenantName());
	}

	private class MockTenantProfile implements TenantProfile
	{
		private String m_tenantName;

		private MockTenantProfile(String tenantName)
		{
			m_tenantName = tenantName;
		}

		@Override
		public String getTenantName()
		{
			return m_tenantName;
		}

		@Override
		public ControllerActionFactory getActionFactory()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public ControllerConfiguration getConfiguration()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public MapConfigRepository getMapConfigRepository()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public TenantCacheable getTenantLegendCache()
		{
			throw new UnsupportedOperationException();
		}

        @Override
        public ResourceAuthorization getResourceAuthorization(ResourceType resourceType) {
            throw new UnsupportedOperationException();
	    }

        @Override
        public RESTLocatorProxy getLocateRestProxy() {
            throw new UnsupportedOperationException();
        }

		@Override
		public RESTAnalystProxy getAnalystRestProxy() {
			throw new UnsupportedOperationException();
		}

        @Override
        public int getSessionTimeout() {
            throw new UnsupportedOperationException();
        }
    }
}
