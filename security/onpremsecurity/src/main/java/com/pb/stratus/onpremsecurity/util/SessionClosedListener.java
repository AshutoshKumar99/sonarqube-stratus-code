package com.pb.stratus.onpremsecurity.util;

import com.pb.stratus.core.common.application.SpringApplicationContextLocator;
import com.pb.stratus.onpremsecurity.Constants;
import com.pb.stratus.onpremsecurity.authentication.SpectrumTokenService;
import com.pb.stratus.onpremsecurity.token.SpectrumToken;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.pb.spectrum.platform.server.common.security.token.TokenLogoutService;

/**
 * Created by RA007GI on 8/23/2016.
 */
public class SessionClosedListener implements
        ApplicationListener<HttpSessionDestroyedEvent> {

    private static final Logger logger = Logger.getLogger(SessionClosedListener.class);

    private SpectrumTokenService spectrumTokenService;
    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setSpectrumTokenService(SpectrumTokenService spectrumTokenService) {
        this.spectrumTokenService = spectrumTokenService;
    }

    @Override
    public void onApplicationEvent(
            HttpSessionDestroyedEvent httpSessionDestroyedEvent) {

        if (this.applicationContext == null) {
            this.applicationContext = SpringApplicationContextLocator.getApplicationContext();
        }
        if (this.spectrumTokenService == null) {
            this.spectrumTokenService = (SpectrumTokenService) applicationContext.getBean("spectrumTokenService");
        }
        HttpSession session = httpSessionDestroyedEvent.getSession();
        SpectrumToken token = null;
        if (session.getAttribute(Constants.SPECTRUM_TOKEN) != null) {
            token = (SpectrumToken) session.getAttribute(Constants.SPECTRUM_TOKEN);
        }

        if (token != null && token.getUserId() != null) {
            try {
                this.spectrumTokenService.logout();
            } catch (Exception e) {
               logger.error(e);
            }
        }
    }
}
