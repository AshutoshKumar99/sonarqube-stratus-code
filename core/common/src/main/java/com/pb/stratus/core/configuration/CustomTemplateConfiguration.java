package com.pb.stratus.core.configuration;

import java.io.File;
import java.nio.file.Paths;

/**
 * It provides the details for various customer template related configurations.
 * Created by gu003du on 07-Feb-18.
 */
public class CustomTemplateConfiguration {
    public static final String BASE_TEMPLATE_TS_FTL = "base-custom-template-ts.ftl";
    public static final String BASE_TEMPLATE_HTML_FTL = "base-custom-template-html.ftl";
    public static final String RELATIVE_CUSTOM_TEMPLATE_DIR = "customerconfigurations/analyst/theme/infotemplates";
    private static final String CUSTOM_TEMPLATE_DIR = "analyst/theme/infotemplates";
    private static final String TEMPLATES_DIR = "analyst/theme/templatecomponents";

    private CustomerConfigDirHolder configDirHolder;

    /**
     * Constructor for template configuration.
     *
     * @param configDirHolder customer configuration holder instance.
     */
    public CustomTemplateConfiguration(CustomerConfigDirHolder configDirHolder) {
        this.configDirHolder = configDirHolder;
    }

    /**
     * Get a file instance for directory where custom templates are to be stored.
     *
     * @return
     */
    public File getCustomTemplateDirectory() {
        return Paths.get(configDirHolder.getCustomerConfigDir().toString(),
                CUSTOM_TEMPLATE_DIR).toFile();
    }

    /**
     * Get a file instance  for directory where base template for generating custom template is stored.
     *
     * @return
     */
    public File getBaseTemplateDirectory() {
        return Paths.get(configDirHolder.getCustomerConfigDir().toString(),
                TEMPLATES_DIR).toFile();
    }
}
