package com.pb.stratus.security.core.resourceauthorization;

import com.pb.stratus.core.configuration.ConfigReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * User: sh003bh
 * Date: 11/11/11
 * Time: 12:27 PM
 * Interface with convenient methods required for reading resource authorizations.
 */
public interface ResourceAuthorizationReader extends ConfigReader {

    /**
     * Get reference of a file object with the given path relative to the base
     * directory.
     * @param filePath
     * @return
     */
    public File getFile(String filePath);

    /**
     * Convenient method to get the InputStream for the given file.
     * @param file
     * @return
     * @throws FileNotFoundException
     */
    public InputStream getConfigFile(File file) throws FileNotFoundException;
}
