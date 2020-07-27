package com.pb.stratus.controller.datainterchangeformat;


import com.pb.stratus.controller.kml.KMLConverter;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.*;


public class KMLFileFormatResponseTest {

    KMLFileFormatResponse kmlFileFormatResponse;
    HttpServletResponse mockHttpServletResponse;
    ServletOutputStream mockOutputStream;
    KMLConverter mockKmlConverter;
    String value = "some value";

    @Before
    public void setUp() throws IOException {
        kmlFileFormatResponse = new KMLFileFormatResponse();
        mockHttpServletResponse = mock(HttpServletResponse.class);
        mockOutputStream = mock(ServletOutputStream.class);
        when(mockHttpServletResponse.getOutputStream()).thenReturn(
                mockOutputStream);
        mockKmlConverter = mock(KMLConverter.class);
        when(mockKmlConverter.getKMLString()).thenReturn(value);

    }

    @Test (expected = NullPointerException.class)
    public void shouldThrowExceptionForNullResults() throws IOException {
        kmlFileFormatResponse.send(mockHttpServletResponse, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfResultIsNotKMLConverter() throws IOException {
        kmlFileFormatResponse.send(mockHttpServletResponse, this);
    }

    @Test
    public void shouldWriteDataToOutputStream() throws IOException {
        kmlFileFormatResponse.send(mockHttpServletResponse, mockKmlConverter);
        verify(mockHttpServletResponse).setContentType("application/xml");
        verify(mockHttpServletResponse).setHeader("Content-Disposition",
                "attachment;filename=annotation.kml");
        verify(mockHttpServletResponse).setCharacterEncoding("UTF-8");
        byte[] expectedBytes = value.getBytes("UTF-8");
        verify(mockHttpServletResponse).setContentLength(expectedBytes.length);
        verify(mockOutputStream).write(expectedBytes);
    }
}
