package com.pb.stratus.controller.featuresearch;

import com.mapinfo.midev.service.feature.v1.AttributeList;
import com.mapinfo.midev.service.feature.v1.SearchIntersectsRequest;
import com.mapinfo.midev.service.feature.v1.SearchResponse;
import com.mapinfo.midev.service.feature.v1.SearchWithinPolygonRequest;
import com.mapinfo.midev.service.feature.ws.v1.FeatureServiceInterface;
import com.mapinfo.midev.service.feature.ws.v1.ServiceException;
import com.mapinfo.midev.service.table.v1.NamedTable;
import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.service.SearchParams;
import com.pb.stratus.controller.service.SearchWithinGeometryParams;
import uk.co.graphdata.utilities.contract.Contract;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by GU003DU on 3/10/2016.
 */
public class SearchByGeometryInTablesStrategy extends BaseSearchStrategy {

    public SearchByGeometryInTablesStrategy(
            FeatureServiceInterface featureWebService,
            FeatureSearchResultConverterFactory converterFactory, int maxResults) {
        super(featureWebService, converterFactory, maxResults);
    }

    @Override
    public FeatureSearchResult search(SearchParams searchParams) {
        Contract.pre(searchParams instanceof SearchWithinGeometryParams,
                "SearchParams must be an instance of " +
                        SearchWithinGeometryParams.class);

        SearchWithinGeometryParams searchWithinGeometryParams =
                (SearchWithinGeometryParams) searchParams;
        return performSearch(searchWithinGeometryParams);

    }

    private FeatureSearchResult performSearch(SearchWithinGeometryParams params) {
        Map<String, SearchResponse> responses = new LinkedHashMap<String, SearchResponse>();
        // Get the tables
        String[] tables;
        if (params.getTable().indexOf(",") > 0) {
            tables = params.getTable().split(",");
        } else {
            tables = new String[1];
            tables[0] = params.getTable();
        }

        SearchIntersectsRequest request = createIntersectsRequest(params, tables[0]);
        NamedTable namedTable = new NamedTable();
        for (String table : tables) {
            namedTable.setName(table);
            request.setTable(namedTable);
            SearchResponse response = performSearchIntersects(request);
            responses.put(table, response);
        }
        return convertResponsesToFeatureSearchResult(responses,params);
    }


    private SearchIntersectsRequest createIntersectsRequest(SearchWithinGeometryParams searchParams, String table) {
        SearchIntersectsRequest request = new SearchIntersectsRequest();
        popuplateBaseValues(table, request, searchParams);
        request.setGeometry(searchParams.getGeometry());
        AttributeList excludeList = new AttributeList();
        excludeList.setExclude(Boolean.TRUE);
        excludeList.getAttributeName().add("OBJ");
        excludeList.getAttributeName().add("GEOM");
        request.setAttributeList(excludeList);
        return request;
    }

    private SearchResponse performSearchIntersects(
            SearchIntersectsRequest request) {
        try {
            return featureWebService.searchIntersects(request);
        } catch (ServiceException sx) {
            throw new Error("Unhandled ServiceException", sx);
        }
    }

    private FeatureSearchResult convertResponsesToFeatureSearchResult(
            Map<String, SearchResponse> responses,SearchWithinGeometryParams params) {
        FeatureSearchResult joined = new FeatureSearchResult();
        for (Map.Entry<String, SearchResponse> entry : responses.entrySet()) {
            String tableName = entry.getKey();
            FeatureSearchResult result = convertWebServiceResponse(
                    tableName, entry.getValue(),params);
            joined.addFeatureCollection(tableName,
                    result.getFeatureCollections().get(tableName));
            joined.addFeatureCollectionMetadata(tableName,
                    result.getFeatureCollectionsMetadata().get(tableName));
            joined.setMaxFeaturesCount(maxResults);
        }
        return joined;
    }
}
