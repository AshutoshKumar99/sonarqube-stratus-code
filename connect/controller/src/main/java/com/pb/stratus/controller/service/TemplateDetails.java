package com.pb.stratus.controller.service;

/**
 * Created by su019ch on 5/17/2018.
 * This is a simple pojo class for TemplateDetails against a tableName
 * which provides mapping between tableName and custom templates
 */
public class TemplateDetails {
    private String templateName;
    private long timeStamp;
    private boolean isDefault;

    public TemplateDetails(String templateName, long timeStamp, boolean isDefault){
        this.templateName = templateName;
        this.timeStamp = timeStamp;
        this.isDefault = isDefault;
    }

    public String getTemplateName() {
        return templateName;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}
