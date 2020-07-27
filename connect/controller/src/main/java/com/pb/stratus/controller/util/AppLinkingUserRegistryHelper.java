package com.pb.stratus.controller.util;

import com.pb.stratus.core.configuration.ConfigReader;
import com.pb.stratus.core.exception.ParseException;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;

/**
 * Created by AW001AG on 6/24/2015.
 */
public class AppLinkingUserRegistryHelper {
    private static final String REGISTERED_APPLINKING_USERS_FILE_STORE = "RegisterApplinkingUsersFileStore.txt";

    public static boolean isApplinkingUserRegistered(ConfigReader configReader, String uniqueBrowserFingerPrint) {
        try {
            //No Caching for File data here as Applinking Configuration is cached on client side(dojo).
            //There will be only one call per user session to this Action
            InputStream inputStream = configReader.getConfigFile(REGISTERED_APPLINKING_USERS_FILE_STORE);
            String registeredApplinkingUsersList = IOUtils.toString(inputStream, "UTF-8");

            return (null != uniqueBrowserFingerPrint) &&
                    (registeredApplinkingUsersList.indexOf(uniqueBrowserFingerPrint) != -1) ? true : false;
        } catch (Exception exception) {
            throw new ParseException(exception);
        }
    }
}
