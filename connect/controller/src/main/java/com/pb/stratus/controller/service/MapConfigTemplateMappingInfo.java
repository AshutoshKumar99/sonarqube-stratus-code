package com.pb.stratus.controller.service;

import java.util.ArrayList;

/**
 * Created by AN023SH on 9/7/2018.
 */
public class MapConfigTemplateMappingInfo {
    private String projectName;
    private ArrayList<tableTemplate> tableTemplate;
    public MapConfigTemplateMappingInfo(){}

    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String name) {
        this.projectName = name;
    }
    public ArrayList<tableTemplate> gettableTemplateArray() {
        return tableTemplate;
    }
}
class tableTemplate{
    private String tableName;
    private String templateName;
    public tableTemplate(){

    }
    public String getTableName() {
        return tableName;
    }
    public void setTableName(String name) {
        this.tableName = name;
    }
    public String getTemplateName() {
        return templateName;
    }
    public void setTemplateName(String name) {
        this.templateName = name;
    }
}
