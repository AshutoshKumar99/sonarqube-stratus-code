package com.pb.stratus.controller.action;

import com.pb.stratus.core.configuration.TenantSpecificFileSystemResourceResolver;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


public class ThemeActionTest extends ControllerActionTestBase
{

    private ThemeAction action;
    private byte[] mockData;

    private TenantSpecificFileSystemResourceResolver mockResolver;

    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        mockData = new byte[] {0, 1, 9, 2, 3, 27, 9, 100};
        InputStream is = new ByteArrayInputStream(mockData);
        mockResolver = mock(TenantSpecificFileSystemResourceResolver.class);
        when(mockResolver.getResourceAsStream(any(String.class))).thenReturn(is);
        action = new ThemeAction(mockResolver);
    }

    @Test
    public void  checkValidMimeTypeGif() throws Exception
    {
        testReturnedContent("/somepath/somedir/x.gif", "image/gif");
    }

    @Test
    public void  checkValidMimeTypePng() throws Exception
    {
        testReturnedContent("/somepath/somedir/x.png", "image/png");
    }

    @Test
    public void  checkValidMimeTypeJpeg() throws Exception
    {
        testReturnedContent("/somepath/somedir/x.jpg", "image/jpeg");
    }

    @Test
    public void  checkValidMimeTypeCss() throws Exception
    {
        testReturnedContent("/somepath/somedir/x.css", "text/css");
    }

    @Test
    public void  checkValidMimeTypeJS() throws Exception
    {
        testReturnedContent("/somepath/somedir/x.js", "text/javascript");
    }

    @Test
    public void  checkValidMimeTypeTS() throws Exception
    {
        testReturnedContent("/somepath/somedir/x.ts", "text/x.typescript");
    }

    @Test
    public void  checkValidMimeTypeHtm() throws Exception
    {
        testReturnedContent("/somepath/somedir/x.htm", "text/html");
    }

    @Test
    public void  checkValidMimeTypeHtml() throws Exception
    {
        testReturnedContent("/somepath/somedir/x.html", "text/html");
    }

    @Test
    public void  checkInvalidMimeType() throws Exception
    {
        // Verify that 404 is returned if an illegal file type (by extension) is requested
        getRequest(). setPathInfo("/somepath/somedir/x.zs");
        action.execute(getRequest(), getResponse());
        assertEquals(HttpServletResponse.SC_NOT_FOUND, getResponse().getStatus());
    }

    @Test
    public void  checkForNoStreamData() throws Exception
    {
        // Verify that 404 is returned if the specified file cannot be opened
        InputStream is = null;
        when(mockResolver.getResourceAsStream(any(String.class))).thenReturn(is);

        getRequest(). setPathInfo("/somepath/somedir/x.jpg");
        action.execute(getRequest(), getResponse() );
        assertEquals(HttpServletResponse.SC_NOT_FOUND, getResponse().getStatus());
    }

    private void testReturnedContent(String resourceFilename, String expectedType) throws Exception
    {
        getRequest().setPathInfo("/blah/blah2/" + resourceFilename);
        action.execute(getRequest(), getResponse());
        assertEquals(expectedType, getResponse().getContentType());
        assertArrayEquals(mockData, getResponse().getContentAsByteArray());
    }
}
