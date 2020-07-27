package com.pb.stratus.core.configuration;

import java.net.URL;

public interface ControllerConfiguration
{

    public void reload();

    public URL getSingleLineSearchServiceWsdlUrl();

    public URL getAdminConsoleUrl();

    public URL getTenantThemeUrl();

    public URL getTileServiceUrl();

    public URL getSpatialServiceBaseUrl();

    public URL getProjectServiceUrl();

    public URL getSSAMapProjectBaseUrl();

    public int getMaxFeatureSearchResults();

    public int getLegendCacheTimeoutInMinutes();

    public String getProjectServiceApiKey();
    public String getLocatorImageForPrint();

    public String getCallOutInfoImageForPrint();

    public URL getMappingServiceWsdlForBaseMapsUrl();

    public int getMaximumNumberOfPrintableTiles();

    public String getBingServicesPrivateApiKey();

    public String getBingServicesPublicApiKey();

    public String getSsoStartUrl();

    public String getSloStartUrl();

    public String getAnonymousUserName();

    public String getAnonymousPassword();

    public String getAuthType();

    public String getOauth2ClientId();

    public String getOauth2ClientSecret();

    public URL getOauth2AuthorizationServerUrl();

    public String getUsageDataSource();

    public String getUsageDataSourceFile();

    public String getDBUserName();

    public String getDBUrl();

    public String getDBPassword();

    public String getDBDriverClassName();

    public boolean isCaptureUsageData();

    public boolean isLegendCacheEnabled();

    public String getPoolSize();

    public String getDBBufferSize();

    public String getApplicationLinkingEnabled();

    public String getExtensibilityEnabled();

    public String getApplicationLinkingHostIP();

    public String getApplicationLinkingHostPort();

    public boolean isApplicationLinkingRegistrationShortCircuited();

}
