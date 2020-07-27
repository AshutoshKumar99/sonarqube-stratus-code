package com.pb.stratus.controller.action;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pb.stratus.controller.Constants;
import com.pb.stratus.controller.InvalidGazetteerException;
import com.pb.stratus.controller.exception.CustomTemplateException;
import com.pb.stratus.controller.service.CustomTemplateService;
import com.pb.stratus.controller.service.CustomTemplateTableMappingService;
import com.pb.stratus.controller.service.FeatureEditTemplateMappingService;
import com.pb.stratus.controller.service.MapConfigTemplateMappingInfo;
import com.pb.stratus.core.configuration.ConfigReader;
import com.pb.stratus.core.configuration.CustomerConfigDirHolder;
import com.pb.stratus.security.core.util.AuthorizationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This action is responsible for update and write table template mapping in CalloutInfoTemplateDefinitions xml and tableTemplateMappings json
 * Created by ju002ah on 5/26/2018.
 */
public class CustomTemplateTableMappingAction extends DataInterchangeFormatControllerAction {

    private String tenantName;
    private CustomerConfigDirHolder configDirHolder;
    private final static Logger logger = LogManager.getLogger(CustomTemplateTimestampAction.class);
    private AuthorizationUtils authzUtils;
    private ConfigReader configReader;
    private CustomTemplateService customTemplateService;
    private CustomTemplateTableMappingService customTemplateTableMappingService;
    private FeatureEditTemplateMappingService featureEditTemplateMappingService;

    public CustomTemplateTableMappingAction(CustomTemplateService customTemplateService, AuthorizationUtils authzUtils, CustomTemplateTableMappingService customTemplateTableMappingService,FeatureEditTemplateMappingService featureEditTemplateMappingService ) {
        this.authzUtils = authzUtils;
        this.customTemplateService = customTemplateService;
        this.customTemplateTableMappingService = customTemplateTableMappingService;
        this.featureEditTemplateMappingService = featureEditTemplateMappingService;
    }

    @Override
    protected Object createObject(HttpServletRequest request) throws ServletException, InvalidGazetteerException, IOException {
        if (!authzUtils.isAdminUser()) {
            CustomTemplateException e = new CustomTemplateException(Constants.FAILED_TO_CREATE_TEMPLATE);
            logger.error(Constants.NOT_AUTHORIZED, e);
            throw e;
        }
        String templateName = request.getParameter(Constants.TEMPLATE_NAME);
        String tableName = request.getParameter(Constants.TABLE_REF);
        boolean isDefault = new Boolean(request.getParameter(Constants.IS_DEFAULT));
        String jsonString = request.getParameter(Constants.MAPCONFIG_TABLE_TEMP_MAPPING_PARAM);
        String jsonStringfeatureEdit = request.getParameter(Constants.FEATURE_EDIT_MAPPING_PARAM);
        MapConfigTemplateMappingInfo mapConfigTemplateMappingInfo = new MapConfigTemplateMappingInfo();
        MapConfigTemplateMappingInfo featureEditTemplateMappingInfo = new MapConfigTemplateMappingInfo();
        if(!jsonString.equals(Constants.STRING_UNDEFINED)) {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            mapConfigTemplateMappingInfo = gson.fromJson(jsonString, MapConfigTemplateMappingInfo.class);
        }
        if(!jsonStringfeatureEdit.equals(Constants.STRING_UNDEFINED)) {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            featureEditTemplateMappingInfo = gson.fromJson(jsonStringfeatureEdit, MapConfigTemplateMappingInfo.class);
        }
        Map response = new HashMap<>();

        try {
            if(mapConfigTemplateMappingInfo.getProjectName()!= null){
                this.featureEditTemplateMappingService.updateTemplateMapConfigTableMappingXML(featureEditTemplateMappingInfo);
                this.customTemplateTableMappingService.updateTemplateMapConfigTableMappingXML(mapConfigTemplateMappingInfo);
            } else{
                this.customTemplateTableMappingService.updateCustomTemplateTableMappingXML(templateName, tableName, isDefault);
                this.customTemplateService.saveTableTemplateMappingFile(tableName, templateName, isDefault, false);
                response.put(Constants.TABLE_TEMPLATE_MAPPING_FILENAME, Constants.SUCCESS);
            }

        } catch (Exception ex) {
            logger.error(Constants.FAILED_TO_UPDATE_XML, ex);
            throw new CustomTemplateException(Constants.FAILED_TO_UPDATE_XML, ex);
        }
        return response;
    }

}

