package com.pb.stratus.controller.util.helper;

import com.mapinfo.midev.service.feature.ws.v1.FeatureServiceInterface;
import com.mapinfo.midev.service.geometries.v1.Geometry;
import com.mapinfo.midev.service.geometries.v1.GeometryList;
import com.mapinfo.midev.service.geometries.v1.MultiPolygon;
import com.mapinfo.midev.service.geometry.v1.UnionRequest;
import com.mapinfo.midev.service.geometry.v1.UnionResponse;
import com.mapinfo.midev.service.geometry.ws.v1.GeometryServiceInterface;
import com.mapinfo.midev.service.geometry.ws.v1.ServiceException;
import com.mapinfo.midev.service.mapping.ws.v1.MappingServiceInterface;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


/**
 * I just failed to understand the cris-cross dependencies and hence adding to the chaos
 * by adding a helper class that will help me call all un-abstracted spatial services.
 * Created by sa021sh on 10/27/2014.
 */
public class SpatialServicesHelper {
    public final static Logger logger = LogManager.getLogger(SpatialServicesHelper.class);

    private GeometryServiceInterface geometryWebService;

    public SpatialServicesHelper(GeometryServiceInterface geometryWebService, MappingServiceInterface mappingWebService, FeatureServiceInterface featureWebService) {
        this.geometryWebService = geometryWebService;
        this.mappingWebService = mappingWebService;
        this.featureWebService = featureWebService;
    }

    public MappingServiceInterface getMappingWebService() {
        return mappingWebService;
    }

    public void setMappingWebService(MappingServiceInterface mappingWebService) {
        this.mappingWebService = mappingWebService;
    }

    public FeatureServiceInterface getFeatureWebService() {
        return featureWebService;
    }

    public void setFeatureWebService(FeatureServiceInterface featureWebService) {
        this.featureWebService = featureWebService;
    }

    private MappingServiceInterface mappingWebService;
    private FeatureServiceInterface featureWebService;

    public GeometryServiceInterface getGeometryWebService() {
        return geometryWebService;
    }

    public void setGeometryWebService(GeometryServiceInterface geometryWebService) {
        this.geometryWebService = geometryWebService;
    }

    /**
     * This will take in the parsed geometry and call Union only if the geometry passed is
     * a multipolygon (Case for Collections) having more than one polygons.
     * #Why call Union for only one?
     *
     * Do not like the way - but will need to do the right fit.
     * @param geometry
     * @return
     * @throws ServiceException
     */
    public Geometry getUnion(Geometry geometry) throws ServiceException {

        if (geometry instanceof MultiPolygon) {
            MultiPolygon multiPolygon = (MultiPolygon) geometry;

            if (multiPolygon.getPolygon().size() > 1) {
                GeometryList gList = new GeometryList();
                gList.getGeometry().addAll(multiPolygon.getPolygon());

                UnionRequest urq = new UnionRequest();
                urq.setGeometryList(gList);

                UnionResponse urp = this.geometryWebService.union(urq);

                if (urp != null) {
                    logger.debug(" Union returned success. ");
                    return urp.getGeometry();
                }
            }

            return multiPolygon;
        }
        return geometry;
    }
}
