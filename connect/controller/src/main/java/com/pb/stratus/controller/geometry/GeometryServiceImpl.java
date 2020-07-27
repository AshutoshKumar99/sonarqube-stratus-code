package com.pb.stratus.controller.geometry;

import com.mapinfo.midev.service.geometries.v1.*;
import com.mapinfo.midev.service.geometry.v1.*;
import com.mapinfo.midev.service.geometry.ws.v1.GeometryServiceInterface;
import com.mapinfo.midev.service.geometry.ws.v1.ServiceException;
import com.mapinfo.midev.service.units.v1.Area;
import com.mapinfo.midev.service.units.v1.Distance;
import com.mapinfo.midev.service.units.v1.DistanceUnit;
import com.pb.stratus.controller.ExceptionConverter;
import com.pb.stratus.controller.FeatureServiceExceptionConverter;
import com.pb.stratus.controller.info.Feature;
import com.pb.stratus.controller.info.FeatureCollection;
import com.pb.stratus.core.configuration.ControllerConfiguration;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import uk.co.graphdata.utilities.contract.Contract;

import java.util.LinkedList;
import java.util.List;

/**
 * GeometryService Implementation class, utilise the Midev
 * <code>GeometryService</code> for implementing transformation services.
 * Also implements optimised transform methods.
 *
 * @author ku002va
 */
public class GeometryServiceImpl implements GeometryService {

    private static final Logger logger = LogManager
            .getLogger(GeometryServiceImpl.class);

    private GeometryServiceInterface service;

    private SrsLookup srsLookup;

    //XXX we're not using the feature service so another converter would
    //    be more appropriate
    private ExceptionConverter conv = new FeatureServiceExceptionConverter();


    public GeometryServiceImpl(GeometryServiceInterface geometryService, ControllerConfiguration config) {
        this.service = geometryService;
        SrsLookupImpl sli = new SrsLookupImpl();
        sli.setGeometryService(service);
        srsLookup = sli;
    }

    /**
     * Constructor for JUNIT only
     */
    GeometryServiceImpl() {
    }

    void setGeometryWebService(GeometryServiceInterface
                                       geometryServiceInterface) {
        service = geometryServiceInterface;
    }

    public List<Geometry> transformPoints(List<Point> points, String targetSrs) {
        Contract.pre(points != null, "Points required");
        Contract.pre(targetSrs != null, "Target SRS required");

        CoordSysTransformsRequest request = new CoordSysTransformsRequest();
        request.getGeometry().addAll(points);
        request.setResponseSrsName(targetSrs);
        try {
            CoordSysTransformsResponse response = service
                    .coordSysTransforms(request);
            return response.getGeometry();
        } catch (ServiceException exception) {
            throw conv.convert(exception);
        }
    }

    public Geometry transformGeometry(Geometry geometry, String targetSrs) throws ServiceException {
        Contract.pre(geometry != null, "Geometry required");
        Contract.pre(targetSrs != null, "Target SRS required");

        if (srsLookup.areSrsCodesEquivalent(targetSrs, geometry.getSrsName())) {
            return geometry;
        }
        CoordSysTransformRequest request = new CoordSysTransformRequest();
        request.setGeometry(geometry);
        request.setResponseSrsName(targetSrs);
        CoordSysTransformResponse response = service.coordSysTransform(request);
        return response.getGeometry();
    }

    /**
     * Transforms a collection of geometry into a specified target coordinate
     * system.
     *
     * @param geometryList a collection of geometries to be transformed.
     * @param targetSrs    the coordinate system to transform the geometries
     *                     to.
     * @return a collection of transformed geometries. The order of the list
     *         items corresponds to the order in <code>geometryList</code>.
     */
    public List<Geometry> transformGeometries(List<Geometry> geometryList,
                                              String targetSrs) {
        Contract.pre(geometryList != null, "Geometies required");
        Contract.pre(targetSrs != null, "Target SRS required");

        CoordSysTransformsRequest request = new CoordSysTransformsRequest();
        if (!isTransformRequired(geometryList, targetSrs)) {
            return geometryList;
        }
        //Add the geometry individually to the request.
        for (Geometry geometry : geometryList) {
            request.getGeometry().add(geometry);
        }
        //set the target coordinate system. All geometry in the collection will be transformed into 
        //this target coordinate system.
        request.setResponseSrsName(targetSrs);

        CoordSysTransformsResponse response;
        try {
            response = service.coordSysTransforms(request);
        } catch (ServiceException sx) {
            throw conv.convert(sx);
        }
        List<Geometry> geom = response.getGeometry();
        changeSrsTo(geom, targetSrs);
        return geom;
    }

    private void changeSrsTo(List<Geometry> geometries, String srs) {
        for (Geometry g : geometries) {
            g.setSrsName(srs);
        }
    }

