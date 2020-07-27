package com.pb.stratus.controller.action;

import com.pb.stratus.controller.InvalidGazetteerException;
import com.pb.stratus.controller.util.AppLinkingUserRegistryHelper;
import com.pb.stratus.core.configuration.ConfigReader;
import com.pb.stratus.core.configuration.CustomerConfigDirHolder;
import com.pb.stratus.core.exception.ParseException;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.*;

/**
 * Created by aw001ag on 06/24/2015.
 * This class is responsible for registring user for applinking
 */
public class RegisterAppLinkingUserAction extends DataInterchangeFormatControllerAction
{
    private static final Logger logger = LogManager
            .getLogger(RegisterAppLinkingUserAction.class);
    private CustomerConfigDirHolder customerConfigDirHolder;
    private String tenantName;
    private static final String APPLINKING_USERS_REGISTRY = "RegisterApplinkingUsersFileStore.txt";

    private  ConfigReader configReader;
    public RegisterAppLinkingUserAction(ConfigReader configReader, CustomerConfigDirHolder customerConfigDirHolder, String tenantName) {
        super();
        this.customerConfigDirHolder = customerConfigDirHolder;
        this.tenantName = tenantName;
        this.configReader = configReader;
    }
    @Override
    protected Object createObject(HttpServletRequest request) throws ServletException, IOException,
            InvalidGazetteerException {

        JSONObject appLinkingInfo = new JSONObject();
        String uniqueBrowserFingerPrint = request.getParameter("uniqueBrowserFingerPrint");
        logger.debug("Calling RegisterAppLinkingUserAction with fingerPrintId " +uniqueBrowserFingerPrint);
        boolean userRegistered =
                AppLinkingUserRegistryHelper.isApplinkingUserRegistered(this.configReader,uniqueBrowserFingerPrint);
        Boolean shortCircuitAppLinkingRegistration = getConfig().isApplicationLinkingRegistrationShortCircuited();

        if(!userRegistered)
        {
            logger.debug("User not registered Registering with uniqueBrowserFingerPrint" +uniqueBrowserFingerPrint);
            userRegistered = registerUserForApplinking(uniqueBrowserFingerPrint);
        }

        Boolean enabledInConfig =  Boolean.parseBoolean(getConfig().getApplicationLinkingEnabled());
        appLinkingInfo.put("appLinkingEnabled", Boolean.toString(enabledInConfig &&
                (userRegistered || shortCircuitAppLinkingRegistration)));
        //This flag is used to show Applinking Registration link
        appLinkingInfo.put("appLinkingHostIP", getConfig().getApplicationLinkingHostIP());
        appLinkingInfo.put("appLinkingHostPort", getConfig().getApplicationLinkingHostPort());
        appLinkingInfo.put("userRegistrationId",uniqueBrowserFingerPrint);
        logger.info("User Registered with uniqueBrowserId "+
                uniqueBrowserFingerPrint + " and info " + appLinkingInfo.toString());
        return appLinkingInfo;
    }

    private boolean registerUserForApplinking(String uniqueBrowserFingerPrint)
    {
        BufferedWriter bufferWritter = null;
        FileWriter fileWriter = null;
        try {
            //No Caching for File data here as Applinking Configuration is cached on client side(dojo).
            //There will be only one call per user session to this Action
            String applinkingUserRegistryPath = customerConfigDirHolder.getCustomerConfigDir().getAbsolutePath() +
                                    File.separator + tenantName +File.separator +    APPLINKING_USERS_REGISTRY;

            logger.debug("File path and name of user Registry is" + applinkingUserRegistryPath);
            //true = append file
            fileWriter = new FileWriter(applinkingUserRegistryPath,true);
            bufferWritter = new BufferedWriter(fileWriter);
            bufferWritter.newLine();
            bufferWritter.write(uniqueBrowserFingerPrint);
            bufferWritter.close();
            logger.debug("Finishing writing registration of applinking user to registry");
            return true;
        }
        catch (Exception exception) {
            throw new ParseException(exception);
        }
        finally {
            try {

                if(bufferWritter != null)
                {
                    bufferWritter.close();
                }
                if(fileWriter != null)
                {
                    fileWriter.close();
                }
            }catch (Exception exception)
            {
             logger.error("Error while clossing RegisterApplinkingUser registry file" +
                     " finally block",exception);
            }
        }
    }
}
