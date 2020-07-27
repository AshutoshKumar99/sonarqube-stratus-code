package com.pb.stratus.controller.geocoder;

import com.pb.gazetteer.webservice.Address;
import com.pb.gazetteer.webservice.SearchParameters;
import com.pb.stratus.controller.model.Option;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * Provides helper methods to construct EGM specific GeoService client
 * Created by ar009sh on 25-08-2015.
 */
public class EGMHelper implements GeoServiceHelper {

    private static volatile EGMHelper instance;

    private EGMHelper() {
    }

    public String buildURL(GeoSearchParams egmParams, SearchParameters searchParams)
            throws UnsupportedEncodingException {
        int maxCandidates = searchParams.getMaxRecords();
        StringBuilder result = new StringBuilder();
        result.append(egmParams.getEndPoint());
        if (egmParams.getEndPoint().charAt(egmParams.getEndPoint().length() - 1) != '/') {
            result.append("/results.json?");
        } else {
            result.append("results.json?");
        }
        result.append("Data.AddressLine1=");
        result.append(URLEncoder.encode(searchParams.getSearchString(), "UTF-8"));
        if (!StringUtils.isEmpty(egmParams.getCountry())) {
            result.append("&Data.Country=");
            result.append(egmParams.getCountry());
            result.append("&Option.").append(egmParams.getCountry()).append(".KeepMultimatch=Y");
            result = (maxCandidates > 0) ? result.append("&Option.").append(egmParams.getCountry())
                    .append(".MaxCandidates=").append(maxCandidates) : result.append("&Option.")
                    .append(egmParams.getCountry()).append(".MaxCandidates=20");
        }
        for (Option option : egmParams.getOptions()) {
            result.append("&Option.").append(option.getName()).append("=").append(option.getValue());
        }
        return result.toString();
    }

    public Address parseAddress(JSONObject resource) {
        Address result = new Address();

        result.setAddress((String) resource.get("AddressLine1") + " " + resource.get("LastLine"));
        result.setY(Double.valueOf(resource.get("Latitude").toString()));
        result.setX(Double.valueOf(resource.get("Longitude").toString()));
        if (resource.get("CoordinateSystem") != null)
            result.setSrs(resource.get("CoordinateSystem").toString());
        else
            result.setSrs("epsg:4326");

        return result;
    }

    /**
     * @return
     */
    public static EGMHelper getInstance() {
        if (null == instance) {
            synchronized (EGMHelper.class) {
                if (null == instance) {
                    instance = new EGMHelper();
                }
            }
        }
        return instance;
    }
}
