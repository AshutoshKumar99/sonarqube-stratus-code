package com.pb.stratus.controller.geometry;

import com.mapinfo.midev.service.geometries.v1.Geometry;
import com.mapinfo.midev.service.geometries.v1.Point;
import com.mapinfo.midev.service.geometries.v1.Pos;
import com.mapinfo.midev.service.geometry.v1.*;
import com.mapinfo.midev.service.geometry.ws.v1.GeometryServiceInterface;
import com.mapinfo.midev.service.geometry.ws.v1.ServiceException;
import com.pb.stratus.controller.info.Feature;
import com.pb.stratus.controller.info.FeatureCollection;
import com.pb.stratus.controller.util.MockSupport;
import org.easymock.Capture;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test class for <code>GeometryServiceImpl</code>
 * 
 * @author ku002va
 *
 */
public class GeometryServiceImplTest
{
    private GeometryServiceImpl geometryService;
    private final static String COORD_SYS_BNG = "EPSG:27700";
    private final static String COORD_SYS_LONLAT = "EPSG:4326";
    
    private MockSupport mockSupport;

    @Before
    public void setUp() throws Exception
    {
        mockSupport = new MockSupport();
        createResolver(getCoordSysTransformResponse(),
                getCoordSysTransformsResponse());
        programMockSrsLookup();        
    }
    
    private void programMockSrsLookup()
    {
        SrsLookup mockLookup = new SrsLookup()
        {
            public boolean areSrsCodesEquivalent(String srs1, String srs2)
            {
                return srs1.equals(srs2);
            }
        };
        geometryService.setSrsLookup(mockLookup);
    }
    

    @Test
    public void testTransformPoints()
    {
        List<Point> points = new ArrayList<Point>();
        String sourceSrs = "sourceSrs";
        points.add(createPoint(1, 2, sourceSrs));
        points.add(createPoint(3, 4, sourceSrs));
        points.add(createPoint(5, 6, sourceSrs));
        String targetSrs = "targetSrs";
        List<Geometry> result = geometryService.transformPoints(points,
                targetSrs);
        Point result1 = (Point) result.get(0);
        Point result2 = (Point) result.get(1);
        assertEquals(result1.getPos().getX(), -0.15733379906554382, 0);
        assertEquals(result1.getPos().getY(), 51.54213178319978, 0);
        assertEquals(result2.getPos().getX(), -0.15733379906554382, 0);
        assertEquals(result2.getPos().getY(), 51.54213178319978, 0);
    }
    
    private Point createPoint(double x, double y, String srs)
    {
        Pos pos = new Pos();
        pos.setX(x);
        pos.setY(y);
        Point point = new Point();
        point.setPos(pos);
        point.setSrsName(srs);
        return point;
    }

    @Test
    public void testTransformGeometry() throws ServiceException
    {
        Pos pos = new Pos();
        pos.setX(527880.5);
        pos.setY(184196.25);

        Point point = new Point();
        point.setPos(pos);
        point.setSrsName(COORD_SYS_BNG);
        Geometry result = geometryService.transformGeometry(point, COORD_SYS_LONLAT);
        assertEquals(result.getSrsName(),COORD_SYS_LONLAT);
    }
    
    @Test
    public void testTransformGeometrySameTargetSrs() throws Exception
    {
        Point p = createPoint(1, 2, "testSrs");
        GeometryServiceInterface mockWebService = createMock(
                GeometryServiceInterface.class);
        replay(mockWebService);
        geometryService.setGeometryWebService(mockWebService);
        Point transformed = (Point) geometryService.transformGeometry(p, 
                "testSrs");
        assertEquals(p, transformed);
        verify(mockWebService);
    }

    /**
     * Tests the transform method for optimisation.
     * The service request for transform of collection of Geometry should only execute
     * if at least one of the constituent Geometry has SRS value different from target SRS.
     * 
     * @throws ServiceException
     */
    @Test
    public void testTransformGeometriesOpt() throws ServiceException
    {
    	List<Geometry> testGeometries = createTestGeometriesWithCommonSrs(COORD_SYS_BNG);
    	List<Geometry> expectedGeometries = testGeometries;
        
    	List<String> codeSpace = new ArrayList<String>();
    	codeSpace.add(COORD_SYS_BNG);
    	codeSpace.add(COORD_SYS_LONLAT);

    	ListCodeSpacesResponse response = new ListCodeSpacesResponse();
    	response.getCodeSpace().add(COORD_SYS_BNG);
    	response.getCodeSpace().add(COORD_SYS_BNG);
    	
    	ListCodeSpacesRequest requestCoord = new ListCodeSpacesRequest();
    	requestCoord.setResponseSrsName(COORD_SYS_BNG);
    	
    	GeometryServiceInterface service = createMock(GeometryServiceInterface.class);   	
        replay(service);   

    	SrsLookup srsLookup = createMock(SrsLookup.class);   	
    	String testSrs = anyObject();
    	String testTargetSrs = anyObject();
    	expect(srsLookup.areSrsCodesEquivalent(testSrs ,testTargetSrs)).andReturn(true);
        expectLastCall().anyTimes();
        replay(srsLookup);   

        geometryService.setGeometryWebService(service);
        geometryService.setSrsLookup(srsLookup);
        
        List<Geometry> result = geometryService.transformGeometries(testGeometries, COORD_SYS_BNG);
        assertNotNull(result);
        assertNotNull(result.get(0).getSrsName());
        assertEquals(expectedGeometries.get(0), result.get(0));
        assertEquals(expectedGeometries.get(1), result.get(1));
        verify(service);
        verify(srsLookup);
    }
    
