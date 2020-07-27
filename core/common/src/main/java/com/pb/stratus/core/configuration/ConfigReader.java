package com.pb.stratus.core.configuration;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Interface to read configurations
 *
 */
//XXX this overlaps significantly with ResourceResolver. Investigate if we
//    can remove this class and use ResourceResolver/FileSystemResourceResolver
public interface ConfigReader
{
    
    /**
     * Returns an input stream to the configuration file identified by the
     * abstract path <code>filePath</code>
     * 
     * @param filePath a non-empty string, typically a relative path, using
     *        forward slashes as separators.
     * @return an input stream providing the contents of the config file
     * @throws FileNotFoundException if the corresponding file doesn't exist
     */
    InputStream getConfigFile(String filePath) throws FileNotFoundException;
    
    /**
     * Returns an input stream to the configuration file identified by the
     * abstract name <code>fileName</code> and a file type. The idea behind
     * this is to free the caller from the burden of having to know the exact
     * relative path to a configuration file. 
     * 
     * @param fileName a non-empty string
     * @param type the type of the configuration file
     * @return 
     * @throws FileNotFoundException if the corresponding file doesn't exist
     */
    InputStream getConfigFile(String fileName, ConfigFileType type) 
            throws FileNotFoundException;

}
