package com.pb.stratus.core.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


/**
 * This is a helper class for reading FileSystemBased Files
 * 
 */
public class FileSystemConfigReader implements ConfigReader
{
    
    private ConfigFileTypeResolver resolver;
    
    private String basePath;

	private String customerName;
    
    public FileSystemConfigReader(String basePath, String customerName)
    {
        this(new ConfigFileTypeResolver(), basePath, customerName);
    }

    protected FileSystemConfigReader(ConfigFileTypeResolver fileTypeResolver, 
            String basePath, String customerName)
    {
        this.resolver = fileTypeResolver;
        this.basePath = basePath;
        this.customerName = customerName;
    }
    
    public String getBasePath() {
		return basePath;
	}

    public String getCustomerName() {
		return customerName;
	}
    
    public InputStream getConfigFile(String filePath)
            throws FileNotFoundException
    {
        StringBuilder b = createStringBuilderWithBasePath();
        b.append(filePath.replace("/", File.separator));
        return createInputStream(b.toString());
    }

	public InputStream getConfigFile(String fileName, ConfigFileType type)
            throws FileNotFoundException
    {
        StringBuilder b = createStringBuilderWithBasePath();
        String resolvedPath = resolver.resolve(fileName, type);
        b.append(resolvedPath.replace("/", File.separator));
        return createInputStream(b.toString());
    }
    
    protected StringBuilder createStringBuilderWithBasePath()
    {
        StringBuilder b = new StringBuilder(basePath);
        b.append(File.separator);
        b.append(customerName);
        b.append(File.separator);
        return b;
    }
    
    protected InputStream createInputStream(String path) 
            throws FileNotFoundException
    {
        return new FileInputStream(path); 
    }
    
}