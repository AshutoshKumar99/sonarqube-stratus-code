package com.pb.stratus.controller.action;

import com.pb.stratus.controller.InvalidGazetteerException;
import com.pb.stratus.controller.util.AppLinkingUserRegistryHelper;
import com.pb.stratus.core.configuration.ConfigReader;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by aw001ag on 12/26/2017.
 */
public class AnalystExtensionEnabledAction extends DataInterchangeFormatControllerAction
{
    private ConfigReader configReader;
    private String tenantName;
    private String TENANT_NAME_ON_PRIMISE = "analyst";

    private static final Logger logger = LogManager
                .getLogger(AnalystExtensionEnabledAction.class);

    public AnalystExtensionEnabledAction(ConfigReader configReader, String tenantName) {
        super();
        this.configReader = configReader;
        this.tenantName = tenantName;

    }
    @Override
    protected Object createObject(HttpServletRequest request) throws ServletException, IOException, InvalidGazetteerException {


        JSONObject extensibilityEnabledInfo = new JSONObject();
//        tenantDataBindingEnabled = Boolean.parseBoolean(getPropertyValue(PROP_FILE_SHARED, PROP_KEY_DATA_BINDING));

        Boolean extensibilityEnabledInConfig = Boolean.parseBoolean(getConfig().getExtensibilityEnabled());

        extensibilityEnabledInfo.put("extensibilityEnabled", Boolean.toString(extensibilityEnabledInConfig));

        return extensibilityEnabledInfo;
    }
}
