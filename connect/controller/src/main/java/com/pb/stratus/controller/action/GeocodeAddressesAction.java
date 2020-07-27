package com.pb.stratus.controller.action;

import com.pb.stratus.controller.InvalidGazetteerException;
import com.pb.stratus.controller.geocoder.GGMRequestParams;
import com.pb.stratus.controller.service.AddressService;
import com.pb.stratus.core.configuration.TenantNameHolder;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.pb.stratus.controller.util.TypeConversionUtils.getIntegerValue;
import static com.pb.stratus.controller.util.TypeConversionUtils.getStringValue;

public class GeocodeAddressesAction extends DataInterchangeFormatControllerAction {

    private static final int DEFAULT_COUNT_VALUE = 1000;

    private final static Logger logger = LogManager.getLogger(GeocodeAddressesAction.class);

    private AddressService addressService;

    private TenantNameHolder m_tenantNameHolder;

    public GeocodeAddressesAction(AddressService addressService,
                                  TenantNameHolder tenantNameHolder) {
        this.addressService = addressService;
        //temporary code for while multi-tenant locate service, but connect isn't yet.
        m_tenantNameHolder = tenantNameHolder;
    }

    protected Object createObject(HttpServletRequest request)
            throws ServletException, IOException, InvalidGazetteerException {

        Map parameters = request.getParameterMap();
        String[] addressArray = (String[]) parameters.get("addressArray");
        List<String> addressList = Arrays.asList(addressArray);
        int count = getIntegerValue("count", parameters, DEFAULT_COUNT_VALUE);
        String gazetteerName = getStringValue("gazetteerName", parameters, null);
        String gazetteerService = getStringValue("gazetteerService", parameters, null);
        if (gazetteerName == null) {
            logger.info("Gazetteer name not found. Throwing exception");
            throw new IllegalArgumentException();
        }

        GGMRequestParams ggmRequestParams = new GGMRequestParams();
        populateSearchParameters(ggmRequestParams, addressList, count, gazetteerName, gazetteerService);
        return search(ggmRequestParams);
    }

    /**
     * Method to populates SearchParameters Object.
     */
    private void populateSearchParameters(GGMRequestParams requestParameters, List<String> addressList,
                                          int searchResultCount, String gazetteerName, String gazetteerService) {
        requestParameters.setAddressList(addressList);
        requestParameters.setMaxRecords(searchResultCount);
        requestParameters.setGazetteerName(gazetteerName);
        requestParameters.setTenantName(m_tenantNameHolder.getTenantName());
        requestParameters.setGazetteerService(gazetteerService);
    }

    private JSONObject search(GGMRequestParams ggmRequestParams) throws InvalidGazetteerException {
        return addressService.geocodeAddresses(ggmRequestParams);
    }
}
