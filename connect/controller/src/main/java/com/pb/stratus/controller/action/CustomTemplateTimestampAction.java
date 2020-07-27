package com.pb.stratus.controller.action;

import com.pb.stratus.controller.Constants;
import com.pb.stratus.controller.InvalidGazetteerException;
import com.pb.stratus.controller.exception.CustomTemplateException;
import com.pb.stratus.controller.service.CustomTemplateService;
import com.pb.stratus.security.core.util.AuthorizationUtils;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * This action is responsible for checking if custom template files has been manually edited.
 * It basically checks if the last modified timestamps(in seconds) of json file is different than the other two html and ts files.
 * Created by su019ch on 5/21/2018.
 */
public class CustomTemplateTimestampAction extends DataInterchangeFormatControllerAction{

    private final static Logger logger = LogManager.getLogger(CustomTemplateTimestampAction.class);

    private CustomTemplateService customTemplateService;
    private AuthorizationUtils authzUtils;

    public CustomTemplateTimestampAction(CustomTemplateService customTemplateService,
                                         AuthorizationUtils authzUtils) {
        this.customTemplateService = customTemplateService;
        this.authzUtils = authzUtils;
    }

    @Override
    protected Object createObject(HttpServletRequest request) throws ServletException, IOException, InvalidGazetteerException {
        if (!authzUtils.isAdminUser()) {
            CustomTemplateException e = new CustomTemplateException(Constants.FAILED_TO_CREATE_TEMPLATE);
            logger.error(Constants.NOT_AUTHORIZED, e);
            throw e;
        }

        String templateName = request.getParameter(Constants.TEMPLATE_NAME);
        boolean isTemplateModified = this.customTemplateService.isTemplateManuallyEdited(templateName);

        JSONObject templateTimestampInfo = new JSONObject();
        templateTimestampInfo.put("isTemplateManuallyEdited", Boolean.toString(isTemplateModified));

        return templateTimestampInfo;
    }
}
