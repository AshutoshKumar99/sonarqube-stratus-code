package com.pb.stratus.onpremsecurity.authentication.token;

import com.pb.stratus.onpremsecurity.token.SpectrumToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * An {@link org.springframework.security.core.Authentication} implementation that is designed for simple presentation
 * of Spectrum Session Token.
 * <p/>
 * The <code>principal</code> and <code>credentials</code> should be set with an <code>Object</code> that provides
 * the respective property via its <code>Object.toString()</code> method. The simplest such <code>Object</code> to use
 * is <code>String</code>.
 * Created by gu003du on 19-Oct-18.
 */
public class SpectrumSessionAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private SpectrumToken credentials;


    public SpectrumSessionAuthenticationToken(SpectrumToken credentials) {
        this(credentials.getUserId(), credentials);
    }

    /**
     * This constructor can be safely used by any code that wishes to create a
     * <code>UsernamePasswordAuthenticationToken</code>, as the {@link
     * #isAuthenticated()} will return <code>false</code>.
     */
    public SpectrumSessionAuthenticationToken(Object principal, SpectrumToken credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(false);
    }

    /**
     * This constructor should only be used by <code>AuthenticationManager</code> or <code>AuthenticationProvider</code>
     * implementations that are satisfied with producing a trusted (i.e. {@link #isAuthenticated()} = <code>true</code>)
     * authentication token.
     *
     * @param principal
     * @param credentials
     * @param authorities
     */
    public SpectrumSessionAuthenticationToken(Object principal, SpectrumToken credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true); // must use super, as we override
    }

    public Object getCredentials() {
        return this.credentials;
    }

    public Object getPrincipal() {
        return this.principal;
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException(
                    "Once created you cannot set this token to authenticated. Create a new instance using the constructor which takes a" +
                            " GrantedAuthority list will mark this as authenticated.");
        }

        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        credentials = null;
    }
}
