/**
 * CustomQueryMetadataFactory
 * Creates metadata for a custom query.
 *
 * User: gu003du
 * Date: 6/10/14
 * Time: 4:02 PM
 */
package com.pb.stratus.controller.service;

import com.pb.stratus.controller.queryutils.CriteriaParams;
import com.pb.stratus.controller.queryutils.QueryMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.pb.stratus.controller.Constants.*;

public class CustomQueryMetadataFactory implements ReadInputQuery {

    @Override
    public QueryMetadata createQueryMetadata(SearchByQueryParams queryParams) {
        // Get the custom query conditions from 'params'
        List<Map<String, Object>> params = queryParams.getCustomQueryParams();

        // Create a list of criteriaParams corresponding to  each condition i.e.
        // column name, operator and supplied value.
        List<CriteriaParams> criteriaParams = new ArrayList<>();
        for (Map<String, Object> condition : params) {
            List<String> val = null;
            Object obj = condition.get(QUERY_COLUMN_VAL);
            if (obj instanceof String)
            {
                val = new ArrayList<String>();
                val.add(obj.toString());
            }
            else if(obj instanceof List)
            {
                val = (List)obj;
            }

            CriteriaParams criteriaParam = new CriteriaParams(condition.get(QUERY_COLUMN_NAME).toString(),
                    condition.get(QUERY_COLUMN_TYPE).toString(), condition.get(QUERY_OPERATOR).toString(), val);
            criteriaParams.add(criteriaParam);
        }

        //  create query metadata
        QueryMetadata queryMetadata = new QueryMetadata();
        queryMetadata.setTableName(queryParams.getTableName());
        queryMetadata.setQueryName(queryParams.getQueryName());
        queryMetadata.setCriteriaParams(criteriaParams);

        return queryMetadata;
    }
}
