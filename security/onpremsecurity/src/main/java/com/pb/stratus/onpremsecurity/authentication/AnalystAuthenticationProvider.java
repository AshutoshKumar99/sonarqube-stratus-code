package com.pb.stratus.onpremsecurity.authentication;

import com.pb.stratus.security.core.authentication.AuthenticationFailureException;
import com.pb.stratus.security.core.authentication.IAuthenticator;
import com.pb.stratus.security.core.jaxb.Role;
import com.pb.stratus.security.core.jaxb.User;
import org.apache.log4j.Logger;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Indicates a class can process a specific  custom {@link
 * org.springframework.security.core.Authentication} implementation.
 *
 * Created with IntelliJ IDEA.
 * User: ma050si
 */
public class AnalystAuthenticationProvider implements org.springframework.security.authentication.AuthenticationProvider {

    private static final Logger logger = org.apache.log4j.Logger.getLogger(AnalystAuthenticationProvider.class);

    private IAuthenticator analystAuthenticator;

    public AnalystAuthenticationProvider(IAuthenticator analystAuthenticator) {
        this.analystAuthenticator = analystAuthenticator;
    }

    /**
     * Performs authentication with the same contract as {@link
     * org.springframework.security.authentication.AuthenticationManager#authenticate(Authentication)}.
     *
     * @param authentication the authentication request object.
     *
     * @return a fully authenticated object including credentials.
     *
     * @throws AuthenticationException if authentication fails.
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) authentication;
        String username = String.valueOf(usernamePasswordAuthenticationToken.getPrincipal());
        String password = String.valueOf(usernamePasswordAuthenticationToken.getCredentials());
        User userDetails = null;
        List<GrantedAuthority> grantedAuthorities = null;
        try {
            userDetails = analystAuthenticator.login(username, password);
            grantedAuthorities = new ArrayList<>();
            for(Role role : userDetails.getRoles()){
                grantedAuthorities.add(new GrantedAuthorityImpl(role.getName()));
            }
            usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails.getUserName(),
                    userDetails.getPassword(), grantedAuthorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AuthenticationFailureException e) {
            if(IAuthenticator.INVALID_CREDENTIALS.equals(e.getMessage())){
                throw new BadCredentialsException("Bad Credentials");
            }else if(IAuthenticator.SERVICE_ERROR.equals(e.getMessage())){
                throw new AuthenticationServiceException("Service Exception");
            }
        }
        return usernamePasswordAuthenticationToken;
    }

    /**
     * * Returns <code>true</code> if this <Code>AuthenticationProvider</code> supports the indicated
     * <Code>Authentication</code> object.
     *
     * @param authentication
     * @return
     */
    @Override
    public boolean supports(Class<? extends Object> authentication) {
        return  authentication.getName().equals(UsernamePasswordAuthenticationToken.class.getName());
    }
}
