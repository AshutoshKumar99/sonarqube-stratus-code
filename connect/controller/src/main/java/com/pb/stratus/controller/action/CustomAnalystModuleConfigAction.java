package com.pb.stratus.controller.action;

import com.pb.stratus.controller.InvalidGazetteerException;
import com.pb.stratus.controller.util.AppLinkingUserRegistryHelper;
import com.pb.stratus.core.configuration.ConfigReader;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.sf.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by AW001AG on 6/13/2017.
 */
public class CustomAnalystModuleConfigAction extends DataInterchangeFormatControllerAction {
    private ConfigReader configReader;
    private String tenantName;

    private static final Logger logger = LogManager
            .getLogger(CustomAnalystModuleConfigAction.class);

    public CustomAnalystModuleConfigAction(ConfigReader configReader, String tenantName) {
        super();
        this.configReader = configReader;
        this.tenantName = tenantName;
    }

    @Override
    protected Object createObject(HttpServletRequest request) throws ServletException, IOException, InvalidGazetteerException {
        JSONObject customModuleConfigInfo = null;
        InputStream inputStream = null;
        try {
            inputStream = configReader.getConfigFile("CustomAnalystModuleConfig.json");
            String customAnalystModuleConfigString = IOUtils.toString(inputStream, "UTF-8");
            customModuleConfigInfo = JSONObject.fromObject(customAnalystModuleConfigString);
        } finally {
            // close the input stream.
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return customModuleConfigInfo;
    }
}
