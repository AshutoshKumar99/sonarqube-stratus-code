package com.pb.stratus.controller.datainterchangeformat;


import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

public class ExtractFormatFromRequestTest {

    private ExtractFormatFromRequest extractFormatFromRequest;
    private HttpServletRequest mockHttpServletRequest;

    @Before
    public void setUp()
    {
        extractFormatFromRequest = new ExtractFormatFromRequest();
        mockHttpServletRequest = mock(HttpServletRequest.class);
    }

    @Test
    public void shouldReturnNullIfFormatNotSpecified()
    {
        String result = extractFormatFromRequest.extract(mockHttpServletRequest);
        assertTrue(result == null);
    }

    @Test
    public void shouldReadFormatFromAttributeIfRequestNotMultiPart()
    {
        extractFormatFromRequest.extract(mockHttpServletRequest);
        verify(mockHttpServletRequest).getParameter("format");
    }

    @Test
    public void shouldParseRequestIfItIsMultipart() throws FileUploadException {
        ServletFileUpload mockServletFileUpload = mock(ServletFileUpload.class);
        extractFormatFromRequest.setServletFileUpload(mockServletFileUpload);
        when(mockHttpServletRequest.getContentType()).thenReturn("multipart/form-data");
        extractFormatFromRequest.extract(mockHttpServletRequest);
        verify(mockServletFileUpload).parseRequest(mockHttpServletRequest);
    }

    @Test
    public void shouldSetTheMultiPartInTheRequestAfterParsing()
            throws FileUploadException {
        List emptyList = Collections.EMPTY_LIST;
        ServletFileUpload mockServletFileUpload = mock(ServletFileUpload.class);
        extractFormatFromRequest.setServletFileUpload(mockServletFileUpload);
        when(mockServletFileUpload.parseRequest(mockHttpServletRequest)).
                thenReturn(emptyList);
        when(mockHttpServletRequest.getContentType()).thenReturn("multipart/form-data");
        extractFormatFromRequest.extract(mockHttpServletRequest);
        verify(mockHttpServletRequest).setAttribute("Multipart-Items", emptyList);
    }

    @Test
    public void shouldReadFormatFromMultipartRequest() throws IOException {
        MockHttpServletRequest mockMultipartHttpServletRequest =
                new MockHttpServletRequest();
        mockMultipartHttpServletRequest = createMultipartFormDataRequest(
                mockMultipartHttpServletRequest, "format", "format");
        String result =
                extractFormatFromRequest.extract(mockMultipartHttpServletRequest);
        assertEquals("some-format", result);
    }


    public MockHttpServletRequest createMultipartFormDataRequest(
            MockHttpServletRequest request, String resourceName, String partName)
            throws IOException {
        // Load resource being uploaded
        byte[] fileContent = "some-format".getBytes("UTF-8");
        // Create part & entity from resource
        Part[] parts = new Part[] {
                new FilePart(partName, new ByteArrayPartSource(resourceName, fileContent)) };
        MultipartRequestEntity multipartRequestEntity =
                new MultipartRequestEntity(parts, new PostMethod().getParams());
        // Serialize request body
        ByteArrayOutputStream requestContent = new ByteArrayOutputStream();
        multipartRequestEntity.writeRequest(requestContent);
        // Set request body to HTTP servlet request
        request.setContent(requestContent.toByteArray());
        // Set content type to HTTP servlet request (important, includes Mime boundary string)
        request.setContentType(multipartRequestEntity.getContentType());
        return request;
    }


}
