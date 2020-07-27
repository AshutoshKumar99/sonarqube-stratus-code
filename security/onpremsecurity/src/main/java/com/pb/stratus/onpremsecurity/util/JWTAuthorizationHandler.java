package com.pb.stratus.onpremsecurity.util;

import com.pb.spectrum.platform.server.common.security.token.TokenInfo;
import com.pb.spectrum.platform.server.common.security.token.TokenLogoutService;
import com.pb.spectrum.platform.server.common.security.token.TokenManagerService;
import com.pb.stratus.onpremsecurity.authentication.SpectrumTokenService;
import com.pb.stratus.onpremsecurity.token.SpectrumToken;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPException;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by VI001TY on 3/28/2016.
 */
public class JWTAuthorizationHandler implements ApplicationContextAware {
    private static final Logger logger = Logger.getLogger(JWTAuthorizationHandler.class);
    public static final String AUTHENTICATION_HEADER = "Authorization";
    public static final String COOKIE_HEADER = "Cookie";
    public static final String BEARER_TOKEN_PREFIX = "Bearer ";

    public static final String SPECTRUM_TOKEN = "SpectrumToken";
    public static final String SESSION = "SESSION";
    public static final String SPECTRUM_TOKEN_SERVICE = "spectrumTokenService";


    private TokenManagerService tokenManagerService;
    private ApplicationContext applicationContext;
    private TokenLogoutService tokenLogoutService;

    private SpectrumTokenService spectrumTokenService;

    public TokenManagerService getTokenManagerService() {
        return tokenManagerService;
    }

    public void setTokenManagerService(TokenManagerService tokenManagerService) {
        this.tokenManagerService = tokenManagerService;
    }

    public void setTokenLogoutService(TokenLogoutService tokenLogoutService) {
        this.tokenLogoutService = tokenLogoutService;
    }

    public SpectrumTokenService getSpectrumTokenService() {
        if (spectrumTokenService == null) {
            spectrumTokenService = (SpectrumTokenService) applicationContext.
                    getBean(SPECTRUM_TOKEN_SERVICE);
        }
        return spectrumTokenService;
    }

    public void setSpectrumTokenService(SpectrumTokenService spectrumTokenService) {
        this.spectrumTokenService = spectrumTokenService;
    }

    /**
     * Adds JWT  authentication header to http request
     */
    public Map<String, List<String>> getJWTHeaders(boolean forceCreate) {
        Map<String, List<String>> headers = new HashMap<>();
        try {
            HttpServletRequest curRequest =
                    ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                            .getRequest();
            HttpSession session = curRequest.getSession(false);

            SpectrumToken spectrumToken = null;
            if (null != session.getAttribute(SPECTRUM_TOKEN)) {
                spectrumToken = (SpectrumToken)
                        session.getAttribute(SPECTRUM_TOKEN);
            }

            if (spectrumToken == null || spectrumToken.isExpired()) {
                spectrumToken = getSpectrumToken(forceCreate);
            }


            // Use a session token - set the token in authorization header
            headers.put(AUTHENTICATION_HEADER,
                    Collections.singletonList(BEARER_TOKEN_PREFIX + spectrumToken.getAccessToken()));

            // If the token is a session token, you must also provide the session identifier in the Cookie header
            headers.put(COOKIE_HEADER,
                    Collections.singletonList(SESSION + "=" + spectrumToken.getSession()));

        } catch (WebServiceException ex) {

            if (ex.getCause() instanceof HTTPException) {
                HTTPException e = (HTTPException) ex.getCause();
                if (e.getResponseCode() == HttpStatus.UNAUTHORIZED.value()) {
                    logger.error("UnAuthorized Privileges revoked");
                } else {
                    logger.error("HTTP Error with Status code is: " + e.getResponseCode());
                    logger.error(e);
                }
            }
        } catch (Exception e) {
            logger.error("Error Calling TokenEndPoint");
            logger.error(e);
        }
        return headers;
    }

    //This method can be called by multiple threads but only should be allowed to generate token otherwise multiple threads
    // may be generating more than on tokens
    public synchronized SpectrumToken getSpectrumToken(boolean forceCreate)
            throws URISyntaxException, IOException {

        SpectrumToken spectrumToken;

        HttpServletRequest curRequest =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        .getRequest();

        HttpSession session = curRequest.getSession(false);

        //Double checking here to avoid duplicate token generation for same session
        // As there may be a case where Caller may be waiting for getting null in session for
        //token but before getting a chance to execute token got set by other thread in session
        if (!forceCreate &&
                null != session.getAttribute(SPECTRUM_TOKEN) &&
                !((SpectrumToken)
                        session.getAttribute(SPECTRUM_TOKEN)).isExpired()) {

            spectrumToken = (SpectrumToken)
                    session.getAttribute(SPECTRUM_TOKEN);
        } else {
            //For the case when a user logs in with credentials, the token obtained for anonymous user on initial load should get logged out
            if (forceCreate && null != session.getAttribute(SPECTRUM_TOKEN) &&
                    !((SpectrumToken) session.getAttribute(SPECTRUM_TOKEN)).isExpired()) {
                getSpectrumTokenService().logout();
                // CONN-27717: clear the token so that token logout is not called multiple
                // times when user enters wrong credentials.
                session.removeAttribute(SPECTRUM_TOKEN);
            }

            //obtain new token
            spectrumToken = new SpectrumToken();
            String uri = (String) curRequest.getAttribute("javax.servlet.forward.request_uri");
            if (uri != null && (uri.indexOf("getHistoricTrafficBucket") > 0 || uri.indexOf("getAvailableRoutingDatabases") > 0 ||
                    uri.indexOf("listAvailableGeocodeServices") > 0)) {
                org.apache.cxf.jaxws.JaxWsProxyFactoryBean tokenFactory = (JaxWsProxyFactoryBean) applicationContext.getBean("tokenManagerServiceJaxWsProxyFactory");
                tokenFactory.setAddress(curRequest.getParameter("baseUrl") + "/security/TokenManagerService");
                TokenManagerService tokenService = (TokenManagerService) tokenFactory.create();
                TokenInfo tokenInfo = tokenService.getAccessExpiringToken(5);
                spectrumToken.setAccessToken(tokenInfo.getToken());
                session.setAttribute(SPECTRUM_TOKEN, spectrumToken);
            } else {
                spectrumToken = getSpectrumTokenService().getSessionToken();
                session.setAttribute(SPECTRUM_TOKEN, spectrumToken);
            }
        }
        return spectrumToken;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
