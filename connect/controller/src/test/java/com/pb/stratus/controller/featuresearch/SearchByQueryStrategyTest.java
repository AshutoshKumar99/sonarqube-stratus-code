package com.pb.stratus.controller.featuresearch;

import com.mapinfo.midev.service.feature.v1.*;
import com.mapinfo.midev.service.feature.ws.v1.FeatureServiceInterface;
import com.mapinfo.midev.service.feature.ws.v1.ServiceException;
import com.mapinfo.midev.service.featurecollection.v1.*;
import com.mapinfo.midev.service.geometries.v1.*;
import com.mapinfo.midev.service.table.v1.TableMetadata;
import com.pb.stratus.controller.i18n.LocaleResolver;
import com.pb.stratus.controller.service.SearchByQueryParams;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import static com.pb.stratus.controller.Constants.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class SearchByQueryStrategyTest {

    private FeatureServiceInterface mockFeatureWebService;

    private SearchByQueryStrategy strategy;

    private FeatureSearchResultConverter mockConverter;

    //private DescribeTableResponse mockResponse;

    private SearchByQueryParams mockParams;

    @Before
    public void setUp() throws ServiceException {
        mockFeatureWebService = mock(FeatureServiceInterface.class);
        mockConverter = mock(FeatureSearchResultConverter.class);
        FeatureSearchResultConverterFactory mockFactory
                = mock(FeatureSearchResultConverterFactory.class);
        when(mockFactory.createConverter()).thenReturn(mockConverter);
        strategy = new SearchByQueryStrategy(mockFeatureWebService,
                mockFactory, 1234);
        mockParams = new SearchByQueryParams();
        mockParams.setTableName("someTable");
        mockParams.setParams("{\"0\":[\"897656\"]}");
        mockParams.setPath("src" + File.separatorChar + "test" + File.separatorChar + "resources" + File.separatorChar + "com" + File.separatorChar + "pb" + File.separatorChar + "stratus" + File.separatorChar + "controller" + File.separatorChar + "action" + File.separatorChar + "stratus" + File.separatorChar + "queryconfig" + File.separatorChar + "planningApplications" + File.separatorChar + "SampleQueryData.xml");
        mockParams.setQueryName("testQuery");

        DescribeTableResponse mockResponse = createMockResponse();
        when(
                mockFeatureWebService
                        .describeTable(any(DescribeTableRequest.class)))
                .thenReturn(mockResponse);

    }

    private DescribeTableResponse createMockResponse() {
        DescribeTableResponse response = new DescribeTableResponse();
        TableMetadata metadata = new TableMetadata();
        response.setTableMetadata(metadata);
        AttributeDefinitionList attList = new AttributeDefinitionList();
        metadata.setAttributeDefinitionList(attList);
        GeometryAttributeDefinition attDef = new GeometryAttributeDefinition();
        attDef.setDataType(AttributeDataType.GEOMETRY);
        attDef.setName("someName");
        ScalarAttributeDefinition miPrinxAttrDefinition = new ScalarAttributeDefinition();
        miPrinxAttrDefinition.setDataType(AttributeDataType.DOUBLE);
        miPrinxAttrDefinition.setName("MI_PRINX");
        attList.getAttributeDefinition().add(miPrinxAttrDefinition);
        attList.getAttributeDefinition().add(attDef);
        return response;
    }

    @Test
    public void shouldAddAllAttributesToExpression() throws Exception {
        mockParams.getAttributes().add("attribute1");
        mockParams.getAttributes().add("attribute2");
        assertExpressionMatches("select \"attribute1\", \"attribute2\", \"MI_KEY\" from \"someTable\" where Lower\\(\"AppNumber\"\\) = Lower\\('897656'\\)", captureSearchBySqlRequest().getSQL());
    }


    @Test
    public void shouldUseAsteriskIfNoAttributes() throws Exception {
        String pattern = Pattern.quote("select * from");
        assertExpressionMatches(pattern + " .*", captureSearchBySqlRequest().getSQL());
    }

    @Test
    public void shouldUseCorrectTableInExpression() throws Exception {
        assertExpressionMatches("select .* from \"someTable\"\\s?.*", captureSearchBySqlRequest().getSQL());
    }


    @Test
    public void shouldIncludeGeometryColumnIfRequested() throws Exception {
        mockParams.getAttributes().add("attribute1");
        mockParams.setIncludeGeometry(true);
        setUpMockDescibeTableCall();
        assertExpressionMatches("select \"attribute1\", \"someName\", \"MI_KEY\" from .*", captureSearchBySqlRequest().getSQL());
    }

    @Test
    public void shouldNotIncludeGeometryColumnIfAllAttributesAreRequested()
            throws Exception {
        mockParams.setIncludeGeometry(true);
        setUpMockDescibeTableCall();
        String pattern = Pattern.quote("select * from");
        assertExpressionMatches(pattern + " .*", captureSearchBySqlRequest().getSQL());
    }

    @Test
    public void shouldCreateSQLQueryWithOROnStringAttribute() throws Exception {
        mockParams.getAttributes().add("attribute1");
        mockParams.setParams("{\"0\":[\"897656\", \"897655\"]}");
        mockParams.setPath("src" + File.separatorChar + "test" + File.separatorChar + "resources" + File.separatorChar + "com" + File.separatorChar + "pb" + File.separatorChar + "stratus" + File.separatorChar + "controller" + File.separatorChar + "action" + File.separatorChar + "stratus" + File.separatorChar + "queryconfig" + File.separatorChar + "planningApplications" + File.separatorChar + "SampleQueryPickList.xml");

        assertExpressionMatches("select \"attribute1\", \"MI_KEY\" from \"someTable\" where \\(Lower\\(\"AppNumber\"\\) = Lower\\('897656'\\) OR Lower\\(\"AppNumber\"\\) = Lower\\('897655'\\)\\)", captureSearchBySqlRequest().getSQL());
    }

    @Test
    public void shouldCreateSQLQueryWithOROnDateAttribute() throws Exception {
        mockParams.getAttributes().add("attribute1");
        mockParams.setParams("{\"0\":[\"2014-02-01\", \"2014-01-01\"]}");
        mockParams.setPath("src" + File.separatorChar + "test" + File.separatorChar + "resources" + File.separatorChar + "com" + File.separatorChar + "pb" + File.separatorChar + "stratus" + File.separatorChar + "controller" + File.separatorChar + "action" + File.separatorChar + "stratus" + File.separatorChar + "queryconfig" + File.separatorChar + "planningApplications" + File.separatorChar + "SampleQueryOnDate.xml");
        LocaleResolver.setLocale(Locale.ENGLISH);
        assertExpressionMatches("select \"attribute1\", \"MI_KEY\" from \"someTable\" where \\(\"DeliveryDate\" = StringToDate\\('2014-02-01', 'yyyy-mm-dd'\\) OR \"DeliveryDate\" = StringToDate\\('2014-01-01', 'yyyy-mm-dd'\\)\\)", captureSearchBySqlRequest().getSQL());
    }

    @Test
    public void shouldCreateSQLQueryWithOROnTimeAttribute() throws Exception {
        mockParams.getAttributes().add("attribute1");
        mockParams.setParams("{\"0\":[\"1:20:50\", \"2:30:59\"]}");
        mockParams.setPath("src" + File.separatorChar + "test" + File.separatorChar + "resources" + File.separatorChar + "com" + File.separatorChar + "pb" + File.separatorChar + "stratus" + File.separatorChar + "controller" + File.separatorChar + "action" + File.separatorChar + "stratus" + File.separatorChar + "queryconfig" + File.separatorChar + "planningApplications" + File.separatorChar + "SampleQueryOnTime.xml");
        SearchBySQLRequest request = captureSearchBySqlRequest();
        assertExpressionMatches("select \"attribute1\", \"MI_KEY\" from \"someTable\" where \\(\"DeliveryTime\" = @TIME00 OR \"DeliveryTime\" = @TIME01\\)", request.getSQL());

        BoundParameterList params = request.getBoundParameterList();
        List<String> paramNames = new ArrayList();
        paramNames.add("TIME00");
        paramNames.add("TIME01");

        XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar();
        for (BoundParameter param : params.getBoundParameter()) {
            assertTrue(paramNames.contains((String) param.getName()));
            XMLGregorianCalendar value = ((TimeValue) param.getValue()).getValue();
            if (param.getName().equals("TIME00")) {
                xmlCal.setTime(1, 20, 50);
                assertEquals(xmlCal, value);
            } else if (param.getName().equals("TIME01")) {
                xmlCal.setTime(2, 30, 59);
                assertEquals(xmlCal, value);
            }
        }
    }

    @Test
    public void shouldHandlePointGeometry() throws Exception {
        Point point = new Point();
        Pos position = new Pos();
        position.setX(187234);
        position.setY(-1234);
        point.setPos(position);
        mockParams.setGeometry(point);
        mockParams.getAttributes().add("attribute1");
        mockParams.getAttributes().add("attribute2");
        SearchBySQLRequest request = captureSearchBySqlRequest();
        assertExpressionMatches("select \"attribute1\", \"attribute2\", \"MI_KEY\" from \"someTable\" where MI_Intersects\\(@POINT,Obj\\) AND Lower\\(\"AppNumber\"\\) = Lower\\('897656'\\)", request.getSQL());

        BoundParameterList params = request.getBoundParameterList();
        BoundParameter param = params.getBoundParameter().get(0);
        assertNotNull(param);
        assertEquals("POINT", (String) param.getName());
        assertEquals(point, ((GeometryValue) param.getValue()).getFeatureGeometry());
    }

    @Test
    public void shouldHandlePolygon() throws Exception {
        Polygon polygon = new Polygon();
        mockParams.setGeometry(polygon);
        mockParams.getAttributes().add("attribute1");
        mockParams.getAttributes().add("attribute2");
        SearchBySQLRequest request = captureSearchBySqlRequest();
        assertExpressionMatches("select \"attribute1\", \"attribute2\", \"MI_KEY\" from \"someTable\" where MI_Intersects\\(@POLYGON,Obj\\) AND Lower\\(\"AppNumber\"\\) = Lower\\('897656'\\)", request.getSQL());

        BoundParameterList params = request.getBoundParameterList();
        BoundParameter param = params.getBoundParameter().get(0);
        assertNotNull(param);
        assertEquals("POLYGON", (String) param.getName());
        Object obj = ((GeometryValue) param.getValue()).getFeatureGeometry();
        assertTrue(obj instanceof MultiPolygon);
        assertEquals(polygon, ((MultiPolygon) obj).getPolygon().get(0));
    }

    /**
     * Verifies thar a proper SQL Query is created for Custom filter.
     */
    @Test
    public void verifyCustomQuerySQL() {
        final int maxSize = 5;
        // override values in mock
        mockParams.setQueryName(CUSTOM_FILTER);

        //  create the params as expected in request.
        JSONArray params = new JSONArray();
        for (int i = 1; i <= maxSize; i++) {
            JSONObject condition = new JSONObject();
            condition.put(QUERY_COLUMN_NAME, "Name" + i);
            condition.put(QUERY_COLUMN_TYPE, "String");
            condition.put(QUERY_COLUMN_VAL, "val" + i);
            condition.put(QUERY_OPERATOR, "=");
            params.add(condition);
        }
        mockParams.setParams(params.toString());

        String expectedSQL = "select \\* from \"someTable\" where Lower\\(\"Name1\"\\) = Lower\\('val1'\\) AND Lower" +
                "\\(\"Name2\"\\) = Lower\\('val2'\\) AND Lower\\(\"Name3\"\\) = Lower\\('val3'\\) AND Lower" +
                "\\(\"Name4\"\\) = Lower\\('val4'\\) AND Lower\\(\"Name5\"\\) = Lower\\('val5'\\)";
        try {
            assertExpressionMatches(expectedSQL, captureSearchBySqlRequest().getSQL());
        } catch (Exception e) {
        }
    }

    /**
     * Verify the SQL when a user specifies custom query filter and in multiple annotations.
     *
     * @throws Exception
     */
    @Test
    public void verifySQLForMultiFeatureGeometry() throws Exception {

        MultiFeatureGeometry multiFeatureGeometry = new MultiFeatureGeometry();
        Polygon polygon = new Polygon();
        FeatureGeometryConverter converter = new FeatureGeometryConverter(polygon);
        multiFeatureGeometry.getFeatureGeometry().add(converter.convert());
        //  create the params as expected in request.
        JSONArray params = new JSONArray();
        JSONObject condition = new JSONObject();
        condition.put(QUERY_COLUMN_NAME, "AppNumber");
        condition.put(QUERY_COLUMN_TYPE, "String");
        condition.put(QUERY_COLUMN_VAL, "897656");
        condition.put(QUERY_OPERATOR, "=");
        params.add(condition);

        mockParams.setQueryName(CUSTOM_FILTER);
        mockParams.setGeometry(multiFeatureGeometry);
        mockParams.getAttributes().add("attribute1");
        mockParams.getAttributes().add("attribute2");
        mockParams.setParams(params.toString());

        SearchBySQLRequest request = captureSearchBySqlRequest();
        assertExpressionMatches("select \"attribute1\", \"attribute2\", \"MI_KEY\" from \"someTable\" " +
                "where MI_Intersects\\(@MULTIFEATUREGEOMETRY,Obj\\) AND Lower\\(\"AppNumber\"\\) = Lower\\('897656'\\)", request.getSQL());

        BoundParameterList boundParameterList = request.getBoundParameterList();
        BoundParameter param = boundParameterList.getBoundParameter().get(0);
        assertNotNull(param);
        assertEquals("MULTIFEATUREGEOMETRY", (String) param.getName());
        Object obj = ((GeometryValue) param.getValue()).getFeatureGeometry();
        assertTrue(obj instanceof MultiFeatureGeometry);
        assertEquals(polygon, ((MultiPolygon) ((MultiFeatureGeometry) obj).getFeatureGeometry().get(0)).getPolygon()
                .get(0));
    }

    private SearchBySQLRequest captureSearchBySqlRequest() throws Exception {
        strategy.search(mockParams);
        ArgumentCaptor<SearchBySQLRequest> arg = ArgumentCaptor.forClass(
                SearchBySQLRequest.class);
        verify(mockFeatureWebService).searchBySQL(arg.capture());
        return arg.getValue();
    }

    private void assertExpressionMatches(String pattern, String expression) throws Exception {
        if (!expression.matches(pattern)) {
            fail(expression + " doesn't match '" + pattern + "'");
        }
    }

    private void setUpMockDescibeTableCall() throws Exception {
        DescribeTableResponse response = new DescribeTableResponse();
        TableMetadata metadata = new TableMetadata();
        response.setTableMetadata(metadata);
        AttributeDefinitionList attList = new AttributeDefinitionList();
        metadata.setAttributeDefinitionList(attList);
        GeometryAttributeDefinition attDef = new GeometryAttributeDefinition();
        attDef.setDataType(AttributeDataType.GEOMETRY);
        attDef.setName("someName");

        ScalarAttributeDefinition miPrinxAttrDefinition = new ScalarAttributeDefinition();
        miPrinxAttrDefinition.setDataType(AttributeDataType.DOUBLE);
        miPrinxAttrDefinition.setName("MI_PRINX");
        attList.getAttributeDefinition().add(miPrinxAttrDefinition);

        attList.getAttributeDefinition().add(attDef);
        when(mockFeatureWebService.describeTable(
                any(DescribeTableRequest.class))).thenReturn(response);
    }
}
