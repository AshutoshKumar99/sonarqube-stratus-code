package com.pb.stratus.controller.action;

import com.pb.stratus.controller.QueryType;
import com.pb.stratus.controller.exception.QueryConfigException;
import com.pb.stratus.controller.featuresearch.FeatureService;
import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.queryutils.XMlConfigConstants;
import com.pb.stratus.controller.service.SearchByQueryParams;
import com.pb.stratus.controller.service.SearchParams;
import com.pb.stratus.controller.service.SearchWithinGeometryParams;
import com.pb.stratus.core.configuration.FileSystemConfigReader;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * This class intends to extract the CSV results for the result set being displayed.
 * It can either export all the results or the current page.
 *
 * For SearchBySQL command issued.
 * @author sa021sh
 */
public class SplitRequestForSearchBySqlAction extends
        BaseSplitRequestForFeatureServiceAction
{
    private FileSystemConfigReader fileSystemConfigReader;

    public SplitRequestForSearchBySqlAction(FeatureService featureService,
           FileSystemConfigReader fileSystemConfigReader)
    {
        super(featureService);
        this.fileSystemConfigReader = fileSystemConfigReader;
    }

    @Override
    protected SearchParams constructParams(HttpServletRequest request)
    {
        SearchByQueryParams params = new SearchByQueryParams();
        populateParams(params, request);
        String tableName = request.getParameter(TABLE_NAME);
        String queryName = request.getParameter(QUERY_NAME);
        String dynamicParams = request.getParameter(PARAMS);
         if (StringUtils.isBlank(tableName) && StringUtils.isBlank(queryName))
        {
            throw new QueryConfigException(
                "Invalid tableName or queryName", XMlConfigConstants.INVALID_TABLEQUERY);
        }
        String spatialOperation = request.getParameter(SPATIAL_OPERATION);
        params.setTableName(tableName);
        params.setQueryName(queryName);
        params.setParams(dynamicParams);
        params.setParams(request.getParameter(PARAMS));
        params.setTotalCount(request.getParameter(TOTAL_COUNT));
        params.setSourceSrs(getSourceSrs(request));
        SearchWithinGeometryParams.SpatialOperation operation =
        		SearchWithinGeometryParams.SpatialOperation.valueOf(spatialOperation);
        params.setSpatialOperation(operation);
            params.setPath(getPath(params.getTableName(), params.getQueryName()));
        return params;
    }

    @Override
    protected FeatureSearchResult getResultFromFeatureService(
            SearchParams searchParam)
    {
        SearchByQueryParams params = (SearchByQueryParams) searchParam;
        params.setQueryType(QueryType.FilteredQuery);
        return getFeatureService().searchByQuery(params);
    }

     private String getPath(String tableName, String  queryName)
    {
        try
        {
            BaseQueryConfigImpl baseQueryConfig =
                    BaseQueryConfigImpl.getInstance();
            String filePath = baseQueryConfig.getFilePath(tableName,
                    this.fileSystemConfigReader);
            File file = baseQueryConfig.getFile(tableName,
                    this.fileSystemConfigReader);
            String path = baseQueryConfig.getPathFromQueryName(queryName, file,
                    filePath);
            return path;
        } catch (FileNotFoundException ex)
        {
            StringBuilder sb = new StringBuilder();
            sb.append("file for the table: ");
            sb.append(tableName);
            sb.append("and query :");
            sb.append(queryName);
            sb.append("was not found");
            throw new QueryConfigException(sb.toString());
        }
    }
}
