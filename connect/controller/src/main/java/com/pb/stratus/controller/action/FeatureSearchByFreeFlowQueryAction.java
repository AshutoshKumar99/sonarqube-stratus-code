package com.pb.stratus.controller.action;

import com.pb.stratus.controller.Constants;
import com.pb.stratus.controller.QueryType;
import com.pb.stratus.controller.exception.QueryConfigException;
import com.pb.stratus.controller.featuresearch.FeatureService;
import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.queryutils.XMlConfigConstants;
import com.pb.stratus.controller.service.SearchByQueryParams;
import com.pb.stratus.controller.service.SearchWithinGeometryParams.SpatialOperation;
import com.pb.stratus.controller.util.helper.SpatialServicesHelper;
import com.pb.stratus.core.configuration.FileSystemConfigReader;
import com.pb.stratus.security.core.resourceauthorization.ResourceAuthorizationConfig;
import com.pb.stratus.security.core.resourceauthorization.ResourceException;
import com.pb.stratus.security.core.resourceauthorization.ResourceType;
import com.pb.stratus.security.core.util.AuthorizationUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.AccessDeniedException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

public class FeatureSearchByFreeFlowQueryAction extends FeatureSearchAction {

    private FileSystemConfigReader reader = null;
    private AuthorizationUtils authorizationUtils;

    private static final Logger log =
            LogManager.getLogger(FeatureSearchByFreeFlowQueryAction.class);

    public FeatureSearchByFreeFlowQueryAction(FeatureService featureService,
                                              FileSystemConfigReader fileSystemConfigReader,
                                              AuthorizationUtils authorizationUtils,
                                              SpatialServicesHelper spatialServicesHelper) {
        super(featureService);
        this.reader = fileSystemConfigReader;
        this.authorizationUtils = authorizationUtils;
        setSph(spatialServicesHelper);
    }

    protected Object createObject(HttpServletRequest request)
            throws ServletException, IOException {

        FeatureSearchResult searchResult = null;
        SearchByQueryParams params = null;
        params = createParams(request);
        params.setPath(getPath(params));
        params.setQueryType(QueryType.FilteredQuery);
        searchResult = getFeatureService().searchByQuery(params);
        return searchResult;
    }

    private SearchByQueryParams createParams(HttpServletRequest request) {
        log.info("Creating SearchByQueryParams from clientRequest.");
        SearchByQueryParams params = new SearchByQueryParams();
        populateParams(params, request);

        String tableName = request.getParameter(TABLE_NAME);
        String queryName = request.getParameter(QUERY_NAME);
        String dynamicParams = request.getParameter(PARAMS);

        if (StringUtils.isBlank(tableName) && StringUtils.isBlank(queryName)) {
            throw new QueryConfigException("Invalid tableName or queryName",
                    XMlConfigConstants.INVALID_TABLEQUERY);
        }

        // CONN-18690: Authorization is required only for the query filters created
        // via admin console and not for a user defined query.
        if (!Constants.CUSTOM_FILTER.equalsIgnoreCase(queryName)) {
            // authorizationCheck
            Set<ResourceAuthorizationConfig> configs = null;
            try {
                configs =
                        authorizationUtils.getAuthorizeConfigs(request,
                                ResourceType.QUERY_CONFIG, tableName);
            } catch (ResourceException e) {
                log.debug("Queryconfig not found.");
                throw new QueryConfigException("Invalid queryName",
                        XMlConfigConstants.INVALID_TABLEQUERY);
            }
            if (configs != null) {
                if (!authorizationUtils.configsContains(queryName, configs)) {
                    throw new AccessDeniedException(
                            "No Authorization on query config");
                }
            }
        }
        String spatialOperation = request.getParameter(SPATIAL_OPERATION);
        params.setTableName(tableName);
        params.setQueryName(queryName);
        params.setParams(dynamicParams);
        params.setTotalCount(request.getParameter(TOTAL_COUNT));
        params.setSourceSrs(getSourceSrs(request));
        SpatialOperation operation = SpatialOperation.valueOf(spatialOperation);
        params.setSpatialOperation(operation);

        return params;
    }

    private String getPath(SearchByQueryParams params)
            throws FileNotFoundException {
        BaseQueryConfigImpl object = BaseQueryConfigImpl.getInstance();
        String tableName = params.getTableName();

        String filePath = object.getFilePath(tableName, reader);
        File file = object.getFile(tableName, reader);
        String path = "";

        try {
            path =
                    object.getPathFromQueryName(params.getQueryName(), file,
                            filePath);
        } catch (FileNotFoundException ex) {

            throw new QueryConfigException(ex.getMessage());
        }
        return path;

    }
}
