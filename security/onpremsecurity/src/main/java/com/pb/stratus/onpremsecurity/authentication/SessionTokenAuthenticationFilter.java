package com.pb.stratus.onpremsecurity.authentication;

import com.pb.stratus.onpremsecurity.Constants;
import com.pb.stratus.onpremsecurity.authentication.token.SpectrumSessionAuthenticationToken;
import com.pb.stratus.onpremsecurity.token.SpectrumSessionTokenHolder;
import com.pb.stratus.onpremsecurity.token.SpectrumToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * This filter intercepts the first controller request for checking if extensibility is enabled
 * i.e. '/controller/checkExtensibility' and attempts to authenticate only if Session contains
 * a SpectrumSessionTokenHolder instance. This instance is removed from session when
 * token authentication is complete first time.
 * Created by gu003du on 22-Oct-18.
 */
public class SessionTokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public SessionTokenAuthenticationFilter() {
        super(Constants.SECURITY_CHECK);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

        HttpSession session = request.getSession(false);
        SpectrumSessionTokenHolder tokenHolder = (SpectrumSessionTokenHolder) session.getAttribute(SpectrumSessionTokenHolder.class.getName());
        if (tokenHolder != null) {
            SpectrumToken token = tokenHolder.getToken();
            SpectrumSessionAuthenticationToken authRequest = new SpectrumSessionAuthenticationToken(token);
            // Allow subclasses to set the "details" property
            setDetails(request, authRequest);
            return this.getAuthenticationManager().authenticate(authRequest);
        } else {
            throw new AuthenticationCredentialsNotFoundException("Token information not found in incoming  request.");
        }

    }

    /**
     * Provided so that subclasses may configure what is put into the authentication request's details
     * property.
     *
     * @param request     that an authentication request is being created for
     * @param authRequest the authentication request object that should have its details set
     */
    protected void setDetails(HttpServletRequest request, SpectrumSessionAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }
}
