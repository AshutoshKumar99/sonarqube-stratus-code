package com.pb.stratus.controller.featuresearch;

import com.mapinfo.midev.service.feature.v1.BoundParameter;
import com.mapinfo.midev.service.feature.v1.BoundParameterList;
import com.mapinfo.midev.service.feature.v1.SearchBySQLRequest;
import com.mapinfo.midev.service.feature.v1.SearchBySQLResponse;
import com.mapinfo.midev.service.feature.ws.v1.FeatureServiceInterface;
import com.mapinfo.midev.service.feature.ws.v1.ServiceException;
import com.mapinfo.midev.service.featurecollection.v1.*;
import com.mapinfo.midev.service.geometries.v1.*;
import com.pb.stratus.controller.QueryType;
import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.queryutils.QueryMetadata;
import com.pb.stratus.controller.service.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.graphdata.utilities.contract.Contract;

import java.util.List;

/**
 * This class calls the feature MIDev service method searchBySql to get
 * Attribute Query result.
 *
 * @author ar009sh
 */
public class SearchByQueryStrategy extends BaseSearchStrategy {

    private static final Logger log = LogManager
            .getLogger(SearchByQueryStrategy.class);

    public SearchByQueryStrategy(FeatureServiceInterface featureWebService,
                                 FeatureSearchResultConverterFactory converterFactory, int maxResults) {
        super(featureWebService, converterFactory, maxResults);

    }

    /**
     * Makes query to get the feature result as well as the count for number of
     * records
     *
     * @param searchParams initial parameters present in the HTTP request
     * @return FeatureSearchResult
     */
    public FeatureSearchResult search(SearchParams searchParams) {

        Contract.pre(searchParams instanceof SearchByQueryParams,
                "SearchParams must be an instance of " + SearchByQueryParams.class);

        SearchByQueryParams searchBySqlParams = (SearchByQueryParams) searchParams;
        SearchBySQLRequest request = createRequest(searchBySqlParams, false);
        SearchBySQLResponse response = performRequest(request);
        FeatureSearchResult result = convertWebServiceResponse(
                searchBySqlParams.getTableName(), response);
        long count = populateTotalCount(searchBySqlParams);
        if (result != null) {
            result.getFeatureCollection().setTotalCount(count);
            String sql = request.getSQL();      // Conn-15868 along with query results we send an additional parameter "sql" corresponding to that query
            if (sql != null){
                result.setQuerySql(sql);
            }
        }


        return result;
    }

    private long populateTotalCount(SearchByQueryParams searchBySqlParams) {
        long totalCount = getTotalCountParam(searchBySqlParams);
        if (totalCount >= 0) {
            log
                    .debug("No need to fetch TotalCount from FeatureService | totalCount : "
                            + totalCount);
            return totalCount;

        } else {
            /*
             * create the get count query here .fire an sql here to get the
             * totalCount . populating totalCount in featureSearch Result
             * returned by this method.
             */
            log
                    .debug("Need to fetch TotalCount from FeatureService | totalCount : "
                            + totalCount);
            SearchBySQLRequest countRequest = createRequest(searchBySqlParams,
                    true);
            SearchBySQLResponse countResponse = performRequest(countRequest);
            log.debug("count injected into the feature search result >"
                    + getCount(countResponse));
            /*
             * result.getFeatureCollection()
             * .setTotalCount(getCount(countResponse));
             */
            return getCount(countResponse);

        }

    }

    private long getTotalCountParam(SearchByQueryParams searchBySqlParams) {

        long totalCount = 0;
        try {
            String count = searchBySqlParams.getTotalCount();

            if (null != count) {
                totalCount = Long.parseLong(count.trim());
            }

        } catch (NumberFormatException e) {
            log.error("Invalid totalCount parameter |totalCount : "
                    + searchBySqlParams.getTotalCount());
            throw new IllegalArgumentException("TotalCount must be numeric.", e);
        }
        return totalCount;
    }

