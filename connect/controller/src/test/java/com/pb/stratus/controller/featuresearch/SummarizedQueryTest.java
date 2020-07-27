package com.pb.stratus.controller.featuresearch;

import com.mapinfo.midev.service.feature.v1.BoundParameter;
import com.mapinfo.midev.service.feature.ws.v1.ServiceException;
import com.mapinfo.midev.service.geometries.v1.Geometry;
import com.pb.stratus.controller.json.geojson.GeoJsonParser;
import com.pb.stratus.controller.queryutils.QueryMetadata;
import com.pb.stratus.controller.service.SearchByQueryParams;
import com.pb.stratus.controller.service.SearchWithinGeometryParams;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: VI012GU
 * Date: 9/16/14
 * Time: 8:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class SummarizedQueryTest {

    private SearchByQueryParams mockParams;
    private String TABLE_NAME = "Test_Table";
    private final String SOURCE_SRS_PARAM = "EPSG:3857";
    private QueryMetadata mockQueryMetadata;
    private List<BoundParameter> boundParams = new ArrayList<>();
    private SummarizedQuery sqlQueryForSummarizedData;

    private String pointGeometry = "{ \"type\": \"Point\", \"coordinates\": [100.0, 10.0] }";
    private String lineStringGeometry = "{ \"type\": \"LineString\",  \"coordinates\": [ [100.0, 0.0], [101.0, 1.0] ]  }";
    private String polygonGeometry = "{ \"type\": \"Polygon\",  \"coordinates\": [    [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], " +
            "[100.0, 1.0], [100.0, 0.0] ],    [ [100.2, 0.2], [100.8, 0.2], [100.8, 0.8], [100.2, 0.8], [100.2, 0.2] ]    ] }";
    private String multiPolygonGeometry = "{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100,100],[100,200],[200,200],[200,100],[100,100]]]]}";
    private String geometryCollectionGeometry = "{\"type\":\"GeometryCollection\",\"geometries\":[{\"type\":\"Polygon\"," +
            "\"coordinates\":[[[527802.12402344,184193.84765625],[527925.65917969,184357.66601563],[527963.25683594,184231.4453125]," +
            "[527802.12402344,184193.84765625]]]},{\"type\":\"Polygon\",\"coordinates\":[[[527479.85839844,184841.06445313]," +
            "[527492.796318291,184853.91868838295],[527503.564105464,184868.6386032402],[527511.8966213028,184884.86174454534]," +
            "[527517.5886916132,184902.1886447179],[527520.5001587333,184920.192657971],[527520.5593326801,184938.43046574644]," +
            "[527517.7647563946,184956.45299268892],[527512.1852416195,184973.81646437186],[527503.9581745259,184990.0933344963]," +
            "[527493.2861328099,185004.8828125],[527480.431897557,185017.820732351],[527465.7119826997,185028.58851952408]," +
            "[527449.4888413947,185036.92103536284],[527432.161941222,185042.61310567326],[527414.1579279689,185045.52457279337]," +
            "[527395.9201201935,185045.58374674013],[527377.897593251,185042.78917045458],[527360.5341215681,185037.20965567947]," +
            "[527344.2572514436,185028.982588586],[527329.46777344,185018.31054687],[527316.5298535889,185005.45631161705]," +
            "[527305.7620664159,184990.7363967598],[527297.4295505771,184974.51325545466],[527291.7374802667,184957.1863552821]," +
            "[527288.8260131466,184939.182342029],[527288.7668391998,184920.94453425356],[527291.5614154853,184902.92200731108]," +
            "[527297.1409302604,184885.55853562814],[527305.367997354,184869.2816655037],[527316.04003907,184854.4921875]," +
            "[527328.8942743229,184841.554267649],[527343.6141891802,184830.78648047592],[527359.8373304852,184822.45396463716]," +
            "[527377.1642306579,184816.76189432674],[527395.168243911,184813.85042720663],[527413.4060516864,184813.79125325987]," +
            "[527431.4285786289,184816.58582954542],[527448.7920503118,184822.16534432053],[527465.0689204363,184830.392411414]," +
            "[527479.85839844,184841.06445313]]]},{\"type\":\"Polygon\",\"coordinates\":[[[526829.6710313904,184708.04118138575]," +
            "[526810.5406196596,184709.29977997008],[526792.1197251446,184711.78598626453],[526774.8619314035,184715.4385816189]," +
            "[526759.1921825936,184720.16762701326],[526745.4963199357,184725.85667765525],[526734.1115810223,184732.36565023716]," +
            "[526725.3182959091,184739.53427225197],[526719.3329844588,184747.18602843443],[526716.3030249039,184755.13250715254]," +
            "[526716.3030249039,184763.17803972747],[526719.3329844588,184771.12451844558],[526725.3182959091,184778.77627462804]," +
            "[526734.1115810223,184785.94489664285],[526745.4963199357,184792.45386922476],[526759.1921825936,184798.14291986675]," +
            "[526774.8619314035,184802.87196526112],[526792.1197251446,184806.52456061548],[526810.5406196596,184809.01076690992]," +
            "[526829.6710313904,184810.26936549426],[526849.0399061096,184810.26936549426],[526868.1703178404,184809.01076690992]," +
            "[526886.5912123554,184806.52456061548],[526903.8490060965,184802.87196526112],[526919.5187549064,184798.14291986675]," +
            "[526933.2146175643,184792.45386922476],[526944.5993564777,184785.94489664285],[526953.3926415909,184778.77627462804]," +
            "[526959.3779530412,184771.12451844558],[526962.4079125961,184763.17803972747],[526962.4079125961,184755.13250715254]," +
            "[526959.3779530412,184747.18602843443],[526953.3926415909,184739.53427225197],[526944.5993564777,184732.36565023716]," +
            "[526933.2146175643,184725.85667765525],[526919.5187549064,184720.16762701326],[526903.8490060965,184715.4385816189]," +
            "[526886.5912123554,184711.78598626453],[526868.1703178404,184709.29977997008],[526849.0399061096,184708.04118138575]," +
            "[526829.6710313904,184708.04118138575]]]},{\"type\":\"Polygon\",\"coordinates\":[[[528293.57910156,184513.42773438]," +
            "[528280.15136719,184687.98828125012],[528105.5908203198,184674.56054688],[528119.01855469,184500.0000000099]," +
            "[528293.57910156,184513.42773438]]]},{\"type\":\"Polygon\",\"coordinates\":[[[528371.4599609398,184180.41992188]," +
            "[528371.4599609398,184338.8671875],[528562.1337890599,184338.8671875],[528562.1337890602,184180.41992188]," +
            "[528371.4599609398,184180.41992188]]]},{\"type\":\"Point\",\"coordinates\":[526977.66113281,184548.33984375]}," +
            "{\"type\":\"LineString\",\"coordinates\":[[527039.42871094,184244.87304688],[527391.23535156,184341.55273438]," +
            "[527522.82714844,184599.36523438],[527573.85253906,184639.6484375]]}]}";


    @Before
    public void setUp() throws ServiceException {
        mockParams = new SearchByQueryParams();
        mockParams.setTableName(TABLE_NAME);
        sqlQueryForSummarizedData = new SummarizedQuery();
    }

    @Test
    public void testCreateSQLQuery_Point_EntirelyWithIn() throws Exception {
        String expectedResult = "SELECT " +
                "count(*) AS COUNT, " +
                "SUM(\"ColA\") AS SUM_ColA, AVG(\"ColA\") AS AVG_ColA, MIN(\"ColA\") AS MIN_ColA, MAX(\"ColA\") AS MAX_ColA, " +
                "SUM(\"ColB\") AS SUM_ColB, AVG(\"ColB\") AS AVG_ColB, MIN(\"ColB\") AS MIN_ColB, MAX(\"ColB\") AS MAX_ColB " +
                "FROM \"Test_Table\" WHERE MI_Contains(@POINT,Obj)";

        mockParams.setSpatialOperation(SearchWithinGeometryParams.SpatialOperation.ENTIRELYWITHIN);
        List<String> numericColumnsList = Arrays.asList("ColA", "ColB");
        mockParams.setFunctionAttributes(getNumericColumnMap(numericColumnsList));
        mockParams.setGeometry(createGeometryObject(pointGeometry));
        String query = sqlQueryForSummarizedData.createSQLQuery(mockQueryMetadata, mockParams, true, boundParams, pointGeometry);
        //String diff = StringUtils.difference(expectedResult, query);
        assert(expectedResult.equals(query));

    }

    @Test
    public void testCreateSQLQuery_Point_Intersects() throws Exception {
        String expectedResult = "SELECT " +
                "count(*) AS COUNT, " +
                "SUM(\"ColA\") AS SUM_ColA, AVG(\"ColA\") AS AVG_ColA, MIN(\"ColA\") AS MIN_ColA, MAX(\"ColA\") AS MAX_ColA, " +
                "SUM(\"ColB\") AS SUM_ColB, AVG(\"ColB\") AS AVG_ColB, MIN(\"ColB\") AS MIN_ColB, MAX(\"ColB\") AS MAX_ColB " +
                "FROM \"Test_Table\" WHERE MI_Intersects(@POINT,Obj)";

        mockParams.setSpatialOperation(SearchWithinGeometryParams.SpatialOperation.INTERSECTS);
        List<String> numericColumnsList = Arrays.asList("ColA", "ColB");
        mockParams.setFunctionAttributes(getNumericColumnMap(numericColumnsList));
        mockParams.setGeometry(createGeometryObject(pointGeometry));
        String query = sqlQueryForSummarizedData.createSQLQuery(mockQueryMetadata, mockParams, true, boundParams, pointGeometry);
        //String diff = StringUtils.difference(expectedResult, query);
        assert(expectedResult.equals(query));
    }

    @Test
    public void testCreateSQLQuery_LineString_EntirelyWithIn() throws Exception {
        String expectedResult = "SELECT " +
                "count(*) AS COUNT, " +
                "SUM(\"ColA\") AS SUM_ColA, AVG(\"ColA\") AS AVG_ColA, MIN(\"ColA\") AS MIN_ColA, MAX(\"ColA\") AS MAX_ColA, " +
                "SUM(\"ColB\") AS SUM_ColB, AVG(\"ColB\") AS AVG_ColB, MIN(\"ColB\") AS MIN_ColB, MAX(\"ColB\") AS MAX_ColB " +
                "FROM \"Test_Table\" WHERE MI_Contains(@MULTICURVE,Obj)";

        mockParams.setSpatialOperation(SearchWithinGeometryParams.SpatialOperation.ENTIRELYWITHIN);
        List<String> numericColumnsList = Arrays.asList("ColA", "ColB");
        mockParams.setFunctionAttributes(getNumericColumnMap(numericColumnsList));
        mockParams.setGeometry(createGeometryObject(lineStringGeometry));
        String query = sqlQueryForSummarizedData.createSQLQuery(mockQueryMetadata, mockParams, true, boundParams, lineStringGeometry);
        assert(expectedResult.equals(query));

    }

    @Test
    public void testCreateSQLQuery_LineString_Intersects() throws Exception {
        String expectedResult = "SELECT " +
                "count(*) AS COUNT, " +
                "SUM(\"ColA\") AS SUM_ColA, AVG(\"ColA\") AS AVG_ColA, MIN(\"ColA\") AS MIN_ColA, MAX(\"ColA\") AS MAX_ColA, " +
                "SUM(\"ColB\") AS SUM_ColB, AVG(\"ColB\") AS AVG_ColB, MIN(\"ColB\") AS MIN_ColB, MAX(\"ColB\") AS MAX_ColB " +
                "FROM \"Test_Table\" WHERE MI_Intersects(@MULTICURVE,Obj)";

        mockParams.setSpatialOperation(SearchWithinGeometryParams.SpatialOperation.INTERSECTS);
        List<String> numericColumnsList = Arrays.asList("ColA", "ColB");
        mockParams.setFunctionAttributes(getNumericColumnMap(numericColumnsList));
        mockParams.setGeometry(createGeometryObject(lineStringGeometry));
        String query = sqlQueryForSummarizedData.createSQLQuery(mockQueryMetadata, mockParams, true, boundParams, lineStringGeometry);
        assert(expectedResult.equals(query));
    }

    @Test
    public void testCreateSQLQuery_Polygon_EntirelyWithIn() throws Exception {
        String expectedResult = "SELECT " +
                "count(*) AS COUNT, " +
                "SUM(\"ColA\") AS SUM_ColA, AVG(\"ColA\") AS AVG_ColA, MIN(\"ColA\") AS MIN_ColA, MAX(\"ColA\") AS MAX_ColA, " +
                "SUM(\"ColB\") AS SUM_ColB, AVG(\"ColB\") AS AVG_ColB, MIN(\"ColB\") AS MIN_ColB, MAX(\"ColB\") AS MAX_ColB " +
                "FROM \"Test_Table\" WHERE MI_Contains(@POLYGON,Obj)";

        mockParams.setSpatialOperation(SearchWithinGeometryParams.SpatialOperation.ENTIRELYWITHIN);
        List<String> numericColumnsList = Arrays.asList("ColA", "ColB");
        mockParams.setFunctionAttributes(getNumericColumnMap(numericColumnsList));
        mockParams.setGeometry(createGeometryObject(polygonGeometry));
        String query = sqlQueryForSummarizedData.createSQLQuery(mockQueryMetadata, mockParams, true, boundParams, polygonGeometry);
        //String diff = StringUtils.difference(expectedResult, query);
        assert(expectedResult.equals(query));

    }

    @Test
    public void testCreateSQLQuery_Polygon_Intersects() throws Exception {
        String expectedResult = "SELECT " +
                "count(*) AS COUNT, " +
                "SUM(\"ColA\") AS SUM_ColA, AVG(\"ColA\") AS AVG_ColA, MIN(\"ColA\") AS MIN_ColA, MAX(\"ColA\") AS MAX_ColA, " +
                "SUM(\"ColB\") AS SUM_ColB, AVG(\"ColB\") AS AVG_ColB, MIN(\"ColB\") AS MIN_ColB, MAX(\"ColB\") AS MAX_ColB " +
                "FROM \"Test_Table\" WHERE MI_Intersects(@POLYGON,Obj)";

        mockParams.setSpatialOperation(SearchWithinGeometryParams.SpatialOperation.INTERSECTS);
        List<String> numericColumnsList = Arrays.asList("ColA", "ColB");
        mockParams.setFunctionAttributes(getNumericColumnMap(numericColumnsList));
        mockParams.setGeometry(createGeometryObject(polygonGeometry));
        String query = sqlQueryForSummarizedData.createSQLQuery(mockQueryMetadata, mockParams, true, boundParams, polygonGeometry);
        //String diff = StringUtils.difference(expectedResult, query);
        assert(expectedResult.equals(query));
    }


    @Test
    public void testCreateSQLQuery_MultiPolygon_EntirelyWithIn() throws Exception {
        String expectedResult = "SELECT " +
                "count(*) AS COUNT, " +
                "SUM(\"ColA\") AS SUM_ColA, AVG(\"ColA\") AS AVG_ColA, MIN(\"ColA\") AS MIN_ColA, MAX(\"ColA\") AS MAX_ColA, " +
                "SUM(\"ColB\") AS SUM_ColB, AVG(\"ColB\") AS AVG_ColB, MIN(\"ColB\") AS MIN_ColB, MAX(\"ColB\") AS MAX_ColB " +
                "FROM \"Test_Table\" " +
                "WHERE MI_Contains(@MULTIPOLYGON,Obj)";

        mockParams.setSpatialOperation(SearchWithinGeometryParams.SpatialOperation.ENTIRELYWITHIN);
        List<String> numericColumnsList = Arrays.asList("ColA", "ColB");
        mockParams.setFunctionAttributes(getNumericColumnMap(numericColumnsList));
        mockParams.setGeometry(createGeometryObject(multiPolygonGeometry));
        String query = sqlQueryForSummarizedData.createSQLQuery(mockQueryMetadata, mockParams, true, boundParams, multiPolygonGeometry);
        assert(expectedResult.equals(query));
    }

    @Test
    public void testCreateSQLQuery_MultiPolygon_Intersects() throws Exception {
        String expectedResult = "SELECT " +
                "count(*) AS COUNT, " +
                "SUM(\"ColA\") AS SUM_ColA, AVG(\"ColA\") AS AVG_ColA, MIN(\"ColA\") AS MIN_ColA, MAX(\"ColA\") AS MAX_ColA, " +
                "SUM(\"ColB\") AS SUM_ColB, AVG(\"ColB\") AS AVG_ColB, MIN(\"ColB\") AS MIN_ColB, MAX(\"ColB\") AS MAX_ColB " +
                "FROM \"Test_Table\" " +
                "WHERE MI_Intersects(@MULTIPOLYGON,Obj)";

        mockParams.setSpatialOperation(SearchWithinGeometryParams.SpatialOperation.INTERSECTS);
        List<String> numericColumnsList = Arrays.asList("ColA", "ColB");
        mockParams.setFunctionAttributes(getNumericColumnMap(numericColumnsList));
        mockParams.setGeometry(createGeometryObject(multiPolygonGeometry));
        String query = sqlQueryForSummarizedData.createSQLQuery(mockQueryMetadata, mockParams, true, boundParams, multiPolygonGeometry);
        assert(expectedResult.equals(query));
    }

    @Test
    public void testCreateSQLQuery_MultiFeatureGeometry_EntirelyWithIn() throws Exception {
        String expectedResult = "SELECT " +
                "count(*) AS COUNT, " +
                "SUM(\"ColA\") AS SUM_ColA, AVG(\"ColA\") AS AVG_ColA, MIN(\"ColA\") AS MIN_ColA, MAX(\"ColA\") AS MAX_ColA, " +
                "SUM(\"ColB\") AS SUM_ColB, AVG(\"ColB\") AS AVG_ColB, MIN(\"ColB\") AS MIN_ColB, MAX(\"ColB\") AS MAX_ColB " +
                "FROM \"Test_Table\" WHERE MI_Contains(@MULTIFEATUREGEOMETRY,Obj)";

        mockParams.setSpatialOperation(SearchWithinGeometryParams.SpatialOperation.ENTIRELYWITHIN);
        List<String> numericColumnsList = Arrays.asList("ColA", "ColB");
        mockParams.setFunctionAttributes(getNumericColumnMap(numericColumnsList));
        mockParams.setGeometry(createGeometryObject(geometryCollectionGeometry));
        String query = sqlQueryForSummarizedData.createSQLQuery(mockQueryMetadata, mockParams, true, boundParams, geometryCollectionGeometry);
        //String diff = StringUtils.difference(expectedResult, query);
        assert(expectedResult.equals(query));

    }

    @Test
    public void testCreateSQLQuery_MultiFeatureGeometry_Intersects() throws Exception {
        String expectedResult = "SELECT " +
                "count(*) AS COUNT, " +
                "SUM(\"ColA\") AS SUM_ColA, AVG(\"ColA\") AS AVG_ColA, MIN(\"ColA\") AS MIN_ColA, MAX(\"ColA\") AS MAX_ColA, " +
                "SUM(\"ColB\") AS SUM_ColB, AVG(\"ColB\") AS AVG_ColB, MIN(\"ColB\") AS MIN_ColB, MAX(\"ColB\") AS MAX_ColB " +
                "FROM \"Test_Table\" WHERE MI_Intersects(@MULTIFEATUREGEOMETRY,Obj)";

        mockParams.setSpatialOperation(SearchWithinGeometryParams.SpatialOperation.INTERSECTS);
        List<String> numericColumnsList = Arrays.asList("ColA", "ColB");
        mockParams.setFunctionAttributes(getNumericColumnMap(numericColumnsList));
        mockParams.setGeometry(createGeometryObject(geometryCollectionGeometry));
        String query = sqlQueryForSummarizedData.createSQLQuery(mockQueryMetadata, mockParams, true, boundParams, geometryCollectionGeometry);
        String diff = StringUtils.difference(expectedResult, query);
        assert(expectedResult.equals(query));
    }

    private Map<String, List<String>> getNumericColumnMap (List<String> numericColumnsList) {
        Map<String, List<String>> functionAttributesMap = new LinkedHashMap<>();
        List<String> summarizeFunctionList = Arrays.asList("SUM", "AVG", "MIN", "MAX");
        for (String numericColumn : numericColumnsList) {
            functionAttributesMap.put(numericColumn, summarizeFunctionList);
        }
        return functionAttributesMap;
    }

    private Geometry createGeometryObject(String geometry) {
        if (!StringUtils.isBlank(geometry)) {
            GeoJsonParser parser = new GeoJsonParser(SOURCE_SRS_PARAM);
            return parser.parseGeometry(geometry);
        }
        return null;
    }
}
