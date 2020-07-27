package com.pb.stratus.core.configuration;

/**
 * A ConfigFileType represents a certain type of configuration file. This is
 * used by ConfigReader implementations to abstract from the physical location
 * of configuration files.
 */
//XXX strictly speaking, not all of these are configuration files. Perhaps
//    we should find a different name.
public enum ConfigFileType
{
    CATALOG("catalogconfig", ".xml"), 
    MAP("config", ".xml"), 
    LOCATOR("locateconfig", ".xml"),
    TENANTSETTINGS("tenant-settings", ".xml"),
    BRAND("theme", ".xml"), 
    TEMPLATE("printtemplates", ".fo"), 
    STYLE("css", ".css"), 
    IMAGE("images", ".gif"), 
    TEMPLATEICON("printtemplates", ".gif"), 
    TEMPLATECONFIG("printtemplates", ".xml"),
    DATABINDING("dataBinding", ".xml"),
    MAP_AUTHORIZATION("config", ".auth"),
    INFO_TEMPLATES("", ".xml"),
    TABLE_TEMPLATES_MAPPINGS("CalloutInfoTemplateDefinitions", ".xml"),
    FUNCTIONALITY_PROFILE("functionalityprofile", ".xml");
    private String folder;
    
    private String extension;
    
    private ConfigFileType(String folder, String extension)
    {
        this.folder = folder;
        this.extension = extension;
    }
    
    public String getPath()
    {
        return folder;
    }
    
    public String getExtension()
    {
        return extension;
    }
    
}
