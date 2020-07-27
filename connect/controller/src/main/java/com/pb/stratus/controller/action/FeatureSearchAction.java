package com.pb.stratus.controller.action;

import com.mapinfo.midev.service.feature.ws.v1.ServiceException;
import com.mapinfo.midev.service.geometries.v1.Geometry;
import com.mapinfo.midev.service.geometries.v1.Point;
import com.mapinfo.midev.service.geometries.v1.Pos;
import com.pb.stratus.controller.featuresearch.FeatureService;
import com.pb.stratus.controller.json.geojson.GeoJsonParser;
import com.pb.stratus.controller.service.SearchParams;
import com.pb.stratus.controller.util.helper.SpatialServicesHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * This class handles the requests for getting the feature information at a
 * specified point, polygon or by expression.
 */
public abstract class FeatureSearchAction extends DataInterchangeFormatControllerAction {

    public static final String X_PARAM = "x";

    public static final String Y_PARAM = "y";

    public static final String PROPERTIES_PARAM = "attributes";

    public static final String INCLUDE_GEOMETRY_PARAM = "includeGeometry";

    public static final String SOURCE_SRS_PARAM = "sourceSrs";

    public static final String TARGET_SRS_PARAM = "targetSrs";

    public static final String LOCALE_PARAM = "locale";

    public static final String PAGE_LENGTH_PARAM = "pageLength";

    public static final String PAGE_NUM_PARAM = "pageNumber";

    public static final String ORDER_BY_COLS_PARAM = "orderByColumns";

    public static final String ORDER_BY_DIR_PARAM = "orderByDirection";

    public static final String RETURN_COUNT_PARAM = "returnTotalCount";

    public static final String TABLE_NAME = "tableName";

    public static final String SPATIAL_OPERATION = "spatialOperation";

    public static final String QUERY_NAME = "queryName";

    public static final String GEOMETRY = "geometry";

    public static final String PARAMS = "params";

    public static final String TOTAL_COUNT = "totalCount";

    // maximum page length that MiDev allows
    public static int MAX_PAGE_LENGTH = 1000;
    // If the clientRequest is split then use the new page length
    public static int NEW_PAGE_LENGTH = 999;

    private FeatureService featureService;

    private static final Logger log =
            LogManager.getLogger(FeatureSearchAction.class);

    protected FeatureSearchAction(FeatureService featureService) {
        this.featureService = featureService;
    }

    protected FeatureService getFeatureService() {
        return featureService;
    }

    protected boolean includeGeometry(HttpServletRequest request) {
        return Boolean.parseBoolean(request
                .getParameter(INCLUDE_GEOMETRY_PARAM));
    }

    protected String getSourceSrs(HttpServletRequest request) {
        return request.getParameter(SOURCE_SRS_PARAM);
    }

    protected String getTargetSrs(HttpServletRequest request) {
        return request.getParameter(TARGET_SRS_PARAM);
    }

    protected void populateParams(SearchParams params,
                                  HttpServletRequest request) {
        String attrs[] = request.getParameterValues(PROPERTIES_PARAM);
        if (attrs != null) {
            params.getAttributes().addAll(Arrays.asList(attrs));
        }
        params.setIncludeGeometry(includeGeometry(request));
        params.setTargetSrs(request.getParameter(TARGET_SRS_PARAM));

        // setting locale, paging, ordering, count(all optional) parameters
        params.setLocale(request.getParameter(LOCALE_PARAM));
        params.setReturnTotalCount(Boolean.parseBoolean(request
                .getParameter(RETURN_COUNT_PARAM)));
        setPageParameters(request, params);
        setOrderByParameters(request, params);

        // added for redlining
        log.info("Parsing Geojson to create geometry.");
        String geometry = request.getParameter("geometry");

        if (!StringUtils.isBlank(geometry)) {
            GeoJsonParser parser = new GeoJsonParser(getSourceSrs(request));
            Geometry geom = parser.parseGeometry(geometry);

            /**
             * The geometry has been parsed now time to call getUnion, in case it is
             * for a polygon.
             */
            try {
                /**
                 * I am not setting SpatialHandler in every class, so just to double ensure the existing code runs.
                 */
                if (sph != null) {
                    geom = sph.getUnion(geom);
                }
            } catch (Exception ex) {
                log.error(ex);
            }

            params.setGeometry(geom);
        }
    }

    protected Point createPoint(HttpServletRequest request) {
        Point p = new Point();
        Pos pos = new Pos();
        double x = Float.parseFloat(request.getParameter(X_PARAM));
        double y = Float.parseFloat(request.getParameter(Y_PARAM));
        pos.setX(x);
        pos.setY(y);
        p.setPos(pos);
        p.setSrsName(getSourceSrs(request));
        return p;
    }

    private void setOrderByParameters(HttpServletRequest request,
                                      SearchParams params) {
        String orderByCols[] = request.getParameterValues(ORDER_BY_COLS_PARAM);
        if (orderByCols != null) {
            params.getOrderByList().addAll(Arrays.asList(orderByCols));
            params.setOrderByDirection(request.getParameter(ORDER_BY_DIR_PARAM));
        }
    }

    private void setPageParameters(HttpServletRequest request,
                                   SearchParams params) {
        params.setPageLength(parseToInt(request.getParameter(PAGE_LENGTH_PARAM)));
        params.setPageNumber(parseToInt(request.getParameter(PAGE_NUM_PARAM)));
    }

    private int parseToInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException nfex) {
            return -1;
        }
    }

    public SpatialServicesHelper getSph() {
        return sph;
    }

    public void setSph(SpatialServicesHelper sph) {
        this.sph = sph;
    }

    /**
     * A helper class that will be called in for cross services operations that need
     * to be invoked.
     *
     * Current - Need - Union Geometry call.
     */
    SpatialServicesHelper sph;
}
