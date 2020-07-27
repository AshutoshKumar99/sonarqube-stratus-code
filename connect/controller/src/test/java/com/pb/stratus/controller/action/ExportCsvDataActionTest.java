package com.pb.stratus.controller.action;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;

public class ExportCsvDataActionTest {

    private ExportCsvDataAction action;
    private String mockCsv;
    private MockHttpServletRequest mockRequest;
    private MockHttpServletResponse mockResponse;


    @Before
    public void setUp() throws Exception
    {
        mockRequest = new MockHttpServletRequest();
        mockResponse = new MockHttpServletResponse();
        mockCsv = "Name,Sum,Average,Min,Max\n" +
                "LB_No,4738.0,394.8333333333333,1.0,1586.0\n" +
                "NoOfShapes,12.0,1.0,1.0,1.0";
        mockRequest.addParameter("csvData", mockCsv);
        mockRequest.addParameter("filename", "mockExportData");
        action = new ExportCsvDataAction();
    }

    @Test
    public void shouldWriteExpectedCsvToResponse() throws Exception
    {
        action.execute(mockRequest, mockResponse);
        byte[] actualResponse = mockResponse.getContentAsByteArray();
        byte[] expectedResponse = createExpectedResponse();
        assertArrayEquals(expectedResponse, actualResponse);
    }

    private byte[] createExpectedResponse() throws IOException
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IOUtils.write(mockCsv, bos);
        return bos.toByteArray();
    }

}
