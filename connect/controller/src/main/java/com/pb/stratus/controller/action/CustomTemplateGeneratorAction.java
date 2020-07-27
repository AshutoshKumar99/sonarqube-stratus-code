package com.pb.stratus.controller.action;

import com.pb.stratus.controller.Constants;
import com.pb.stratus.controller.InvalidGazetteerException;
import com.pb.stratus.controller.exception.CustomTemplateException;
import com.pb.stratus.controller.service.CustomTemplateService;
import com.pb.stratus.core.configuration.CustomTemplateConfiguration;
import com.pb.stratus.security.core.util.AuthorizationUtils;
import freemarker.template.TemplateException;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.HashMap;
import java.util.Map;


/**
 * This action is responsible for all template designer operations. Currently it supports
 * only creation of a new custom template.
 * <p/>
 * Created by gu003du on 06-Feb-18.*
 */
public class CustomTemplateGeneratorAction extends DataInterchangeFormatControllerAction {

    private final static Logger logger = LogManager.getLogger(CustomTemplateGeneratorAction.class);

    private CustomTemplateService customTemplateService;
    private AuthorizationUtils authzUtils;

    public CustomTemplateGeneratorAction(CustomTemplateService customTemplateService,
                                         AuthorizationUtils authzUtils) {
        this.customTemplateService = customTemplateService;
        this.authzUtils = authzUtils;
    }

    @Override
    protected Object createObject(HttpServletRequest request) throws ServletException,
            IOException, InvalidGazetteerException {

        Map out = new HashMap<>();

        if (!authzUtils.isAdminUser()) {
            CustomTemplateException e = new CustomTemplateException(Constants.FAILED_TO_CREATE_TEMPLATE);
            logger.error(Constants.NOT_AUTHORIZED, e);
            throw e;
        }

        String templateName = request.getParameter(Constants.TEMPLATE_NAME);
        String templateConfig = request.getParameter(Constants.TEMPLATE_CONFIG);
        String tableName = request.getParameter(Constants.TABLE_REF);
        boolean isDefault = new Boolean(request.getParameter(Constants.IS_DEFAULT));
        boolean isOverrite = new Boolean(request.getParameter(Constants.OVERRITE));

        try {
            customTemplateService.save(templateName, templateConfig, isOverrite);
            // Save the mapping json file which maintains the mapping of templateNames against tableNames
            if(StringUtils.isNotEmpty(tableName) && StringUtils.isNotEmpty(templateName)){
                customTemplateService.saveTableTemplateMappingFile(tableName, templateName, isDefault,true);
            }
            out.put(Constants.TEMPLATE_PATH, CustomTemplateConfiguration.RELATIVE_CUSTOM_TEMPLATE_DIR);
        } catch (FileAlreadyExistsException ex) {
            logger.error(Constants.TEMPLATE_ALREADY_EXISTS_ERROR, ex);
            throw new CustomTemplateException(Constants.TEMPLATE_ALREADY_EXISTS_ERROR, ex);
        } catch (Exception ex) {
            logger.error(Constants.FAILED_TO_CREATE_TEMPLATE, ex);
            throw new CustomTemplateException(Constants.FAILED_TO_CREATE_TEMPLATE, ex);
        }
        return out;
    }
}
