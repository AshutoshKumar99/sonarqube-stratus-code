package com.pb.stratus.controller.wmsprofile;

import com.pb.stratus.controller.httpclient.Authentication;

import java.util.List;
import java.util.Map;

/**
 * Created by vi012gu on 25-09-2018.
 */
public class WMSProfile {
    private String url;
    private Authentication authn;
    private Map<String, String> credentials;
    private List<String> legendURLs;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Authentication getAuthn() {
        return authn;
    }

    public void setAuthn(Authentication authn) {
        this.authn = authn;
    }

    public Map<String, String> getCredentials() {
        return credentials;
    }

    public void setCredentials(Map<String, String> credentials) {
        this.credentials = credentials;
    }

    public List<String> getLegendURLs() {
        return legendURLs;
    }

    public void setLegendURLs(List<String> legendURLs) {
        this.legendURLs = legendURLs;
    }
}