package com.pb.stratus.onpremsecurity.authentication;

import com.pb.spectrum.platform.server.config.core.ws.productapi.impl.SecurityProductServiceImpl;
import com.pb.stratus.onpremsecurity.Constants;
import com.pb.stratus.onpremsecurity.authentication.token.SpectrumSessionAuthenticationToken;
import com.pb.stratus.onpremsecurity.token.SpectrumSessionTokenHolder;
import com.pb.stratus.onpremsecurity.token.SpectrumToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gu003du on 19-Oct-18.
 */
public class SessionTokenAuthenticationProvider implements AuthenticationProvider {

    private SecurityProductServiceImpl securityProductService;

    public SessionTokenAuthenticationProvider(SecurityProductServiceImpl securityProductService) {
        this.securityProductService = securityProductService;
    }

    /**
     * Performs authentication with the same contract as {@link
     * org.springframework.security.authentication.AuthenticationManager#authenticate(Authentication)}.
     *
     * @param authentication the authentication request object.
     * @return a fully authenticated object including credentials.
     * @throws AuthenticationException if authentication fails.
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SpectrumSessionAuthenticationToken authenticationToken = null;
        // Validate if session already has Spectrum token and need to be authenticated.
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(false);
        SpectrumSessionTokenHolder tokenHolder = (SpectrumSessionTokenHolder) session.
                getAttribute(SpectrumSessionTokenHolder.class.getName());
        if (tokenHolder != null) {
            SpectrumToken token = tokenHolder.getToken();
            // set the token in session as expected by existing JWTAuthentication Handler.
            // It is then picked by every service call from session.
            session.setAttribute(Constants.SPECTRUM_TOKEN, token);
            List<String> roles = this.securityProductService.listRolesForCurrentUser();
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            for (String role : roles) {
                grantedAuthorities.add(new SimpleGrantedAuthority(role));
            }
            authenticationToken = new SpectrumSessionAuthenticationToken(token.getUserId(),
                    token, grantedAuthorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // remove the token holder now so that authentication is not done every time for this session.
            session.removeAttribute(SpectrumSessionTokenHolder.class.getName());
        }

        return authenticationToken;
    }

    /**
     * * Returns <code>true</code> if this <Code>AuthenticationProvider</code> supports the indicated
     * <Code>Authentication</code> object.
     *
     * @param authentication
     * @return
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.getName().equals(SpectrumSessionAuthenticationToken.class.getName());
    }
}