    /**
     * Checks if srs asociated with every individual geometry in the List is the same
     * as the target geometry.
     *
     * @param geometryList a collection of geometries.
     * @param targetSrs    the target srs
     * @return if true, indicates the srs associated with atleast one geometry in the
     *         input List is not the same as the targetSrs.
     */
    private boolean isTransformRequired(List<Geometry> geometryList, String targetSrs) {
        for (Geometry geometry : geometryList) {
            if (!srsLookup.areSrsCodesEquivalent(geometry.getSrsName(), targetSrs)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param srsLookup instance of type SrsLookup
     */
    public void setSrsLookup(SrsLookup srsLookup) {
        this.srsLookup = srsLookup;
    }

    /**
     * Transforms a Pos from one coordinate system to another
     *
     * @param position  The original position
     * @param sourceSrs Source Coordinate System
     * @param targetSrs Target Coordinate System
     * @return the transformed position
     */
    public Pos transformPos(Pos position, String sourceSrs,
                            String targetSrs) throws ServiceException {
        Contract.pre(position != null, "Position required");
        Contract.pre(sourceSrs != null, "Source SRS");
        Contract.pre(targetSrs != null, "Target SRS required");

        Point point = new Point();
        point.setPos(position);
        point.setSrsName(sourceSrs);
        Geometry result = transformGeometry(point, targetSrs);
        Point resultPoint = (Point) result;

        return resultPoint.getPos();
    }

    /**
     * Transforms a collection of <code>Feature</code> objects to a
     * specified coordinate system.
     *
     * @param features  a collection of <code>Feature</code> objects be transformed
     *                  to a target coordinate system.
     * @param targetSrs target coordinate system
     * @return tranformed collection of <code>Feature</code> Objects.
     */
    public FeatureCollection transformFeatureCollection(List<Feature> features,
                                                        String targetSrs) {
        List<Geometry> extracted = extractGeometries(features);
        List<Geometry> transformed = transformGeometries(extracted, targetSrs);
        List<Feature> merged = mergeGeometriesIntoFeatures(transformed, features);
        return new FeatureCollection(merged);
    }

    private List<Feature> mergeGeometriesIntoFeatures(
            List<Geometry> geometries, List<Feature> features) {
        for (int i = 0; i < features.size(); i++) {
            Feature f = features.get(i);
            f.setGeometry(geometries.get(i));
        }
        return features;
    }

    private List<Geometry> extractGeometries(List<Feature> features) {
        List<Geometry> geoms = new LinkedList<Geometry>();
        for (Feature f : features) {
            geoms.add(f.getGeometry());
        }
        return geoms;
    }

    public Distance getDistanceBetweenTwoPoints(List<Point> points) {
        DistanceRequest distanceRequest = new DistanceRequest();
        distanceRequest.setDistanceUnit(DistanceUnit.METER);
        distanceRequest.setComputationType(ComputationType.SPHERICAL);
        Point earthLeftMiddlePoint = points.get(0);
        Point earthRightMiddlePoint = points.get(1);
        distanceRequest.setFirstGeometry(earthLeftMiddlePoint);
        distanceRequest.setSecondGeometry(earthRightMiddlePoint);
        DistanceResponse distResponse = null;
        try {
            distResponse = this.service.distance(distanceRequest);
            return distResponse.getDistance();
        } catch (ServiceException sx) {
            throw conv.convert(sx);
        }
    }

    @Override
    public Measure getAreaOfTheGeometry(AreaRequest areaRequest)
            throws ServiceException {
        AreaResponse areaResponse = service.area(areaRequest);
        Area area = areaResponse.getArea();
        Measure measure = new Measure(MeasurementEnum.AREA.value(), area.getValue(),
                area.getUom().value());
        return measure;
    }

    @Override
    public Measure getLengthOfTheGeometry(LengthRequest lengthRequest)
            throws ServiceException {
        LengthResponse lengthResponse = service.length(lengthRequest);
        Distance distance = lengthResponse.getLength();
        Measure measure = new Measure(MeasurementEnum.LENGTH.value(),
                distance.getValue(), distance.getUom().value());
        return measure;
    }

    @Override
    public Measure getPerimeterOfTheGeometry(PerimeterRequest perimeterRequest) throws ServiceException {
        PerimeterResponse perimeterResponse = service.perimeter(perimeterRequest);
        Distance perimeter = perimeterResponse.getLength();
        Measure measure = new Measure(MeasurementEnum.PERIMETER.value(),
                perimeter.getValue(), perimeter.getUom().value());
        return measure;
    }

    @Override
    public BufferResponse getBufferedGeometry(BufferRequest bufferRequest)
            throws ServiceException {
        BufferResponse bufferResponse = service.buffer(bufferRequest);
        return bufferResponse;
    }


}
