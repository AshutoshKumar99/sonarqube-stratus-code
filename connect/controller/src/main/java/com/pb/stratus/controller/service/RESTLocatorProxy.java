package com.pb.stratus.controller.service;

import com.pb.gazetteer.webservice.*;
import com.pb.stratus.controller.InvalidGazetteerException;
import com.pb.stratus.controller.geometry.GeometryService;
import com.pb.stratus.controller.locator.Location;
import com.pb.stratus.controller.util.LocationTransformer;
import com.pb.stratus.core.configuration.CustomerConfigDirHolder;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import uk.co.graphdata.utilities.contract.Contract;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 7/26/12
 * Time: 12:07 PM
 * This class is a proxy class for Connect Locatesearch REST APIs to perform all operations exposed.
 */
public class RESTLocatorProxy {

    private static final Logger LOG = LogManager.getLogger(RESTLocatorProxy.class);
    private SingleLineAddress singleLineAddressWebService;
    private CustomerConfigDirHolder configDirHolder;
    private LocationTransformer transformer;

    private final String DEFAULT_SEARCH_LOGIC = "SimpleWildcardSearch";

    public RESTLocatorProxy(SingleLineAddress singleLineAddress, GeometryService geometryService, CustomerConfigDirHolder configDirHolder){
        this.singleLineAddressWebService = singleLineAddress;
        transformer = new LocationTransformer(geometryService);
        this.configDirHolder = configDirHolder;
    }

    public JSONObject search(SearchParameters params, String targetSrs)throws LocateException_Exception
    {
        Contract.pre(!StringUtils.isBlank(params.getSearchString()),
                "Search expression required");
        Contract.pre(!StringUtils.isBlank(params.getTenantName()),
                "Tenant name required");
        Contract.pre(params.getMaxRecords() > 0,
                "Positive maxResults required");
        if(isValidGazetteerName(params)){

            List<Address> addresses = singleLineAddressWebService.search(params);
            List<Location> locations = new LinkedList<Location>();
            // expecting data projection on all points is egual.Not sure
            // how this works with multiple source concept.
            String dataProjection = targetSrs;
            for (Address a : addresses) {
                Location loc = new Location(a.getId(), 0.0f, a.getAddress(),
                        a.getX(), a.getY(), a.getSrs());
                dataProjection = a.getSrs();
                locations.add(loc);
            }
            // tranform the locations if the projection is not same.
            if (targetSrs != null && !dataProjection.equals(targetSrs)) {
                locations =  transformer.transformLocations(locations, targetSrs);
            }
            JSONArray locationsJson = (JSONArray)JSONSerializer.toJSON(locations);
            JSONObject obj = new JSONObject();
            obj.put("SearchResults", locationsJson);
            return obj;
        }else{
            LOG.error("Invalid Gazetteer Name");
            throw new InvalidGazetteerException();
        }
    }

    private boolean isValidGazetteerName(SearchParameters params) throws LocateException_Exception{
        String gazetteerName = params.getGazetteerName();
        if(StringUtils.isEmpty(gazetteerName)){
            return false;
        }
        GazetteerNames gazetteers = singleLineAddressWebService.getGazetteerNames(params.getTenantName());
        for(GazetteerInstance gazetteer :gazetteers.getGazetteerInstances()){
            if(gazetteerName.equals(gazetteer.getGazetteerName())){
                return true;
            }
        }
        return  false;
    }

    public JSONObject listGazetteer(String tenant)throws LocateException_Exception
    {
        Contract.pre(!StringUtils.isBlank(tenant),
                "Tenant name required");
        GazetteerNames gazetteers = singleLineAddressWebService.getGazetteerNames(tenant);
        JSONObject obj = new JSONObject();
        JSONArray namesJson = new JSONArray();
        for(GazetteerInstance gazetteer : gazetteers.getGazetteerInstances()){
            namesJson.add(gazetteer.getGazetteerName());
        }
        obj.put("Gazetteers", namesJson);
        return obj;
    }

    public JSONObject describeGazetteer(String tenant, String gazetteerName) throws LocateException_Exception
    {
        Contract.pre(!StringUtils.isBlank(tenant),
                "Tenant name required");

        JSONArray gazetteerList = extractAllGazetteersInfo(tenant);

        for(int i=0; i<gazetteerList.size(); i++)
        {
            if(gazetteerList.getJSONObject(i).get("gazetteerName")
                    .toString().equals(gazetteerName))
            {
                return describeGazetteerJSONResponse(
                        gazetteerList.getJSONObject(i));
            }
        }
        throw new InvalidGazetteerException();
    }

    private JSONObject describeGazetteerJSONResponse(JSONObject gazetteerObject)
    {
        JSONObject properties = new JSONObject();
        properties.put("name", gazetteerObject.get("srs"));

        JSONObject crs = new JSONObject();
        crs.put("type", "name");
        crs.put("properties", properties);

        JSONObject gazetteerDescriptionValue = new JSONObject();

        gazetteerDescriptionValue.put("Version", "1.0");
        gazetteerDescriptionValue.put("Name", gazetteerObject.get("gazetteerName"));
        gazetteerDescriptionValue.put("crs", crs);

        JSONObject gazetteerDescription = new JSONObject();
        gazetteerDescription.put("GazetteerDescription", gazetteerDescriptionValue);

        return gazetteerDescription;
    }

    public JSONArray extractAllGazetteersInfo(String tenant) throws LocateException_Exception
    {
        GazetteerNames gazetteers = singleLineAddressWebService.getGazetteerNames(tenant);
        JSONArray jsonArray = new JSONArray();

        for(GazetteerInstance gazetteer :gazetteers.getGazetteerInstances())
        {
            jsonArray.add(gazetteer);
        }
        return jsonArray;
    }
}
