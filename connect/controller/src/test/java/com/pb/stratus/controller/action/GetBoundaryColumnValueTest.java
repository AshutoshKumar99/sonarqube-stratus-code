package com.pb.stratus.controller.action;

import com.mapinfo.midev.service.feature.v1.SearchBySQLResponse;
import com.mapinfo.midev.service.featurecollection.v1.*;
import com.mapinfo.midev.service.mapping.ws.v1.ServiceException;
import com.pb.stratus.controller.featuresearch.FeatureService;
import com.pb.stratus.controller.service.SearchByQueryParams;

import org.apache.commons.httpclient.methods.ExpectContinueMethod;
import org.apache.http.HttpResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;


import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 12/30/14
 * Time: 2:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetBoundaryColumnValueTest {
    private FeatureService mockFeatureService;
    private GetBoundaryColumnValueAction target;

    @Before
    public void setup(){
        mockFeatureService = Mockito.mock(FeatureService.class);
        target = new GetBoundaryColumnValueAction(mockFeatureService);
    }

    @Test
    public void  testServiceInegration() throws Exception{
        String sql = "select max(\"columnName\") from \"tableName\"";
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        httpRequest.setParameter("tableName", "tableName");
        httpRequest.setParameter("columnName", "columnName");
        httpRequest.setParameter("boundaryType", "MAX");
        SearchBySQLResponse response = createResponse(100D);
        Mockito.when(mockFeatureService.executeSql(Mockito.anyString())).thenReturn(response);
        ArgumentCaptor<String> captor =
                ArgumentCaptor.forClass(String.class);
        Object value = target.createObject(httpRequest);
        Mockito.verify(mockFeatureService).executeSql(captor.capture());
        Assert.assertEquals(sql, captor.getValue());
        Assert.assertEquals("100.0", value);
    }

    private SearchBySQLResponse createResponse(Double value){
        SearchBySQLResponse response = new SearchBySQLResponse();
        DoubleValue attribValue = new DoubleValue();
        attribValue.setValue(value);
        Feature feature = new Feature();
        feature.getAttributeValue().add(attribValue);
        FeatureList list = new FeatureList();
        list.getFeature().add(feature);
        FeatureCollection collection = new FeatureCollection();
        collection.setFeatureList(list);
        response.setFeatureCollection(collection);
        return response;
    }
}
