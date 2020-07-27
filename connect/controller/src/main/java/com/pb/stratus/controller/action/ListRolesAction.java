package com.pb.stratus.controller.action;

import com.pb.spectrum.platform.server.config.core.ws.productapi.impl.SecurityProductServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.pb.stratus.controller.Constants;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * This action is used to fetch all the roles present in the system.
 * In analyst we show only those roles which starts from 'Analyst'
 * @author ra005ja
 */
public class ListRolesAction extends DataInterchangeFormatControllerAction {

    private final static Logger logger = LogManager.getLogger(ListRolesAction.class);

    private SecurityProductServiceImpl securityProductService;

    public ListRolesAction(SecurityProductServiceImpl securityProductService) {
        this.securityProductService = securityProductService;
    }

    protected Object createObject(HttpServletRequest request)
            throws ServletException, IOException {

        Set<String> roles = new HashSet<String>();
        try {
            List<String> systemRoles = securityProductService.listRoles();
            //Apart from the admin Role, Analyst will be listing only those roles that start with the String Analyst
            for (String roleValue : systemRoles) {
                if((roleValue.equals(Constants.ADMIN)) ||
                        (roleValue.startsWith(Constants.VALID_ANALYST_ROLE_PREFIX)))
                {
                    roles.add(roleValue);
                }
            }
            logger.debug("Roles found in the System : "+ roles.size());
        } catch (Exception ex) {
            logger.debug(ex.getStackTrace());
        }
        return roles;
    }

}
