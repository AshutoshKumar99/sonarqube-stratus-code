package com.pb.stratus.controller.action;

import com.mapinfo.midev.service.units.v1.DistanceUnit;
import com.pb.stratus.controller.IllegalRequestException;
import com.pb.stratus.controller.csv.FMNResultsCsvConverter;
import com.pb.stratus.controller.datainterchangeformat.DataInterchangeFormats;
import com.pb.stratus.controller.featuresearch.FeatureService;
import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.service.SearchNearestParams;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * This class is responsible for providing a back-end interface for accessing
 * the Midev Feature WebService method searchNearest, from a web request.
 * <p/>
 * SearchNearest is a function that finds a defined number of features nearest
 * to the specified geographic origin within an optionally defined distance.
 * <p/>
 * This class uses an Midev webservice Artifact.
 *
 * @author mo002mi
 */
public class FeatureSearchNearestAction extends FeatureSearchAction {

    public static final String DATA_INTERCHANGE_FORMAT_PARAM = "format";
    public static final String LOCATION = "location";
    public static final String MAX_DISTANCE = "maxDistance";
    public static final String DISTANCE_UNIT = "distanceUnit";
    public static final String RETURNED_DISTANCE_UNIT = "returnedDistanceUnit";
    public static final String MAX_RESULTS = "maxResults";
    public static final String INCORRECT_RETURNED_DISTANCE_UNIT_ERROR = "Unknown Returned Distance Unit";
    public static final String INCORRECT_DISTANCE_UNIT_ERROR = "Unknown Distance Unit";


    public FeatureSearchNearestAction(FeatureService featureService) {
        super(featureService);
    }

    @Override
    protected Object createObject(HttpServletRequest request)
            throws ServletException, IOException {

        SearchNearestParams params = createParams(request);
        FeatureSearchResult searchResult = getFeatureService().searchNearest(params);
        setCsvConverter(searchResult, request);

        return searchResult;
    }

    private SearchNearestParams createParams(HttpServletRequest request) {
        SearchNearestParams params = new SearchNearestParams();
        populateParams(params, request);
        params.setPoint(createPoint(request));
        params.setTable(request.getParameter(TABLE_NAME));
        String distanceValue = request.getParameter(MAX_DISTANCE);
        String returnedDistanceUnit = request
                .getParameter(RETURNED_DISTANCE_UNIT);


        if (!StringUtils.isBlank(distanceValue)) {
            params.setDistanceValue(Double.parseDouble(distanceValue));
            DistanceUnit unit = getDistanceUnit(
                    request.getParameter(DISTANCE_UNIT));
            params.setDistanceUnit(unit);
        }
        params.setMaxResults(Integer.parseInt(
                request.getParameter(MAX_RESULTS)));
        params.setReturnedDistanceUnit(getReturnedDistanceUnit
                (returnedDistanceUnit));
        return params;
    }

    private DistanceUnit getReturnedDistanceUnit(String unit) {
        try {
            return validateDistanceUnit(unit);

        } catch (IllegalArgumentException iax) {
            throw new IllegalRequestException(INCORRECT_RETURNED_DISTANCE_UNIT_ERROR + " '" + unit + "'");
        }
    }

    private DistanceUnit getDistanceUnit(String unit) {
        try {
            return validateDistanceUnit(unit);

        } catch (IllegalArgumentException iax) {
            throw new IllegalRequestException(INCORRECT_DISTANCE_UNIT_ERROR + " '" + unit + "'");
        }
    }

    private DistanceUnit validateDistanceUnit(String distanceUnit) throws IllegalArgumentException {
        return validateDistanceUnit(distanceUnit, DistanceUnit.METER);

    }

    private DistanceUnit validateDistanceUnit(String distanceUnit, DistanceUnit defaultDistanceUnit) throws IllegalArgumentException {
        if (StringUtils.isBlank(distanceUnit)) {
            return defaultDistanceUnit;
        }
        return DistanceUnit.valueOf(distanceUnit.toUpperCase());

    }

    /**
     * Set FMN CSV converter, if a FMN results csv file is requested.
     *
     * @param searchResult
     * @param request
     */
    private void setCsvConverter(FeatureSearchResult searchResult, HttpServletRequest request) {
        String extractTableName = request.getParameter(TABLE_NAME).substring(request.getParameter(TABLE_NAME).lastIndexOf('/')+1, request.getParameter(TABLE_NAME).length());
        if (DataInterchangeFormats.CSV.toString().equals(request.getParameter(DATA_INTERCHANGE_FORMAT_PARAM))) {
            searchResult.setCsvConverter(new FMNResultsCsvConverter(searchResult.getFeatureCollection(), request.getParameter(LOCATION),
                    Float.parseFloat(request.getParameter(X_PARAM)), Float.parseFloat(request.getParameter(Y_PARAM)),
                    extractTableName, request.getParameter(DISTANCE_UNIT), request.getParameter(RETURNED_DISTANCE_UNIT),
                    request.getParameter(MAX_DISTANCE)
            ));
        }
    }

}
