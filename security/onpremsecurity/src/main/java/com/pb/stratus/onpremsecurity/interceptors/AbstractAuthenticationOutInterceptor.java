/**
 * This is a CXF interceptor implementation for fetching security token for.
 * 1> authenticate Requests
 * 2> non-anonymous user requests.
 *
 * It uses basic authentication while fetching the Spectrum security tokens using TokenManager service.
 * Spectrum security token is stored in user session after a successful retrieval. Any subsequent web
 * service invocations use the cached token.
 * <p/>
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 4/4/14
 * Time: 7:07 PM
 *
 */

package com.pb.stratus.onpremsecurity.interceptors;

import com.pb.stratus.onpremsecurity.util.JWTAuthorizationHandler;
import com.pb.stratus.security.core.connect.identity.RequestBasisAccessConfigurationResolver;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.log4j.Logger;
import org.apache.ws.security.util.Base64;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class AbstractAuthenticationOutInterceptor extends AbstractPhaseInterceptor<Message> {

    private static final Logger logger = Logger.getLogger(AbstractAuthenticationOutInterceptor.class);

    private static final String BASIC_AUTHENTICATION_HEADER = "Authorization";
    private static final String CXF_ENDPOINT_PARAM = "org.apache.cxf.message.Message.ENDPOINT_ADDRESS";
    private static final String TM_ENDPOINT_NAME = "TokenManagerService";
    private static final String SPECTRUM_TOKEN_CREATE = "spetrum.token.create";

    protected String usernameParameter = UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY;
    protected String passwordParameter = UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_PASSWORD_KEY;

    public void setRequestBasisAccessConfigurationResolver(RequestBasisAccessConfigurationResolver requestBasisAccessConfigurationResolver) {
        this.requestBasisAccessConfigurationResolver = requestBasisAccessConfigurationResolver;
    }

    protected RequestBasisAccessConfigurationResolver requestBasisAccessConfigurationResolver;

    public void setJwtAuthorizationHandler(JWTAuthorizationHandler jwtAuthorizationHandler) {
        this.jwtAuthorizationHandler = jwtAuthorizationHandler;
    }

    protected JWTAuthorizationHandler jwtAuthorizationHandler;

    public void setPasswordParameter(String passwordParameter) {
        this.passwordParameter = passwordParameter;
    }

    public void setUsernameParameter(String usernameParameter) {
        this.usernameParameter = usernameParameter;
    }

    public AbstractAuthenticationOutInterceptor(String phase) {
        super(phase);
    }

    public AbstractAuthenticationOutInterceptor() {
        this(Phase.PRE_PROTOCOL);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        if (isTokenManagerRequest(message)) {
            // For a token manager request, get user name, password  and add a basic authentication header
            populateAuthenticationInfo(message);
        } else {
            // For all other services, get a spectrum security token & add it to message header

            // Figure out if token has to be created forcefully.
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.
                    currentRequestAttributes()).getRequest();

            boolean forceTokenCreation = request.getAttribute(SPECTRUM_TOKEN_CREATE) != null ?
                    (boolean) request.getAttribute(SPECTRUM_TOKEN_CREATE) : false;

            // Get & add header.
            Map<String, List<String>> jwtHeaders = jwtAuthorizationHandler.getJWTHeaders(forceTokenCreation);
            Map<String, List<?>> headers = (Map<String, List<?>>) message.get(Message.PROTOCOL_HEADERS);
            headers.putAll(jwtHeaders);
        }
    }

    /*
    * @param headers request headers that will be modified by adding 'Authorization' header
    * This method will be implemented by child classes to decide after handling strategy.
    */

    public abstract void populateAuthenticationInfo(Message message);

    protected void addBasicAuthenticationHeader(Message message) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String username = request.getParameter(usernameParameter) != null ?
                request.getParameter(usernameParameter) : (String) request.getAttribute(usernameParameter);
        String password = request.getParameter(passwordParameter) != null ?
                request.getParameter(passwordParameter) : (String) request.getAttribute(passwordParameter);

        if (username == null && password == null) {
            // put user/password in message if needed.
            Object uName = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Object pWord = SecurityContextHolder.getContext().getAuthentication().getCredentials();

            if (uName instanceof String && pWord instanceof String) {
                username = (String) uName;
                password = (String) pWord;
            } else {
                if (logger.isInfoEnabled()) {
                    logger.info("Invalid Authentication found.");
                }
            }
        }

        addBasicAuthenticationHeader(message, username, password);
    }

    /**
     * Adds basic authentication header to http request
     */

    protected void addBasicAuthenticationHeader(Message message, String username, String password) {
        Map<String, List<?>> headers = (Map<String, List<?>>) message.get(Message.PROTOCOL_HEADERS);
        if (username != null && password != null) {
            String authString = username + ":" + password;
            headers.put(BASIC_AUTHENTICATION_HEADER, Collections.singletonList("Basic " + new String(Base64.encode(authString.getBytes()))));
        } else {
            // throw exception.
            throw new RuntimeException("You are not authorized, user name & password not found.");
        }
    }

    protected boolean isTokenManagerRequest(Message message) {
        if (message != null) {
            if (message.get(CXF_ENDPOINT_PARAM).toString().contains(TM_ENDPOINT_NAME) &&
                    message.get("java.lang.reflect.Method").toString().contains("TokenManagerService.getAccessExpiringToken")) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
