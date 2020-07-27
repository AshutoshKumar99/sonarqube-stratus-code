package com.pb.stratus.onpremsecurity.adminconsole.identity;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


/**
 * Created with IntelliJ IDEA.
 * User: al021ch
 * Date: 3/22/14
 * Time: 4:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class AdminAnalystAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private AnalystSsoRedirectHelper redirectHelper;
    private MessageSource messageSource;
    private Logger logger = Logger.getLogger(AdminAnalystAuthenticationEntryPoint.class);

    public void setRedirectHelper(AnalystSsoRedirectHelper redirectHelper) {
        this.redirectHelper = redirectHelper;
    }

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res,
                         AuthenticationException authException) throws IOException, ServletException {
        //CONN-18260
        // Handle authentication failure from ajax requests as session timeout
        // TODO: We may need to do this differently in the future
        // If the request is submitted via XmlHttpRequest (ajax) then the client won't be able
        // to follow the redirect to the login server (due to same-origin security restrictions).
        // Even if they could though, if the login server needs interaction (username/password) then
        // the request still wouldn't be able to succeed because it is happening behind the scenes.
        if ("XMLHttpRequest".equals(req.getHeader("X-Requested-With"))) {
            res.sendError(450, messageSource.getMessage("sessiontimeout", null, null));
        } else {
            HttpSession session = req.getSession();
            if (logger.isInfoEnabled()) {
                logger.info("Starting a new Session");
            }
            this.redirectHelper.sendSsoRedirect(req, res);
        }
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
