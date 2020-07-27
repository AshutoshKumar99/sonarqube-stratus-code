package com.pb.stratus.controller.action;

import com.pb.stratus.controller.exception.QueryConfigException;
import com.pb.stratus.security.core.resourceauthorization.ResourceException;
import com.pb.stratus.security.core.resourceauthorization.ResourceType;
import com.pb.stratus.security.core.util.AuthorizationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Provides a map of table repository path and pre created query filter names.
 * Created by GU003DU on 07-Sep-17.
 */
public class QueryFiltersAction extends DataInterchangeFormatControllerAction {
    private static final Logger logger = LogManager.getLogger(QueryFiltersAction.class
            .getName());
    public static final String TABLE_NAMES_PARAM = "tableNames";

    private AuthorizationUtils authorizationUtils;

    public QueryFiltersAction(AuthorizationUtils authorizationUtils) {
        this.authorizationUtils = authorizationUtils;
    }

    @Override
    protected Object createObject(HttpServletRequest request) {
        Map<String, Set<String>> queries = new HashMap<>();
        String[] tableNames = request.getParameterValues(TABLE_NAMES_PARAM);

        if (tableNames.length < 1) {
            throw new QueryConfigException("TableName not Entered", "invalid_Table");
        }

        try {
            Set<String> tables = new HashSet<>(Arrays.asList(tableNames));
            queries = authorizationUtils.getAuthorizedConfigs(request, ResourceType.QUERY_CONFIG, tables);
        } catch (ResourceException e) {
            logger.debug("No Query Found for tables : " + tableNames.toString(), e);
        } finally {
            return queries;
        }
    }
}
