package com.pb.stratus.controller.datainterchangeformat;


import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class PlainTextFormatResponseTest {

    private PlainTextFormatResponse plainTextFormatResponse;
    private HttpServletResponse mockHttpServletResponse;
    private PlainTextConvertible mockPlainTextConvertible;
    private OutputStream mockOutputStream;

    @Before
    public void setUp() throws IOException {
        plainTextFormatResponse = new PlainTextFormatResponse();
        mockHttpServletResponse = mock(HttpServletResponse.class);
        mockPlainTextConvertible = mock(PlainTextConvertible.class);
        when(mockPlainTextConvertible.getText()).thenReturn("some text");
        mockOutputStream = mock(ServletOutputStream.class);
        when(mockHttpServletResponse.getOutputStream()).thenReturn(
                (ServletOutputStream) mockOutputStream);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNPE() throws IOException {
        plainTextFormatResponse.send(mockHttpServletResponse, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgExcptn() throws IOException {
        plainTextFormatResponse.send(mockHttpServletResponse, new String());
    }

    @Test
    public void shouldWritePlainTextToStream() throws IOException {
        plainTextFormatResponse.send(mockHttpServletResponse,
                mockPlainTextConvertible);
        verify(mockHttpServletResponse).setContentType("text/plain");
        verify(mockHttpServletResponse).setCharacterEncoding("UTF-8");
    }

    @Test
    public void shouldWriteCorrectData() throws IOException {
        plainTextFormatResponse.send(mockHttpServletResponse,
                mockPlainTextConvertible);
        byte[] expected = "some text".getBytes("UTF-8");
        verify(mockHttpServletResponse).setContentLength(expected.length);
        verify(mockOutputStream).write(expected);
    }

}
