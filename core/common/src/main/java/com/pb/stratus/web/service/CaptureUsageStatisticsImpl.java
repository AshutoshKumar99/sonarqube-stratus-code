package com.pb.stratus.web.service;

import com.pb.stratus.core.common.Constants;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: su019ch
 * Date: 7/18/14
 * Time: 5:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class CaptureUsageStatisticsImpl implements CaptureUsageStatistics {

    private static String DEFAULT_MAP_CFG = "defaultmap";

    public void captureSessionId(HttpServletRequest httpRequest)
    {
        String sessionId = httpRequest.getSession().getId();
        ThreadContext.put(SESSION_ID, sessionId);
    }

    public void captureUserAgent(HttpServletRequest httpRequest)
    {
        String userAgent = httpRequest.getHeader(USER_AGENT);
        ThreadContext.put(USER_AGENT, userAgent);
    }

    public void captureTenant(HttpServletRequest httpRequest)
    {
        String tenantName = (String)httpRequest.getAttribute(Constants.TENANT_ATTRIBUTE_NAME);
        if(StringUtils.isEmpty(tenantName))
        {
            tenantName = UNKNOWN;
        }
        ThreadContext.put(Constants.TENANT_ATTRIBUTE_NAME, tenantName);
    }

    public void captureUserId()
    {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        String userName;
        if (authentication.getPrincipal() != null)
        {
            userName = authentication.getPrincipal().toString();

        } else {

            userName = UNKNOWN;
        }
        ThreadContext.put(USER, userName);
    }

    public void captureBaseUrl(HttpServletRequest httpRequest)
    {
        String requestUrl = httpRequest.getHeader("referer");
        ThreadContext.put(REQUEST_URL, requestUrl);
    }

    public void captureOperationName(HttpServletRequest httpRequest)
    {
        String requestUrl = httpRequest.getRequestURL().toString();
        String method = getOperationName(requestUrl);
        ThreadContext.put(OPERATION_NAME, method);
    }

    private String getOperationName(String requestURL)
    {
        String method = "";
        String[] urlParts = requestURL.split("/");
        method = urlParts[urlParts.length-1];

        if (!StringUtils.isEmpty(method)) {
            // Assumed - method name does not contain the dot character
            if (StringUtils.contains(method, "."))
            {
                method = "";
            }
        }
        return method;
    }

    public void captureMapConfig(HttpServletRequest httpRequest)
    {
        String mapCfg = httpRequest.getParameter(MAP_CONFIG);
        if (StringUtils.isEmpty(mapCfg))
        {
            mapCfg = "";
        }
        ThreadContext.put(MAP_CONFIG, mapCfg);
    }

    public void captureParameters(HttpServletRequest httpRequest)
    {
        String queryParams;
        String requestType = httpRequest.getMethod();

        if (StringUtils.equals(requestType, POST))
        {
            queryParams = getPostQueryParams(httpRequest);
        }
        else
        {
            queryParams = httpRequest.getQueryString();
        }
        if (StringUtils.isEmpty(queryParams))
        {
            queryParams = UNKNOWN;
        }

        ThreadContext.put(URL_PARAMS, StringUtils.replace(queryParams, "\"", "\'"));
    }

    private String getPostQueryParams(HttpServletRequest httpRequest)
    {
        String queryParams = "";
        Map paramsMap = httpRequest.getParameterMap();

        Iterator itr = paramsMap.entrySet().iterator();
        while (itr.hasNext())
        {
            Map.Entry mapEntry = (Map.Entry) itr.next();

            String mapEntryKey = (String) mapEntry.getKey();
            String mapEntryVal = ((String[])mapEntry.getValue())[0];

            if (!StringUtils.contains(mapEntryVal, "\"coordinates\":")
                    && !StringUtils.contains(mapEntryKey, "dojo.preventCache"))
            {
                if (StringUtils.isEmpty(queryParams))
                {
                    queryParams = mapEntryKey + "=" + mapEntryVal;
                }
                else
                {
                    queryParams = queryParams + "&" + mapEntryKey + "=" + mapEntryVal;
                }
            }
        }
        return queryParams;
    }

    public void captureApplicationName(HttpServletRequest httpRequest)
    {
        String applicationName = httpRequest.getContextPath().substring(1);
        ThreadContext.put(APPLICATION, applicationName);
    }

    public void captureHostIP(HttpServletRequest httpServletRequest)
    {
        if (httpServletRequest.getHeader(EXTERNAL_HOST_IP_HEADER) == null)
        {
            ThreadContext.put(EXTERNAL_HOST_IP, httpServletRequest.getRemoteAddr());
        }
        else
        {
            String externalHostIP = httpServletRequest.getHeader(EXTERNAL_HOST_IP_HEADER);
            String[] hostIpsArr = externalHostIP.split(",");
            if (hostIpsArr.length > 1)
            {
                // The first Ip address is of client IP address
                externalHostIP = hostIpsArr[0];
            }
            ThreadContext.put(EXTERNAL_HOST_IP, externalHostIP);
        }
    }

    public void captureInternalHostIP(HttpServletRequest httpRequest)
    {
        String ipAddress = httpRequest.getRemoteAddr();
        if (StringUtils.isEmpty(ipAddress))
        {
            ipAddress = UNKNOWN;
        }
        ThreadContext.put(INTERNAL_HOST_IP, ipAddress);
    }

    public void captureResponseTime(long startTime)
    {
        long elapsedTime = System.currentTimeMillis() - startTime;
        ThreadContext.put(RESPONSE_TIME, String.valueOf(elapsedTime));
    }

    public void captureResponseStatusCode(HttpServletResponse httpResponse)
    {
        ThreadContext.put(RESPONSE_STATUS_CODE, String.valueOf(httpResponse.getStatus()));
    }

    public void captureResponseSize(HttpServletResponse httpResponse)
    {
        String responseSize = httpResponse.getHeader(RESPONSE_SIZE_HEADER);
        if (StringUtils.isEmpty(responseSize))
        {
            responseSize = "0";
        }
        ThreadContext.put(RESPONSE_SIZE, responseSize);
    }
}