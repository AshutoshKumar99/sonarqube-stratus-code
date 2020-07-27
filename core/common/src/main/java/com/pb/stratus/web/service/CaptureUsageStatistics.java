package com.pb.stratus.web.service;

import com.pb.stratus.core.common.Constants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: su019ch
 * Date: 7/18/14
 * Time: 5:14 PM
 * To change this template use File | Settings | File Templates.
 */
public interface CaptureUsageStatistics {

    static final String UNKNOWN = "";
    static final String SESSION_ID = "sessionId";
    static final String USER_AGENT = "user-agent";
    static final String USER = "user";
    static final String REQUEST_URL = "requestURL";
    static final String OPERATION_NAME = "operationName";
    static final String MAP_CONFIG = "mapcfg";
    static final String POST = "POST";
    static final String URL_PARAMS = "urlParams";
    static final String APPLICATION = "application";
    static final String EXTERNAL_HOST_IP_HEADER = "X-Archived-Client-IP";
    static final String EXTERNAL_HOST_IP = "externalHostIp";
    static final String INTERNAL_HOST_IP = "remoteIpAddr";
    static final String RESPONSE_TIME = "responseTime";
    static final String RESPONSE_STATUS_CODE = "statusCode";
    static final String RESPONSE_SIZE = "responseSize";
    static final String RESPONSE_SIZE_HEADER = "Content-Length";

    static final String PATTERN = "\"%d{dd-MMM-yyyy}\",\"%d{HH:mm:ss}\"," +
            "\"%X{"+EXTERNAL_HOST_IP+"}\"," +
            "\"%X{"+USER_AGENT+"}\"," +
            "\"%X{"+ Constants.TENANT_ATTRIBUTE_NAME+"}\"," +
            "\"%X{"+OPERATION_NAME+"}\"," +
            "\"%X{"+MAP_CONFIG+"}\"," +
            "\"%X{"+REQUEST_URL+"}\"," +
            "\"%X{"+URL_PARAMS+"}\"," +
            "\"%X{"+USER+"}\"," +
            "\"%X{"+SESSION_ID+"}\"," +
            "\"%X{"+RESPONSE_STATUS_CODE+"}\"," +
            "\"%X{"+RESPONSE_SIZE+"}\"," +
            "\"%X{"+RESPONSE_TIME+"}\" %n";

    void captureApplicationName(HttpServletRequest httpRequest);
    void captureHostIP(HttpServletRequest httpServletRequest);
    void captureInternalHostIP(HttpServletRequest httpRequest);
    void captureUserAgent(HttpServletRequest httpRequest);
    void captureTenant(HttpServletRequest httpRequest);
    void captureOperationName(HttpServletRequest httpRequest);
    void captureMapConfig(HttpServletRequest httpRequest);
    void captureParameters(HttpServletRequest httpRequest);
    void captureUserId();
    void captureSessionId(HttpServletRequest httpRequest);
    void captureResponseStatusCode(HttpServletResponse httpResponse);
    void captureResponseTime(long startTime);
    void captureResponseSize(HttpServletResponse httpResponse);
    void captureBaseUrl(HttpServletRequest httpRequest);
}
