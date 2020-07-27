package com.pb.stratus.controller.application;

import com.pb.stratus.controller.configuration.ApplicationCustomerConfigDirHolder;
import com.pb.stratus.controller.configuration.CachingTenantProfileManager;
import com.pb.stratus.controller.configuration.TenantProfileManager;
import com.pb.stratus.controller.configuration.TenantProfileManagerImpl;
import com.pb.stratus.controller.util.WebServiceProxyFactory;
import com.pb.stratus.core.configuration.SystemPropertyCustomerConfigDirHolder;
import com.pb.stratus.core.configuration.TenantConfiguration;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.ServletContext;

/**
 * Servlet Application, starts up Connect application.
 */
public class ServletApplication implements Application {
	private static final Logger logger = LogManager
			.getLogger(ServletApplication.class);
    private ServletContext applicationScope;
    private ApplicationCustomerConfigDirHolder configDirHolder;

    private TenantConfiguration tenantConfiguration;
    private TenantProfileManager tenantProfileManager;

    public ServletApplication(ServletContext applicationScope) {
        this.applicationScope = applicationScope;
    }

    public void start() {
		logger.debug(" Stratus connect initialization started up.");
        initCustomerConfigHolder();
        initTenantConfiguration();
        initTenantProfileManager();
        WebServiceProxyFactory.init(applicationScope);
    }

    private void initCustomerConfigHolder() {
        configDirHolder = new ApplicationCustomerConfigDirHolder();
    }

    private void initTenantConfiguration() {
        tenantConfiguration = new TenantConfiguration(configDirHolder);
    }

    private void initTenantProfileManager() {
		tenantProfileManager = new CachingTenantProfileManager(new TenantProfileManagerImpl(getTenantConfiguration()));
    }

    public TenantProfileManager getTenantProfileManager() {
        return tenantProfileManager;
    }

    public TenantConfiguration getTenantConfiguration() {
        return tenantConfiguration;
    }
}
