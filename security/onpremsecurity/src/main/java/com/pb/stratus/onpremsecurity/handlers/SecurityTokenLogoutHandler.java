package com.pb.stratus.onpremsecurity.handlers;

import com.pb.stratus.onpremsecurity.authentication.SpectrumTokenService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by GU003DU on 13-Nov-18.
 */
public class SecurityTokenLogoutHandler implements LogoutHandler {

    private SpectrumTokenService spectrumTokenService;

    public SecurityTokenLogoutHandler(SpectrumTokenService spectrumTokenService) {
        this.spectrumTokenService = spectrumTokenService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            spectrumTokenService.logout();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
