package com.pb.stratus.controller.action;

import com.pb.stratus.core.configuration.FileSystemConfigReader;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Agreement for queryConfig
 * 
 * @author ar009sh
 */
public interface BaseQueryConfig
{
    /**
     * Returns the file for the selected tableName
     * 
     * @param tableName
     * @param reader
     * @return
     * @throws FileNotFoundException
     */
    public File getFile(String tableName, FileSystemConfigReader reader)
        throws FileNotFoundException;

    /**
     * returns the absolute file path for the selected file Name
     * 
     * @param tableName
     * @param reader
     * @return
     * @throws FileNotFoundException
     */
    public String getFilePath(String tableName, FileSystemConfigReader reader)
        throws FileNotFoundException;

    /**
     * returns the full path for the query selected on the UI
     * 
     * @param queryName
     * @param file
     * @param filePath
     * @return
     * @throws FileNotFoundException
     */
    public String getPathFromQueryName(String queryName, File file,
        String filePath) throws FileNotFoundException;

}
