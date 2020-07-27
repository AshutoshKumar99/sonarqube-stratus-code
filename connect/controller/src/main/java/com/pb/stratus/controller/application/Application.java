package com.pb.stratus.controller.application;

import com.pb.stratus.controller.configuration.TenantProfileManager;
import com.pb.stratus.core.configuration.TenantConfiguration;

/**
 * Created by IntelliJ IDEA.
 * User: yo003ba
 * Date: Aug 12, 2011
 * Time: 12:52:10 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Application {
    void start();

    TenantProfileManager getTenantProfileManager();

    TenantConfiguration getTenantConfiguration();
}
