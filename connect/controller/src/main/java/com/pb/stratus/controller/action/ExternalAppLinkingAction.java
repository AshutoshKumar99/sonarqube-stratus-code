package com.pb.stratus.controller.action;

import com.pb.stratus.controller.InvalidGazetteerException;
import com.pb.stratus.controller.util.AppLinkingUserRegistryHelper;
import com.pb.stratus.core.configuration.ConfigReader;
import com.pb.stratus.core.exception.ParseException;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by vi012gu on 5/13/2015.
 */
public class ExternalAppLinkingAction extends DataInterchangeFormatControllerAction
{
    private ConfigReader configReader;
    private String tenantName;
    private String TENANT_NAME_ON_PRIMISE = "analyst";

    private static final Logger logger = LogManager
                .getLogger(RegisterAppLinkingUserAction.class);

    public ExternalAppLinkingAction(ConfigReader configReader, String tenantName ) {
        super();
        this.configReader = configReader;
        this.tenantName = tenantName;

    }
    @Override
    protected Object createObject(HttpServletRequest request) throws ServletException, IOException, InvalidGazetteerException {


        JSONObject appLinkingInfo = new JSONObject();
        String uniqueBrowserFingerPrint  = request.getParameter("uniqueBrowserFingerPrint");
        logger.info("Getting information for applinking enablement with incoming finger print " +
                "value as " +uniqueBrowserFingerPrint);

        Boolean enabledInConfig = Boolean.parseBoolean(getConfig().getApplicationLinkingEnabled());
        Boolean shortCircuitAppLinkingRegistration = getConfig().isApplicationLinkingRegistrationShortCircuited();
        boolean userRegistered = false;
        if(enabledInConfig && TENANT_NAME_ON_PRIMISE.equalsIgnoreCase(tenantName))
        {
          userRegistered  = AppLinkingUserRegistryHelper.isApplinkingUserRegistered(configReader,
                    uniqueBrowserFingerPrint);
        }

        appLinkingInfo.put("appLinkingEnabled", Boolean.toString(enabledInConfig &&
                (userRegistered || shortCircuitAppLinkingRegistration)));
        //This flag is used to show Applinking Registration link
        appLinkingInfo.put("appLinkingHostIP", getConfig().getApplicationLinkingHostIP());
        appLinkingInfo.put("appLinkingHostPort", getConfig().getApplicationLinkingHostPort());
        appLinkingInfo.put("registrationId", uniqueBrowserFingerPrint);
        logger.info("Getting information for applinking enablement with incoming finger print " +
                "value as " +uniqueBrowserFingerPrint, appLinkingInfo);
        return appLinkingInfo;
    }
}
