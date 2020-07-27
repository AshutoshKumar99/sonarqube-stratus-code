package com.pb.stratus.controller.action;

import com.pb.stratus.controller.exception.QueryConfigException;
import com.pb.stratus.security.core.util.AuthorizationUtils;
import com.pb.stratus.security.core.resourceauthorization.ResourceAuthorizationConfig;
import com.pb.stratus.security.core.resourceauthorization.ResourceException;
import com.pb.stratus.security.core.resourceauthorization.ResourceType;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Provides the list of QueryConfig XML for a given Table
 * 
 * @author ar009sh
 */
public class QueryListAction extends DataInterchangeFormatControllerAction
{
    private static final Logger logger = LogManager.getLogger(QueryListAction.class
        .getName());

    private static final String NO_FILTER = "No Filter";

    private static final String CUSTOM_FILTER = "Custom Filter";

    private AuthorizationUtils authorizationUtils;

    public QueryListAction(AuthorizationUtils authorizationUtils) {
        this.authorizationUtils = authorizationUtils;
    }

    protected Object createObject(HttpServletRequest request)
    {
        String tableName = request.getParameter("tableName");
        Set<ResourceAuthorizationConfig> configs = null;
        List<String> queryList = getDefaultQueryList();
        List<String> queryConfigList = new ArrayList<String>();
        if (StringUtils.isBlank(tableName))
        {
            throw new QueryConfigException(
                "TableName not Entered","invalid_Table");
        }
        try
        {
            configs =  authorizationUtils.getAuthorizeConfigs(request, ResourceType.QUERY_CONFIG, tableName);
        }
        catch (ResourceException e)
        {
            logger.debug("No Query Found for table :"+tableName);
            return new QueryListResponse(queryList);
        }
        
        for(ResourceAuthorizationConfig config:configs){
            queryConfigList.add(config.getName());
        }
        Collections.sort(queryConfigList);
        queryList.addAll(1, queryConfigList);
        return new QueryListResponse(queryList);
    }

    private List<String> getDefaultQueryList()
    {
        List<String> list = new ArrayList<String>();
        list.add(NO_FILTER);
        list.add(CUSTOM_FILTER);
        return list;
    }

    public class QueryListResponse
    {
        private List<String> queryList;

        public List<String> getQueryList()
        {
            return queryList;
        }

        public void setQueryList(List<String> queryList)
        {
            this.queryList = queryList;
        }

        public QueryListResponse(List<String> queryList)
        {
            this.queryList = queryList;
        }
    }

}
