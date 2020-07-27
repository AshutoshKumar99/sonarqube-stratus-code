package com.pb.stratus.security.core.resourceauthorization;

import com.pb.stratus.core.configuration.FileSystemConfigReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * User: sh003bh
 * Date: 11/11/11
 * Time: 12:28 PM
 * Default implementation for the ResourceAuthorizationReader interface. this class
 * also extends FileSystemConfigReader to take advantage of already existing
 * code.
 */
public class ResourceAuthorizationReaderImpl extends FileSystemConfigReader
        implements ResourceAuthorizationReader {

    public ResourceAuthorizationReaderImpl(String basePath, String customerName)
    {
        super(basePath, customerName);
    }

    @Override
    public File getFile(String filePath) {
        StringBuilder b = createStringBuilderWithBasePath();
        b.append(filePath.replace("/", File.separator));
        return createFile(b.toString());
    }

    @Override
    public InputStream getConfigFile(File file) throws FileNotFoundException {
        return new FileInputStream(file);
    }

    protected File createFile(String path)
    {
        return new File(path);
    }
}
