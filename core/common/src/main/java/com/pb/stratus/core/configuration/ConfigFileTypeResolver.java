package com.pb.stratus.core.configuration;

/**
 * Resolves a file name to a relative path within the customerconfigurations 
 * directory structure. To be agnostic of the underlying storage mechanism, 
 * a forward slash is used as a separator, instead of a platform-dependant file
 * separator.
 */
public class ConfigFileTypeResolver
{
    
    public String resolve(String fileName, ConfigFileType type)
    {
        StringBuilder b = new StringBuilder(type.getPath());
        b.append("/");
        b.append(fileName);
        if (fileName.indexOf(".") < 0)
        {
            b.append(type.getExtension());
        }
        return b.toString();
    }
    
}
