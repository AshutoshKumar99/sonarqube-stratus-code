package com.pb.stratus.controller.httpclient;

import com.pb.stratus.controller.service.ProjectService;
import com.pb.stratus.controller.wmsprofile.WMSProfile;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.co.graphdata.utilities.contract.Contract;

/**
 * Created with IntelliJ IDEA.
 * User: GU003DU
 * Date: 5/14/13
 * Time: 3:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class WMSMapConfigHelper {

    public enum WMS_REQUEST_TYPE {GetMap, GetFeatureInfo, GetLegendGraphic}

    private static final String INVALID_URL = "Invalid Url requested!";
    private static final String INVALID_WMS_PROFILE = "Invalid WMS profile requested!";
    private static final String NOT_AUTHORIZED = "You are not authorized to request this WMS, check with Stratus Administrator.";

    private ProjectService projectService;

    public WMSMapConfigHelper(ProjectService projectService) {
        this.projectService = projectService;
    }

    public void validateUrl(String url, WMS_REQUEST_TYPE requestType, String service) {

        // validate parameters
        Contract.pre(!StringUtils.isBlank(url), INVALID_URL);
        Contract.pre(!StringUtils.isBlank(service), INVALID_WMS_PROFILE);

        // Get the configuration for a secure wms service.
        WMSProfile wmsProfile = getWMSProfileFromSession(service);
        try {
            if (wmsProfile == null) {
                wmsProfile = getWMSProfile(service);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(NOT_AUTHORIZED, e);
        }

        boolean found = false;
        if (requestType == WMS_REQUEST_TYPE.GetMap || requestType == WMS_REQUEST_TYPE.GetFeatureInfo) {
            found = validateGetMapUrl(url, wmsProfile);
        } else if (requestType == WMS_REQUEST_TYPE.GetLegendGraphic) {
            found = validateGetLegendGraphicUrl(url, wmsProfile);
        }

        if (!found) {
            throw new IllegalArgumentException(NOT_AUTHORIZED);
        }
    }

    private WMSProfile getWMSProfile(String service) throws Exception {
        WMSProfile wmsProfile = this.projectService.getWMSProfile(service);
        // Set WMSProfile in session
        ServletRequestAttributes requestAttributes = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes());
        requestAttributes.getRequest().getSession().setAttribute(service, wmsProfile);
        return wmsProfile;
    }

    private WMSProfile getWMSProfileFromSession(String wmsProfileName) {
        ServletRequestAttributes requestAttributes = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes());
        return (WMSProfile) requestAttributes.getRequest().getSession().getAttribute(wmsProfileName);
    }

    /**
     * Validate if specified url exists in <map> section of map configuration.
     *
     * @param url
     * @param wmsProfile
     * @return
     */
    private boolean validateGetMapUrl(String url, WMSProfile wmsProfile) {
        return url.equalsIgnoreCase(UrlUtil.truncateQueryParams(wmsProfile.getUrl()));
    }

    /**
     * Validate if specified legend url exists in <map> section of map configuration.
     *
     * @param url        Legend Url
     * @param wmsProfile WMS configuration
     * @return
     */
    private boolean validateGetLegendGraphicUrl(String url, WMSProfile wmsProfile) {
        boolean found = false;
        for (String legendUrl : wmsProfile.getLegendURLs()) {
            if (UrlUtil.truncateQueryParams(legendUrl).equalsIgnoreCase(url)) {
                found = true;
                break;
            }
        }
        return found;
    }
}
