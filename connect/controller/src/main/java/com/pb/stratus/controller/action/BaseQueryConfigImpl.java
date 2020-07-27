package com.pb.stratus.controller.action;

import com.pb.stratus.controller.queryutils.XMlConfigConstants;
import com.pb.stratus.core.configuration.FileSystemConfigReader;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * This class takes care of providing filePath for input XML
 * 
 * @author ar009sh
 */

public class BaseQueryConfigImpl implements BaseQueryConfig
{
    private static volatile BaseQueryConfigImpl instance = null;

    public File getFile(String tableName, FileSystemConfigReader reader)
        throws FileNotFoundException
    {
        return new File(getFilePath(tableName, reader));
    }

    public String getFilePath(String tableName, FileSystemConfigReader reader)
        throws FileNotFoundException
    {
        String filePath = null;

        if (reader != null)
        {
            filePath = reader.getBasePath() + File.separator
                + reader.getCustomerName() + File.separator
                + XMlConfigConstants.Query_CONFIG + File.separator + tableName;
        }
        return filePath;
    }

    public String getPathFromQueryName(String queryName, File file,
        String filePath) throws FileNotFoundException
    {
        String[] children = file.list();
        String path = "";
        if (children != null)
        {
            for (int i = 0; i < children.length; i++)
            {
                if (FilenameUtils.getBaseName(children[i]).equals(queryName)
                        && FilenameUtils.getExtension(children[i]).equalsIgnoreCase("xml"))
                {
                    path = (filePath + File.separator + children[i]);
                }
            }
        }

        return path;
    }

    public static BaseQueryConfigImpl getInstance()
    {
        if (null == instance)
        {
            instance = new BaseQueryConfigImpl();
        }
        return instance;
    }

    private BaseQueryConfigImpl()
    {
    }

}
