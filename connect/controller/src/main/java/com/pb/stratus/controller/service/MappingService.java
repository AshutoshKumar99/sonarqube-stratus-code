package com.pb.stratus.controller.service;

import com.mapinfo.midev.service.geometries.v1.Point;
import com.mapinfo.midev.service.mapping.v1.*;
import com.mapinfo.midev.service.mapping.ws.v1.ServiceException;
import com.pb.stratus.controller.annotation.Annotation;
import com.pb.stratus.controller.print.BoundingBox;

import java.awt.*;
import java.util.List;

/**
 * Provides a single interface to access mapping services required by all
 * actions 
 */
public interface MappingService
{
    /**
     * Calls MiDev service to get the requested Named Map.
     * 
     * @param params
     * @return RenderNamedMapResponse
     * @throws ServiceException
     */
    RenderNamedMapResponse getNamedMap(RenderNamedMapParams params)
            throws ServiceException;

    /**
     * Calls MiDev service to get the requested Map
     *
     * @param params
     * @return RenderMapResponse
     * @throws ServiceException
     */
    RenderMapResponse getLayerMap(RenderMapParams params)
            throws ServiceException;

    /**
     * Makes multiple request to MiDev RenderNamedMap service and returns the
     * list of all the responses.
     * 
     * @param params
     * @return List<RenderNamedMapResponse>
     * @throws ServiceException
     */
    List<RenderNamedMapResponse> getNamedMaps(List<RenderNamedMapParams> params)
            throws ServiceException;
    
    /**
     * Convert the given point in pixels
     * @param request
     * @return ConvertPointToXYResponse
     * @throws ServiceException
     */
    ConvertPointToXYResponse convertPointToXY(ConvertPointToXYRequest request)
            throws ServiceException;
    
    /**
     *        __________
     *       |          |
     *       |__________|
     *       |          |
     *       |__________|
     * Used to return list consist of two points.
     * 0 - Point at this index represents earth coordinates for start point of middle 
     * line from Left
     * 1 - Point at this index represents earth coordinates for end point of middle line 
     * Suppose figure above is a map screen than start point of center line is point 1
     * and end point is point 2
     * @param mapViewPort
     * @return - List of points
     * @throws ServiceException
     */
    List<Point> getMapScreenCenterLineStartEndEarthCoordinates(MapViewPort mapViewPort)
            throws ServiceException;

	/**
	 * This method will be used to invoke spatial server operation renderMap
	 * that will take a list of geometry and will return an image with all those
	 * geometry in that
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public RenderMapResponse renderMapWithGeometry(Dimension imageSize,BoundingBox boundingBox,
            List<Annotation> annotations, String displayUnit) throws ServiceException;


    /**
     * This method will be used to invoke spatial server operation DescribeNamedMap
     * that will take DescribeNamedMapRequest object and will return an DescribeNamedMapResponse
     * object.
     *
     * @return
     * @throws ServiceException
     */
    public DescribeNamedMapResponse describeNamedMap(
            DescribeNamedMapRequest describeNamedMap) throws ServiceException;


    public DescribeNamedMapsResponse describeNamedMaps(
            DescribeNamedMapsRequest describeNamedMaps) throws ServiceException;

    /**
     * This method will be used to invoke spatial server operation DescribeNamedMap
     * that will take DescribeNamedMapRequest object and will return an DescribeNamedMapResponse
     * object.
     *
     * @return
     * @throws ServiceException
     */
    public DescribeNamedLayerResponse describeNamedLayer(
            DescribeNamedLayerRequest describeNamedLayer) throws ServiceException;

    /**
     * This method will be used to invoke spatial server operation RenderMap2
     * that will take RenderMapRequest object and will return an RenderMapResponse
     * object.
     *
     * @return
     * @throws ServiceException
     */
    public RenderMapResponse renderMap(RenderMapParams params,Map mapObj) throws ServiceException;


}
