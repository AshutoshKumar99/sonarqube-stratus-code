package com.pb.stratus.controller.action;

import com.pb.stratus.controller.exception.QueryConfigException;
import com.pb.stratus.core.configuration.FileSystemConfigReader;
import com.pb.stratus.security.core.util.AuthorizationUtils;
import com.pb.stratus.security.core.resourceauthorization.ResourceAuthorizationConfig;
import com.pb.stratus.security.core.resourceauthorization.ResourceException;
import com.pb.stratus.security.core.resourceauthorization.ResourceType;
import net.sf.json.JSON;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

/**
 * Converts QueryConfig XML to JSON Object
 * 
 * @author ar009sh
 */
public class QueryDataLoadAction extends DataInterchangeFormatControllerAction
{
    private static final Logger logger = LogManager
        .getLogger(QueryDataLoadAction.class.getName());

    private FileSystemConfigReader reader = null;
    BaseQueryConfig object = null;

    public QueryDataLoadAction(FileSystemConfigReader fileSystemConfigReader)
    {
        this.reader = fileSystemConfigReader;
    }

    protected Object createObject(HttpServletRequest request)
        throws IOException
    {
        String tableName = request.getParameter("tableName");
        String queryName = request.getParameter("queryName");
        Set<ResourceAuthorizationConfig> configs = null;
        
        if (StringUtils.isBlank(tableName) || StringUtils.isBlank(queryName))
        {
            throw new QueryConfigException(
                "TableName or QueryName not Entered","invalid_Table/Query");
        }

        JSON response = null;
        String filePath = "";
        try
        {
            object = BaseQueryConfigImpl.getInstance();
            filePath = object.getFilePath(tableName, reader);
            File file = object.getFile(tableName, reader);

            if (file.isDirectory())
            {
                String path = object.getPathFromQueryName(queryName, file,
                    filePath);
                if (path != null)
                {
                    response = convertxmlToJSON(path);
                }
            }
            else
            {
                logger.error("Directory with given table name does not exist ");
            }
        }
        catch (FileNotFoundException ex)
        {
            logger.error("FileNotFoundException Occured :", ex);
        }

        if (null == response)
        {
            // sending empty JSON Object as per the front end requirements
            logger
                .error("Unable to locate queryConfig file .Please check server logs");
            response = new JSONObject();
        }
        return response;
    }

    private JSON convertxmlToJSON(String inputFilePath) throws IOException
    {
        JSON jsonresponse = null;
        FileInputStream stream = null; 
        try
        {
            File file = new File(inputFilePath);
            if( file == null ){
                throw new JSONException( "File is null" );
             }
             if( !file.canRead() ){
                throw new JSONException( "Can't read input file" );
             }
             if( file.isDirectory() ){
                throw new JSONException( "File is a directory" );
             }
             
            XMLSerializer xmlSerializer = new XMLSerializer();
            stream = new FileInputStream(file);
            jsonresponse = xmlSerializer.readFromStream(stream);
            logger.info(jsonresponse);
        }
        catch (Exception e)
        {
            logger.error("exception in converting " + inputFilePath
                + " to JSON format : ");
            e.printStackTrace();
        }
        finally
        {
        	if(stream != null)
        	{
        		stream.close();
        	}
        }
        return jsonresponse;
    }

}
