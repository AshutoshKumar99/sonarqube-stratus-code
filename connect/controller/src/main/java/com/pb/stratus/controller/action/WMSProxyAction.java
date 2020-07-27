/**
 * A proxy for WMS requests to WMS. Requests  can be forwarded to secure as well as non secure
 * WMS.  Three parameters are expected:
 * 1.
 * <p>
 * User: GU003DU
 * Date: 4/12/13
 * Time: 7:27 PM *
 */

package com.pb.stratus.controller.action;

import com.pb.stratus.controller.httpclient.HttpClientFactory;
import com.pb.stratus.controller.httpclient.UrlUtil;
import com.pb.stratus.controller.httpclient.WMSMapConfigHelper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class WMSProxyAction extends BaseControllerAction {

    private static final Logger LOGGER = LogManager.getLogger(WMSProxyAction.class);

    private static final String REQUIRED_PARAM_NOT_FOUND = "Required parameter mapUrl is not specified in request!";
    private static final String FAILED_TO_PROCESS_REQUEST = "Failed to process current request, please check logs for detailed information!";

    private static final String CACHE_CONTROL = "Cache-Control";
    /* Maximum cache time is 1 hour*/
    private static final String CACHE_CONTROL_MAX_AGE = "max-age=3600";
    private static final String DEFAULT_ENCODING = "UTF-8";

    private static final String PARAM_MAP_CFG = "mapcfg";
    private static final String PARAM_WMS_PROFILE = "WMS_PROFILE";
    private static final String PARAM_GET_MAP_URL = "MAP_URL";
    private static final String PARAM_SECURE = "SECURE";
    private static final String PARAM_WMS_URL = "WMS_URL";
    private static final String PARAM_WMS_REQUEST = "WMS_REQUEST";

    private HttpClientFactory httpClientFactory;
    private WMSMapConfigHelper configHelper;

    public WMSProxyAction(HttpClientFactory httpClientFactory, WMSMapConfigHelper configHelper) {
        this.httpClientFactory = httpClientFactory;
        this.configHelper = configHelper;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String secure = request.getParameter(PARAM_SECURE);
        String requestType = request.getParameter(PARAM_WMS_REQUEST);

        WMSMapConfigHelper.WMS_REQUEST_TYPE wmsRequestType;
        if (!StringUtils.isBlank(requestType)) {
            wmsRequestType = WMSMapConfigHelper.WMS_REQUEST_TYPE.valueOf(requestType);
        } else {
            // WMS_REQUEST_TYPE is used only to  validate the url, so it is fine even if the request is getfeature because it uses same url.
            wmsRequestType = WMSMapConfigHelper.WMS_REQUEST_TYPE.GetMap;
        }

        boolean isSecure = false;
        if (!StringUtils.isBlank(secure)) {
            isSecure = Boolean.parseBoolean(secure);
        }

        CloseableHttpClient httpClient = null;
        try {
            String wmsService = request.getParameter(PARAM_WMS_PROFILE);
            String requestUrl = getRequestUrl(request, wmsRequestType, isSecure);
            //CONN-16441: validate if the requested  url exists in current map config; to avoid the abuse of this proxy;
            if (wmsService != null)
                configHelper.validateUrl(UrlUtil.truncateQueryParams(requestUrl), wmsRequestType, wmsService);
            // create http client
            StringBuilder wmsRequest = new StringBuilder();
            wmsRequest.append(requestUrl);
            httpClient = httpClientFactory.getHttpClient(wmsService, isSecure, wmsRequest);

            // Execute the given url as HTTP get request.
            HttpGet serviceRequest = new HttpGet(wmsRequest.toString());
            HttpResponse serviceResponse = httpClient.execute(serviceRequest);
            if (serviceResponse.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
                writeSuccessResponse(serviceResponse, response);
            } else {
                writeFailureResponse(serviceResponse, response);
            }
        } catch (Exception e) {
            LOGGER.error(FAILED_TO_PROCESS_REQUEST, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, FAILED_TO_PROCESS_REQUEST);
        } finally {
            if (httpClient != null) {
                httpClient.getConnectionManager().shutdown();
            }
        }
    }

    /**
     * Send back success response.
     * Override this method if response need to be changed.
     *
     * @param wmsResponse
     * @param response
     * @throws IOException
     */

    protected void writeSuccessResponse(HttpResponse wmsResponse, HttpServletResponse response) throws IOException {

        org.apache.http.Header header = wmsResponse.getEntity().getContentType();
        if (header != null) {
            response.setContentType(header.getValue());
        }
        response.setHeader(CACHE_CONTROL, CACHE_CONTROL_MAX_AGE);
        wmsResponse.getEntity().writeTo(response.getOutputStream());
    }

    /**
     * Send back failure response.
     *
     * @param wmsResponse
     * @param response
     * @throws IOException
     */
    protected void writeFailureResponse(HttpResponse wmsResponse, HttpServletResponse response) throws IOException {
        response.setContentType(wmsResponse.getEntity().getContentType().getValue());
        response.sendError(wmsResponse.getStatusLine().getStatusCode(), IOUtils.toString(wmsResponse.getEntity().getContent()));
    }

    protected String getRequestUrl(HttpServletRequest request, WMSMapConfigHelper.WMS_REQUEST_TYPE requestType,
                                   boolean isSecure) throws Exception {
        // we always validate url(whitelist).
        String url = request.getParameter(PARAM_WMS_URL);
        if (StringUtils.isBlank(url)) {
            // it means request did not have WMS_URL parameter, create the url from mapUrl parameter and other parameters.
            // this is an alternative when WMS_URL is not in request; used for secure GetMap requests. Refer to  CONN-16440
            String getMapURL = request.getParameter(PARAM_GET_MAP_URL);
            if (isSecure) {
                if (StringUtils.isBlank(getMapURL)) {
                    throw new Exception(REQUIRED_PARAM_NOT_FOUND);
                }
                getMapURL = URLDecoder.decode(getMapURL, DEFAULT_ENCODING);
            }
            url = UrlUtil.appendQueryParameters(getMapURL, getParameters(request));
        }
        //CONN-16970
        if (WMSMapConfigHelper.WMS_REQUEST_TYPE.GetLegendGraphic == requestType) {
            url = URLDecoder.decode(url, DEFAULT_ENCODING);
        }
        return url;
    }

    /**
     * Get the relevant parameters from request.
     *
     * @param request
     * @return
     */
    private static Map<String, String> getParameters(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        Enumeration names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            if (PARAM_GET_MAP_URL.equalsIgnoreCase(name) || PARAM_MAP_CFG.equalsIgnoreCase(name)
                    || PARAM_SECURE.equalsIgnoreCase(name) || PARAM_WMS_REQUEST.equalsIgnoreCase(name)) {
                continue;
            }
            map.put(name, request.getParameter(name));
        }

        LOGGER.debug("query parameter: " + map);
        return map;
    }
}
