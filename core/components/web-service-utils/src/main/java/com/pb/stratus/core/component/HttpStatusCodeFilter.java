/*****************************************************************************
 *       Copyright © 2012, Pitney Bowes Software Inc.
 *       All  rights reserved.
 *       Confidential Property of Pitney Bowes Software Inc.
 *
 * $Author: $
 * $Revision: $
 * $LastChangedDate: $
 *
 * $HeadURL: $
 *****************************************************************************/
package com.pb.stratus.core.component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

public class HttpStatusCodeFilter implements Filter {
    private static final Logger logger = Logger.getLogger(HttpStatusCodeFilter.class);

    private static final int MIN_CLIENT_ERROR = 400;
    private static final int MAX_CLIENT_ERROR = 499;
    private static final int MIN_SERVER_ERROR = 500;
    private static final int MAX_SERVER_ERROR = 999;

    private static final String INIT_PARAM_TRIGGER_HEADER = "triggerHeader";
    private static final String INIT_PARAM_STATUS_CODE_HEADER = "statusCodeHeader";
    private static final String INIT_PARAM_CONVERT_CLIENT_ERROR_CODES_TO = "convertClientErrorCodesTo";
    private static final String INIT_PARAM_CONVERT_SERVER_ERROR_CODES_TO = "convertServerErrorCodesTo";
    private static final String INIT_PARAM_ACCEPTABLE_CLIENT_ERROR_CODES = "acceptableClientErrorCodes";
    private static final String INIT_PARAM_ACCEPTABLE_SERVER_ERROR_CODES = "acceptableServerErrorCodes";

    private String triggerHeader;
    private String statusCodeHeader;
    private int convertClientErrorCodesTo;
    private int convertServerErrorCodesTo;
    private Set<Integer> acceptableClientErrorCodes;
    private Set<Integer> acceptableServerErrorCodes;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        /* check for this header on the request to see if we should apply the response wrapper */
        triggerHeader = getRequiredParam(filterConfig, INIT_PARAM_TRIGGER_HEADER);

        /* set this header on the response to be the original status code */
        statusCodeHeader = getRequiredParam(filterConfig, INIT_PARAM_STATUS_CODE_HEADER);

        /* the code to use for unacceptable client errors */
        convertClientErrorCodesTo = getErrorCodeParam(filterConfig, INIT_PARAM_CONVERT_CLIENT_ERROR_CODES_TO, MIN_CLIENT_ERROR, MIN_CLIENT_ERROR, MAX_CLIENT_ERROR);

        /* the code to use for unacceptable server errors */
        convertServerErrorCodesTo = getErrorCodeParam(filterConfig, INIT_PARAM_CONVERT_SERVER_ERROR_CODES_TO, MIN_SERVER_ERROR, MIN_SERVER_ERROR, MAX_SERVER_ERROR);

        /* the list of acceptable client error codes, comma separated */
        String clientErrorsString = getRequiredParam(filterConfig, INIT_PARAM_ACCEPTABLE_CLIENT_ERROR_CODES);
        acceptableClientErrorCodes = parseIntString(clientErrorsString, MIN_CLIENT_ERROR, MAX_CLIENT_ERROR);

