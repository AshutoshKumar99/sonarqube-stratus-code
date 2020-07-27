package com.pb.stratus.controller.datainterchangeformat;


import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static org.mockito.Mockito.*;

public class DojoIoIFrameResponseTest {

    private DojoIoIFrameResponse dojoIoIFrameResponse;
    private InputStream is;
    private HttpServletResponse mockHttpServletResponse;
    private  OutputStream mockOutputStream;

    public DojoIoIFrameResponseTest() throws IOException {


    }

    @Before
    public void setUp() throws IOException {

        dojoIoIFrameResponse = new DojoIoIFrameResponse();
        mockHttpServletResponse = mock(HttpServletResponse.class);
        mockOutputStream = mock(ServletOutputStream.class);
        when(mockHttpServletResponse.getOutputStream()).thenReturn(
                (ServletOutputStream) mockOutputStream);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNPE() throws IOException {
        dojoIoIFrameResponse.send(mockHttpServletResponse, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgExcptn() throws IOException {
        dojoIoIFrameResponse.send(mockHttpServletResponse, new String());
    }

    @Test
    public void shouldSendHtml() throws IOException {
        PlainTextConvertible plainTextConvertible =
                new PlainTextConvertibleImpl("some text");
        dojoIoIFrameResponse.send(mockHttpServletResponse, plainTextConvertible);
        verify(mockHttpServletResponse).setContentType("text/html");
        verify(mockHttpServletResponse).setCharacterEncoding("UTF-8");
    }

    @Test
    public void shouldSendCorrectContentInHtml() throws IOException {
        PlainTextConvertible plainTextConvertible =
                new PlainTextConvertibleImpl("some text");
        dojoIoIFrameResponse.send(mockHttpServletResponse,plainTextConvertible);
        verify(mockOutputStream).write(getResult("some text"));
        verify(mockHttpServletResponse).setContentLength(getResult("some text").length);
    }

    private byte[] getResult(String replacement) throws IOException {
        InputStream is = DojoIoIFrameResponseTest.class.
                getResourceAsStream("DojoIoIframeHtml.html");
        List<String> lines =
                IOUtils.readLines(is, "UTF-8");
        StringBuilder sb = new StringBuilder();
        for(String line: lines)
        {
            sb.append(line);
        }
        String result =  String.format(sb.toString(), replacement);
        return result.getBytes("UTF-8");
    }
}

