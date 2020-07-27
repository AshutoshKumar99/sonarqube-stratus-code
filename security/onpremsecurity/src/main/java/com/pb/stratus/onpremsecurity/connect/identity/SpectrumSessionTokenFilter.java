package com.pb.stratus.onpremsecurity.connect.identity;

import com.pb.stratus.onpremsecurity.Constants;
import com.pb.stratus.onpremsecurity.token.SpectrumSessionTokenHolder;
import com.pb.stratus.onpremsecurity.token.SpectrumToken;
import org.apache.log4j.Logger;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;


/**
 * This filter extracts the session token and saves it into session.
 * Created by gu003du on 16-Oct-18.
 */
public class SpectrumSessionTokenFilter extends GenericFilterBean {

    private static final Logger logger = Logger.getLogger(SpectrumSessionTokenFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        // Get Spectrum session token from request
        String userId = request.getParameter(Constants.SSO_USER_NAME);
        String accessToken = request.getParameter(Constants.SSO_ACCESS_TOKEN);
        String sessionId = request.getParameter(Constants.SSO_SESSION);

        // Add validations, if validation successful add token to Session.
        if (userId != null && accessToken != null && sessionId != null) {
            // All three expected values found in request
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            if (session != null) {
                SpectrumToken spectrumToken = new SpectrumToken();
                spectrumToken.setSession(sessionId);
                spectrumToken.setAccessToken(accessToken);
                spectrumToken.setUserId(userId);
                SpectrumSessionTokenHolder tokenHolder = new SpectrumSessionTokenHolder(spectrumToken);
                session.setAttribute(SpectrumSessionTokenHolder.class.getName(), tokenHolder);
                chain.doFilter(request, response);
            } else {
                throw new RuntimeException("HTTP Session doesn't exist.");
            }
        } else if (!(userId == null && accessToken == null && sessionId == null)) {
            // If all three values are not there, then there is no token in the request; let it pass through.
            /// Only one value is found, its an error
            StringBuilder builder = new StringBuilder();
            builder.append("These mandatory parameters are missing in request: ");
            if (userId == null) {
                builder.append(Constants.SSO_USER_NAME + " ");
            }
            if (accessToken == null) {
                builder.append(Constants.SSO_ACCESS_TOKEN + " ");
            }
            if (sessionId == null) {
                builder.append(Constants.SSO_SESSION);
            }
            throw new IllegalArgumentException(builder.toString());
        } else {
            chain.doFilter(request, response);
        }
    }
}