        /* the list of acceptable server error codes, comma separated */
        String serverErrorsString = getRequiredParam(filterConfig, INIT_PARAM_ACCEPTABLE_SERVER_ERROR_CODES);
        acceptableServerErrorCodes = parseIntString(serverErrorsString, MIN_SERVER_ERROR, MAX_SERVER_ERROR);
    }

    private String getRequiredParam(FilterConfig filterConfig, String key) throws ServletException {
        String value = filterConfig.getInitParameter(key);
        if(value == null || value.trim().length() == 0)
        {
            throw new ServletException("Missing required init parameter: " + key);
        }
        return value;
    }

    private int getErrorCodeParam(FilterConfig filterConfig, String key, int defaultValue, int min, int max) throws ServletException {
        String str = filterConfig.getInitParameter(key);
        if(str != null)
        {
            try {
                int intValue = Integer.parseInt(str);

                if(intValue < min || intValue > max) {
                    throw new ServletException("Parsed integer (" + intValue + ") outside acceptable range of [" + min + "," + max + "]");
                }

                return intValue;
            }
            catch (NumberFormatException nfex)
            {
                throw new ServletException("Unable to parse integers from: " + str, nfex);
            }
        }

        return defaultValue;
    }

    private Set<Integer> parseIntString(String str, int min, int max) throws ServletException {
        Set<Integer> integers = new HashSet<Integer>();

        StringTokenizer tokenizer = new StringTokenizer(str, ",");
        while(tokenizer.hasMoreElements())
        {
            try
            {
                Integer intValue = Integer.parseInt(tokenizer.nextToken());

                if(intValue < min || intValue > max) {
                    throw new ServletException("Unable to parse integers from: " + str + ".  Parsed integer (" + intValue + ") outside acceptable range of [" + min + "," + max + "]");
                }

                integers.add(intValue);
            }
            catch (NumberFormatException nfex)
            {
                throw new ServletException("Unable to parse integers from: " + str, nfex);
            }
        }

        if(integers.isEmpty())
        {
            throw new ServletException("Unable to parse integers from: " + str);
        }

        return integers;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        boolean doFilter = ((HttpServletRequest) servletRequest).getHeader(triggerHeader) != null;
        if (doFilter) {
            logger.debug("Status codes for this response will be filtered");
            HttpStatusCodeFilterServletResponseWrapper wrappedResponse =
                    new HttpStatusCodeFilterServletResponseWrapper((HttpServletResponse) servletResponse,
                            statusCodeHeader, convertClientErrorCodesTo, convertServerErrorCodesTo,
                            acceptableClientErrorCodes, acceptableServerErrorCodes);
            filterChain.doFilter(servletRequest, wrappedResponse);
        } else {
            logger.debug("Status codes for this response will not be filtered");
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
        // no-op
    }

    private static class HttpStatusCodeFilterServletResponseWrapper extends HttpServletResponseWrapper {
        private final String statusCodeHeader;
        private final int convertClientErrorCodesTo;
        private final int convertServerErrorCodesTo;
        private final Set<Integer> acceptableClientErrorCodes;
        private final Set<Integer> acceptableServerErrorCodes;

        private HttpStatusCodeFilterServletResponseWrapper(HttpServletResponse response, String statusCodeHeader,
                                                           int convertClientErrorCodesTo, int convertServerErrorCodesTo,
                                                           Set<Integer> acceptableClientErrorCodes, Set<Integer> acceptableServerErrorCodes) {
            super(response);
            this.statusCodeHeader = statusCodeHeader;
            this.convertClientErrorCodesTo = convertClientErrorCodesTo;
            this.convertServerErrorCodesTo = convertServerErrorCodesTo;
            this.acceptableClientErrorCodes = acceptableClientErrorCodes;
            this.acceptableServerErrorCodes = acceptableServerErrorCodes;
        }

        @Override
        public void setStatus(int sc) {
            logger.debug("Status code set by: setStatus(" + sc + ")");
            super.setStatus(standardizeStatus(sc));
        }

        @Override
        public void setStatus(int sc, String msg) {
            logger.debug("Status code set by: setStatus(" + sc + "," + msg + ")");
            super.setStatus(standardizeStatus(sc), msg);
        }

        @Override
        public void sendError(int sc) throws IOException {
            logger.debug("Status code set by: sendError(" + sc + ")");
            super.sendError(standardizeStatus(sc));
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {
            logger.debug("Status code set by: sendError(" + sc + "," + msg + ")");
            super.sendError(standardizeStatus(sc), msg);
        }

        private int standardizeStatus(int status) {
            int newStatus = status;
            if (status >= MIN_CLIENT_ERROR && status <= MAX_CLIENT_ERROR && !acceptableClientErrorCodes.contains(status)) {
                newStatus = convertClientErrorCodesTo;
            } else if (status >= MIN_SERVER_ERROR && status <= MAX_SERVER_ERROR && !acceptableServerErrorCodes.contains(status)) {
                newStatus = convertServerErrorCodesTo;
            }

            if (status != newStatus) {
                logger.debug("Changing status to: " + newStatus);
                logger.debug("Saving old status in header: " + statusCodeHeader + " = " + status);
                ((HttpServletResponse)getResponse()).setIntHeader(statusCodeHeader, status);
            }

            return newStatus;
        }
    }
}