    private SearchBySQLRequest createRequest(SearchByQueryParams params,
                                             boolean getCount) {
        SearchBySQLRequest request = new SearchBySQLRequest();
        if (!getCount) {
            if(params.getQueryType() == QueryType.DataBindingQuery)
            {
                request.setLocale(params.getLocale());
            }else
            {
                super.popuplateBaseValues(params.getTableName(), request, params);
            }

        }

        BoundParameterList boundParameterList = new BoundParameterList();
        List<BoundParameter> boundParameters = boundParameterList
                .getBoundParameter();

        Geometry geom = params.getGeometry();

        if (geom instanceof Polygon) {
            MultiPolygon mpolygon = new MultiPolygon();
            log.info("adding polygon bound parameter to request");
            mpolygon.getPolygon().add((Polygon) geom);
            BoundParameter boundParameter = new BoundParameter();
            boundParameter.setName("POLYGON");
            GeometryValue geometryValue = new GeometryValue();
            geometryValue.setFeatureGeometry(mpolygon);
            boundParameter.setValue(geometryValue);
            boundParameters.add(boundParameter);
        } else if (geom instanceof LineString) {
            log.info("adding linestring bound parameter to request");
            Curve curve = new Curve();
            curve.setSrsName(params.getSourceSrs());
            curve.getLineString().add((LineString) geom);
            MultiCurve mcurve = new MultiCurve();
            mcurve.getCurve().add(curve);
            BoundParameter boundParameter = new BoundParameter();
            boundParameter.setName("MULTICURVE");
            GeometryValue geometryValue = new GeometryValue();
            geometryValue.setFeatureGeometry(mcurve);
            boundParameter.setValue(geometryValue);
            boundParameters.add(boundParameter);
        } else if (geom instanceof Point) {
            log.info("adding point bound parameter to request");
            Point point = (Point) geom;
            BoundParameter boundParameter = new BoundParameter();
            boundParameter.setName("POINT");
            GeometryValue geometryValue = new GeometryValue();
            geometryValue.setFeatureGeometry(point);
            boundParameter.setValue(geometryValue);
            boundParameters.add(boundParameter);
        } else if (geom instanceof MultiPolygon) {
            BoundParameter boundParameter = new BoundParameter();
            boundParameter.setName("MULTIPOLYGON");
            GeometryValue geometryValue = new GeometryValue();
            geometryValue.setFeatureGeometry((MultiPolygon) geom);
            boundParameter.setValue(geometryValue);
            boundParameters.add(boundParameter);
        } else if (geom instanceof MultiFeatureGeometry) {
            BoundParameter boundParameter = new BoundParameter();
            boundParameter.setName("MULTIFEATUREGEOMETRY");
            GeometryValue geometryValue = new GeometryValue();
            geometryValue.setFeatureGeometry((MultiFeatureGeometry) geom);
            boundParameter.setValue(geometryValue);
            boundParameters.add(boundParameter);
        }

        // Get Query metadata
        // CONN-18686: Made changes to create query metadata depending on the
        // query filter
        Query queryImpl = new QueryFactory().createQuery(params.getQueryType());
        QueryMetadata queryMetadata = null;
        String geometryAttribute = "";
        if(params.getQueryType() != QueryType.DataBindingQuery &&
                params.getQueryType() != QueryType.ApplicationLinkingQuery)
        {
            queryMetadata = getQueryMetadata(queryImpl, params);
            geometryAttribute = getGeometryAttributeFromTable(params.getTableName());
        }
        String sql = queryImpl.createSQLQuery(queryMetadata, params, getCount, boundParameters, geometryAttribute);
        request.setSQL(sql);
        // CONN-13488 Don't set empty element.
        if (boundParameterList.getBoundParameter() != null && !boundParameterList.getBoundParameter().isEmpty())
            request.setBoundParameterList(boundParameterList);

        return request;
    }

    private SearchBySQLResponse performRequest(SearchBySQLRequest request) {
        try {
            return featureWebService.searchBySQL(request);
        } catch (ServiceException e) {
            throw new Error("Unhandled ServiceException", e);
        }
    }

    private Long getCount(SearchBySQLResponse countResponse) {

        com.mapinfo.midev.service.featurecollection.v1.FeatureCollection features = countResponse
                .getFeatureCollection();
        com.mapinfo.midev.service.featurecollection.v1.FeatureList list = features
                .getFeatureList();
        Long value = Long.parseLong("-1");
        for (Feature feature : list.getFeature()) {
            for (AttributeValue attributeValue : feature.getAttributeValue()) {
                try {
                    String count = PropertyUtils.getProperty(
                            attributeValue, "value").toString();
                    if (attributeValue instanceof IntValue) {
                        value = Long.parseLong(count);
                    }// need to remove workaround after MID-4064 fix.
                    else if (attributeValue instanceof DoubleValue) {
                        value = (long) Double.parseDouble(count);
                    } else {
                        throw new RuntimeException(
                                "Attribute value not an instance of IntValue");
                    }
                } catch (Exception x) {
                    throw new IllegalStateException(
                            "Encountered AttributeValue "
                                    + "without value property", x);
                }
            }
        }
        return value;

    }

    /**
     * Creates Query metadata depending on the type of query filter i.e. custom query
     * or already saved query filter usually created via admin console.
     *
     * @param params
     * @return
     */
    private QueryMetadata getQueryMetadata(Query queryImpl, SearchByQueryParams params) {
        ReadInputQuery object = null;

        if (queryImpl.isCustomQuery(params)) {
            // Get the query metadata from request.
            object = new CustomQueryMetadataFactory();
        } else {
            // Get the query metadata from saved xml
            object = new ReadInputQueryFromXML();
        }
        return object.createQueryMetadata(params);
    }
}