    @Test
    public void testTransformGeometries() throws ServiceException
    {
           
    	List<Geometry> testGeometries = createTestGeometries();
    	List<Geometry> expectedGeometries = createTestGeometriesWithCommonSrs(COORD_SYS_BNG);
    	List<String> codeSpace = new ArrayList<String>();
    	codeSpace.add(COORD_SYS_BNG);
    	codeSpace.add(COORD_SYS_LONLAT);

    	ListCodeSpacesResponse responseCodeSpace = new ListCodeSpacesResponse();
    	responseCodeSpace.getCodeSpace().add(COORD_SYS_BNG);
    	responseCodeSpace.getCodeSpace().add(COORD_SYS_LONLAT);
    	
    	ListCodeSpacesRequest requestCoord = new ListCodeSpacesRequest();
    	requestCoord.setResponseSrsName(COORD_SYS_BNG);
    	
    	CoordSysTransformsResponse response = new CoordSysTransformsResponse();
    	response.getGeometry().addAll(expectedGeometries);
    	
    	GeometryServiceInterface service = createMock(GeometryServiceInterface.class);   	
    	CoordSysTransformsRequest request = anyObject(); 
    	expect(service.coordSysTransforms(request)).andReturn(response);
        replay(service);   

    	SrsLookup srsLookup = createMock(SrsLookup.class);  
    	String testSrs = anyObject();
    	String testTargetSrs = anyObject();
    	expect(srsLookup.areSrsCodesEquivalent(testSrs ,testTargetSrs)).andReturn(false);

    	expectLastCall().anyTimes();
        replay(srsLookup);   

        geometryService.setGeometryWebService(service);
        geometryService.setSrsLookup(srsLookup);
        
        List<Geometry> result = geometryService.transformGeometries(testGeometries, COORD_SYS_LONLAT);
        assertNotNull(result);
        assertNotNull(result.get(0));
        assertEquals(expectedGeometries.get(0), result.get(0));
        assertEquals(expectedGeometries.get(1), result.get(1));
        verify(service);
        verify(srsLookup);
    }
    
    @Test
    public void testTransformGeometriesRequestedSrsReturned() throws Exception
    {
        String targetSrs = "targetSrs";
        programMockWebServiceForMultiTransform(targetSrs);
        List<Geometry> p = new LinkedList<Geometry>();
        p.add(createPoint(1, 2, "sourceSrs"));
        p.add(createPoint(1, 2, "sourceSrs"));
        List<Geometry> transformed = geometryService
                .transformGeometries(p, "targetSrs");
        for (Geometry g : transformed)
        {
            assertEquals(targetSrs, g.getSrsName());
        }
    }
    
    @Test
    public void testTransformPos() throws ServiceException
    {
        Pos pos = new Pos();
        pos.setX(527880.5);
        pos.setY(184196.25);
        Pos result = geometryService
                .transformPos(pos, COORD_SYS_BNG, COORD_SYS_LONLAT);
        assertEquals(result.getX(), -0.15733379906554382, 0);
        assertEquals(result.getY(), 51.54213178319978, 0);

    }
    
    @Test
    public void testTransformFeatures() throws Exception
    {
        List<Feature> features = new LinkedList<Feature>();
        String sourceSrs = "sourceSrs";
        features.add(createFeature(1, 2, sourceSrs));
        features.add(createFeature(3, 4, sourceSrs));
        features.add(createFeature(5, 6, sourceSrs));
        String targetSrs = "targetSrs";
        programMockWebServiceForMultiTransform(targetSrs);
        FeatureCollection coll = geometryService
                .transformFeatureCollection(features, targetSrs);
        assertEquals(3, coll.getFeatures().size());
        assertTransformed(coll.getFeatures(), targetSrs);
        mockSupport.verifyAllMocks();
    }
    
    private void assertTransformed(List<Feature> transformed, String targetSrs)
    {
        for (Feature f : transformed)
        {
            Point t = (Point) f.getGeometry();
            assertEquals(1234, t.getPos().getX(), 0d);
            assertEquals(5678, t.getPos().getY(), 0d);
            assertEquals(targetSrs, t.getSrsName());
        }
        // TODO Auto-generated method stub
        
    }

    private Feature createFeature(double x, double y, String srs)
    {
        Point p = createPoint(x, y, srs);
        Map<String, Object> attrs = Collections.emptyMap();
        Feature f = new Feature(p, attrs);
        return f;
    }

