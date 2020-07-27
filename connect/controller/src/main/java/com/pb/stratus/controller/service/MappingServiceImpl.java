package com.pb.stratus.controller.service;

import com.mapinfo.midev.service.geometries.v1.*;
import com.mapinfo.midev.service.geometries.v1.Point;
import com.mapinfo.midev.service.mapping.v1.*;
import com.mapinfo.midev.service.mapping.ws.v1.MappingServiceInterface;
import com.mapinfo.midev.service.mapping.ws.v1.ServiceException;
import com.mapinfo.midev.service.style.v1.*;
import com.mapinfo.midev.service.units.v1.Distance;
import com.mapinfo.midev.service.units.v1.DistanceUnit;
import com.pb.stratus.controller.annotation.Annotation;
import com.pb.stratus.controller.annotation.AnnotationStyle;
import com.pb.stratus.controller.annotation.PointAnnotationStyle;
import com.pb.stratus.controller.print.BoundingBox;
import com.pb.stratus.controller.thematic.StyleBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MappingServiceImpl implements MappingService {

    /**
     * Mapping service - the request shoot point
     */
    private MappingServiceInterface mappingService;
    /**
     * Tenant name is required for passing the NamedStyle request
     */
    private String tenantName;

    public MappingServiceImpl(MappingServiceInterface mappingWebService, String tenantName) {
        this.mappingService = mappingWebService;
        this.tenantName = tenantName;
    }

    public RenderNamedMapResponse getNamedMap(RenderNamedMapParams params)
            throws ServiceException {
        return this.mappingService.renderNamedMap(createRequest(params));
    }

    public RenderMapResponse getLayerMap(RenderMapParams params)
            throws ServiceException {
        return this.mappingService.renderMap(createRequest(params));
    }

    public DescribeNamedMapResponse describeNamedMap(
            DescribeNamedMapRequest describeNamedMap) throws ServiceException {
        return this.mappingService.describeNamedMap(describeNamedMap);
    }

    public DescribeNamedMapsResponse describeNamedMaps(
            DescribeNamedMapsRequest describeNamedMaps) throws ServiceException {
        return this.mappingService.describeNamedMaps(describeNamedMaps);
    }

    public DescribeNamedLayerResponse describeNamedLayer(
            DescribeNamedLayerRequest describeNamedLayer) throws ServiceException {
        return this.mappingService.describeNamedLayer(describeNamedLayer);
    }

    public List<RenderNamedMapResponse> getNamedMaps(
            List<RenderNamedMapParams> params) throws ServiceException {
        List<RenderNamedMapResponse> maps = new ArrayList<RenderNamedMapResponse>();
        for (RenderNamedMapParams mapParams : params) {
            maps.add(this.getNamedMap(mapParams));
        }

        return maps;
    }

    protected RenderNamedMapRequest createRequest(RenderNamedMapParams params)
    {
        RenderNamedMapRequest renderRequest = new RenderNamedMapRequest();
        ZoomAndCenterMapView mapView = new ZoomAndCenterMapView();
        mapView.setMapCenter(new Point());
        mapView.getMapCenter().setPos(new Pos());
        mapView.getMapCenter().setSrsName(params.getSrs());
        mapView.getMapCenter().getPos().setX(params.getXPos());
        mapView.getMapCenter().getPos().setY(params.getYPos());
        mapView.getMapCenter().setSrsName(params.getSrs());
        mapView.setZoomLevel(new Distance());
        mapView.getZoomLevel().setUom(DistanceUnit.METER);
        mapView.getZoomLevel().setValue(params.getZoom());
        mapView.setHeight(params.getHeight());
        mapView.setWidth(params.getWidth());
        mapView.setRendering(params.getRendering());
        renderRequest.setNamedMap(params.getMapName());
        renderRequest.setImageMimeType(params.getImageMimeType());
        renderRequest.setReturnImage(params.isReturnImage());
        renderRequest.setMapView(mapView);
        return renderRequest;
    }

    protected RenderMapRequest createRequest(RenderMapParams params)
    {
        RenderMapRequest renderRequest = new RenderMapRequest();
        ZoomAndCenterMapView mapView = new ZoomAndCenterMapView();
        mapView.setMapCenter(new Point());
        mapView.getMapCenter().setPos(new Pos());
        mapView.getMapCenter().setSrsName(params.getSrs());
        mapView.getMapCenter().getPos().setX(params.getXPos());
        mapView.getMapCenter().getPos().setY(params.getYPos());
        mapView.getMapCenter().setSrsName(params.getSrs());
        mapView.setZoomLevel(new Distance());
        mapView.getZoomLevel().setUom(DistanceUnit.METER);
        mapView.getZoomLevel().setValue(params.getZoom());
        mapView.setHeight(params.getHeight());
        mapView.setWidth(params.getWidth());
        mapView.setRendering(params.getRendering());

        Map map = new Map();
        List<Layer> layerList = map.getLayer();
        for(String layerName : params.getMapLayers())
        {
            NamedLayer layer = new NamedLayer();
            layer.setName(layerName);
            layerList.add(layer);
        }
        renderRequest.setMap(map);
        renderRequest.setImageMimeType(params.getImageMimeType());
        renderRequest.setReturnImage(params.isReturnImage());
        renderRequest.setMapView(mapView);
        return renderRequest;
    }

    private ZoomAndCenterMapView createMapView(MapViewPort mapViewPort)
    {
        ZoomAndCenterMapView mapView = new ZoomAndCenterMapView();
        mapView.setMapCenter(new com.mapinfo.midev.service.geometries.v1.Point());
        mapView.getMapCenter().setPos(new Pos());
        mapView.getMapCenter().setSrsName(mapViewPort.getMapSrs());
        mapView.getMapCenter().getPos().setX(mapViewPort.getMapXpos());
        mapView.getMapCenter().getPos().setY(mapViewPort.getMapYpos());
        mapView.setZoomLevel(new Distance());
        mapView.getZoomLevel().setUom(mapViewPort.getDistanceUnit());
        mapView.getZoomLevel().setValue(mapViewPort.getMapZoom());
        mapView.setHeight(mapViewPort.getMapHeight());
        mapView.setWidth(mapViewPort.getMapWidth());
        return mapView;

    }

    public List<Point> getMapScreenCenterLineStartEndEarthCoordinates(
            MapViewPort mapViewPort) throws ServiceException {

        ZoomAndCenterMapView mapView = createMapView(mapViewPort);
        ConvertXYToPointRequest convertXYToPointRequest = new ConvertXYToPointRequest();
        XYList screenPointList = new XYList();
        XY screenLeftMiddlePoint = new XY();
        screenLeftMiddlePoint.setX(0);
        screenLeftMiddlePoint.setY(mapView.getHeight() / 2);
        screenPointList.getXY().add(screenLeftMiddlePoint);

        XY screenRightMiddlePoint = new XY();
        screenRightMiddlePoint.setX(mapView.getWidth());
        screenRightMiddlePoint.setY(mapView.getHeight() / 2);
        screenPointList.getXY().add(screenRightMiddlePoint);
        convertXYToPointRequest.setXYList(screenPointList);
        convertXYToPointRequest.setMapView(mapView);
        return convertXYToPoint(convertXYToPointRequest).getPoint();
    }

    private ConvertXYToPointResponse convertXYToPoint(
            ConvertXYToPointRequest convertXYToPointRequest)
            throws ServiceException {
        ConvertXYToPointResponse convertXYToPointResponse = this.mappingService
                .convertXYToPoint(convertXYToPointRequest);
        return convertXYToPointResponse;
    }

    public ConvertPointToXYResponse convertPointToXY(
            ConvertPointToXYRequest request) throws ServiceException {
        return this.mappingService.convertPointToXY(request);
    }

    /**
     * Render map with the annotations presented
     * @param imageSize
     * @param boundingBox
     * @param annotations
     * @return
     * @throws ServiceException
     */
    public RenderMapResponse renderMapWithGeometry(Dimension imageSize,
                        BoundingBox boundingBox, List<Annotation> annotations, String displayUnit)
            throws ServiceException {

        RenderMapRequest renderMapRequest = new RenderMapRequest();

        ZoomAndCenterMapView mapView = new ZoomAndCenterMapView();
        mapView.setHeight(imageSize.getHeight());
        mapView.setWidth(imageSize.getWidth());

        // Setting Map Center
        Pos centerPos = new Pos();
        centerPos.setX(boundingBox.getCenter().getX());
        centerPos.setY(boundingBox.getCenter().getY());

        Point centerPoint = new Point();
        centerPoint.setSrsName(boundingBox.getSrs());
        centerPoint.setPos(centerPos);

        mapView.setMapCenter(centerPoint);

        // Setting Zoom Level
        Distance dist = new Distance();
        dist.setValue(boundingBox.getWidth());

        dist.setUom(DistanceUnit.METER);
		mapView.setZoomLevel(dist);

        // Initializing Map and adding Geometry there
        Map map = new Map();
        map.setName("Test Map");

        for (Annotation annotation : annotations) {
            GeometryLayer geometryLayer = new GeometryLayer();
            geometryLayer.setRenderable(true);
            geometryLayer.setDescription("Geometry layer for Features");
            GeometryList geometryList = new GeometryList();
            geometryList.getGeometry().add(annotation.getGeometry());
            geometryLayer.setGeometryList(geometryList);
            geometryLayer.setStyle(getAnnotationStyle(annotation));

            map.getLayer().add(geometryLayer);
        }

        renderMapRequest.setMapView(mapView);
        renderMapRequest.setMap(map);
        renderMapRequest.setReturnImage(true);

        // Invoking the Mapping Service with the filled Request
        return this.mappingService.renderMap(renderMapRequest);
    }

    private Style getAnnotationStyle(Annotation annotation)
    {
        AnnotationStyle annotationStyle = annotation.getStyle();
        String strokeColor = annotationStyle.getStrokeColor();
        String strokePattern = annotationStyle.getStrokePattern();
        String strokeWidth = annotationStyle.getStrokeWidth();

        int strokePatternCode = 2;
        if ("1 4".equalsIgnoreCase(strokePattern)) {
            strokePatternCode = 3;
        } else if ("6 4".equalsIgnoreCase(strokePattern)) {
            strokePatternCode = 5;
        } else {
            strokePatternCode = 2;
        }
           switch (annotation.getType()) {

            case LINE:
                return StyleBuilder.createLineStyle(strokeColor, String.valueOf(strokePatternCode), strokeWidth);

            case POINT:
                String shape  = ((PointAnnotationStyle) annotationStyle).getSpatialGraphicPointCode();
                String fillColor = annotationStyle.getFillColor();
                String border = ((PointAnnotationStyle) annotationStyle).getBorder();
                String pointRadius = ((PointAnnotationStyle) annotationStyle).getPointRadius();
                // factor of 1.7 is applied to make sure that point radius is updated to show exact print size.
                Long fontSize = Math.round(Integer.valueOf(pointRadius)*1.8);
                return StyleBuilder.createPointStyle(shape, fillColor, fontSize.toString(), "Mapinfo Symbols", border);

            default:

                return StyleBuilder.createPolygonStyle(strokeColor, annotationStyle.getSpatialGraphicCode(), strokeColor,
                        String.valueOf(strokePatternCode), strokeWidth);
        }
    }

    public RenderMapResponse renderMap(RenderMapParams params,Map mapObj) throws ServiceException {
        return this.mappingService.renderMap(createRenderMapRequest(params, mapObj));
    }

    private RenderMapRequest createRenderMapRequest(RenderMapParams params,Map mapObj)
    {
        RenderMapRequest renderRequest = new RenderMapRequest();
        ZoomAndCenterMapView mapView = new ZoomAndCenterMapView();
        mapView.setMapCenter(new Point());
        mapView.getMapCenter().setPos(new Pos());
        mapView.getMapCenter().setSrsName(params.getSrs());
        mapView.getMapCenter().getPos().setX(params.getXPos());
        mapView.getMapCenter().getPos().setY(params.getYPos());
        mapView.getMapCenter().setSrsName(params.getSrs());
        mapView.setZoomLevel(new Distance());
        mapView.getZoomLevel().setUom(DistanceUnit.METER);
        mapView.getZoomLevel().setValue(params.getZoom());
        mapView.setHeight(params.getHeight());
        mapView.setWidth(params.getWidth());
        mapView.setRendering(params.getRendering());

        renderRequest.setMap(mapObj);
        renderRequest.setImageMimeType(params.getImageMimeType());
        renderRequest.setReturnImage(params.isReturnImage());
        renderRequest.setMapView(mapView);
        return renderRequest;
    }
}
