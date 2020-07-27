package com.pb.stratus.onpremsecurity.authentication;

import com.pb.stratus.onpremsecurity.util.JWTAuthorizationHandler;
import com.pb.stratus.security.core.authentication.AuthenticationFailureException;
import com.pb.spectrum.platform.server.config.core.ws.productapi.impl.SecurityProductServiceImpl;
import com.pb.stratus.security.core.authentication.IAuthenticator;
import com.pb.stratus.security.core.jaxb.Role;
import com.pb.stratus.security.core.jaxb.User;
import org.apache.cxf.transport.http.HTTPException;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;

import javax.xml.ws.WebServiceException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ma050si
 */
public class AnalystAuthenticatorImpl implements IAuthenticator {

    private static final Logger logger = org.apache.log4j.Logger.getLogger(AnalystAuthenticatorImpl.class);
    public static final String SPECTRUM_TOKEN = "SpectrumToken";
    private SecurityProductServiceImpl securityProductService;

    public void setSecurityProductService(SecurityProductServiceImpl securityProductService) {
        this.securityProductService = securityProductService;
    }

    private JWTAuthorizationHandler jwtAuthorizationHandler;

    public void setJwtAuthorizationHandler(JWTAuthorizationHandler jwtAuthorizationHandler) {
        this.jwtAuthorizationHandler = jwtAuthorizationHandler;
    }

    public AnalystAuthenticatorImpl() {
    }


    /**
     * Override the login functionality of IAnalystAuthenticator.
     * It will call securityProductService to get the roles for the user.
     *
     * @param userName - String
     * @param password - String
     * @return - a list of roles on a particular user.
     * @throws AuthenticationFailureException - on failing the authentication.
     */
    @Override
    public User login(String userName, String password) throws AuthenticationFailureException {
        User user = new User();
        List<String> roles = new ArrayList<String>();
        try {
            jwtAuthorizationHandler.getSpectrumToken(true);
            roles = this.securityProductService.listRolesForCurrentUser();
        } catch (WebServiceException ex) {

            if (ex.getCause() instanceof HTTPException) {

                HTTPException e = (HTTPException) ex.getCause();

                if (e.getResponseCode() == HttpStatus.UNAUTHORIZED.value()) {
                    logger.debug("AuthenticationFailureException authentication failed");
                    throw new AuthenticationFailureException(INVALID_CREDENTIALS);
                }
            }
            logger.error("AuthenticationFailureException authentication failed due to ServiceException");
            throw new AuthenticationFailureException(SERVICE_ERROR);

        } catch (Exception e) {
            logger.error("Service error occurred while Authentication");
            logger.error(e);
            throw new AuthenticationFailureException(SERVICE_ERROR);
        }

        for (String role : roles) {
            Role singleRole = new Role();
            singleRole.setName(role);
            user.getRoles().add(singleRole);
        }
        user.setDisplayName(userName);
        user.setUserName(userName);
        user.setPassword(password);
        return user;
    }


}