    /**
     * creates a collection of test geometries.
     * 
     * @return testPoints a collection of geometries.
     */
    private List<Geometry> createTestGeometries()
    {
     	//the test geometry points List
    	List<Geometry> testPoints = new ArrayList<Geometry>();
        Point point1 = new Point();
        Pos pos1 = new Pos();
        pos1.setX(527880.5);
        pos1.setY(184196.25);
        point1.setPos(pos1);
        point1.setSrsName(COORD_SYS_LONLAT);

        Pos pos2 = new Pos();
        pos2.setX(527680.5);
        pos2.setY(184896.25);
        Point point2 = new Point();
        point2.setPos(pos1);
        point2.setSrsName(COORD_SYS_BNG);

        testPoints.add(point1);
        testPoints.add(point2);
        return testPoints;
    }
    
    /**
     * creates a collection of test geometries having same SRS name.
     * 
     * @return testPoints a collection of geometries.
     */
    private List<Geometry> createTestGeometriesWithCommonSrs(String SrsName)
    {
     	//the test geometry points List
    	List<Geometry> testPoints = new ArrayList<Geometry>();
        Point point1 = new Point();
        Pos pos1 = new Pos();
        pos1.setX(527880.5);
        pos1.setY(184196.25);
        point1.setPos(pos1);
        point1.setSrsName(SrsName);

        Pos pos2 = new Pos();
        pos2.setX(527680.5);
        pos2.setY(184896.25);
        Point point2 = new Point();
        point2.setPos(pos1);
        point2.setSrsName(SrsName);

        testPoints.add(point1);
        testPoints.add(point2);
        return testPoints;
    }
    
    private Capture<CoordSysTransformsRequest> programMockWebServiceForMultiTransform(final String targetSrs) 
            throws Exception
    {
        GeometryServiceInterface mockService = createMock(
                GeometryServiceInterface.class);
        geometryService.setGeometryWebService(mockService);
        Capture<CoordSysTransformsRequest> capture 
                = new Capture<CoordSysTransformsRequest>();
        
        IAnswer<CoordSysTransformsResponse> answer 
                = new IAnswer<CoordSysTransformsResponse>()
        {
            public CoordSysTransformsResponse answer() throws Throwable
            {
                CoordSysTransformsRequest req = (CoordSysTransformsRequest) 
                        getCurrentArguments()[0];
                CoordSysTransformsResponse resp 
                        = new CoordSysTransformsResponse();
                List<Geometry> transformed = resp.getGeometry();
                for (Geometry geom : req.getGeometry())
                {
                    transformed.add(createPoint(1234, 5678, targetSrs
                            .toLowerCase()));
                }
                return resp;
            }
        };
        expect(mockService.coordSysTransforms(capture(capture))).andAnswer(answer);
        replay(mockService);
        return capture;
    }
    
    private void createResolver(
            final CoordSysTransformResponse coordSysTransformResponse,
            final CoordSysTransformsResponse coordSysTransformsResponse)
            throws Exception
    {
        
        InvocationHandler h = new InvocationHandler()
        {
            public Object invoke(Object proxy, Method method, Object[] args)
                    throws Throwable
            {
                if (method.getName().equals("coordSysTransform"))
                {
                    return coordSysTransformResponse;
                }
                else if (method.getName().equals("coordSysTransforms"))
                {
                    return coordSysTransformsResponse;
                }
                else
                {

                    throw new UnsupportedOperationException();
                }
            }
        };
        GeometryServiceInterface service = (GeometryServiceInterface) Proxy
                .newProxyInstance(this.getClass().getClassLoader(),
                        new Class[] { GeometryServiceInterface.class }, h);
        geometryService = new GeometryServiceImpl();
        geometryService.setGeometryWebService(service);
    }

    private CoordSysTransformResponse getCoordSysTransformResponse()
    {
        CoordSysTransformResponse result = new CoordSysTransformResponse();

        Pos pos = new Pos();
        pos.setX(-0.15733379906554382);
        pos.setY(51.54213178319978);

        Point point = new Point();
        point.setPos(pos);
        point.setSrsName(COORD_SYS_LONLAT);
        result.setGeometry(point);
        return result;
    }

    private CoordSysTransformsResponse getCoordSysTransformsResponse()
    {
        CoordSysTransformsResponse result = new CoordSysTransformsResponse();

        Pos pos1 = new Pos();
        pos1.setX(-0.15733379906554382);
        pos1.setY(51.54213178319978);
        Point point1 = new Point();
        point1.setPos(pos1);
        point1.setSrsName(COORD_SYS_LONLAT);

        Pos pos2 = new Pos();
        pos2.setX(-0.15733379906554382);
        pos2.setY(51.54213178319978);
        Point point2 = new Point();
        point2.setPos(pos1);
        point2.setSrsName(COORD_SYS_LONLAT);

        result.getGeometry().add(point1);
        result.getGeometry().add(point2);

        return result;
    }
}
