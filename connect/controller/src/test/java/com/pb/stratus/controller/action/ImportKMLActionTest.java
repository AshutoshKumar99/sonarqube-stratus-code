package com.pb.stratus.controller.action;

import com.pb.stratus.controller.datainterchangeformat.PlainTextConvertible;
import com.pb.stratus.controller.datainterchangeformat.PlainTextConvertibleImpl;
import org.apache.commons.fileupload.FileItem;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.pb.stratus.controller.action.ImportKMLAction.EMPTY_KML;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ImportKMLActionTest {

    private ImportKMLAction importKMLAction;
    HttpServletRequest mockHttpServletRequest;

    @Before
    public void setUp()
    {
        importKMLAction = new ImportKMLAction();
        mockHttpServletRequest = mock(HttpServletRequest.class);
    }

    @Test
    public void shouldReturnEmptyKMLIfNoFileUploaded()
            throws IOException, ServletException
    {
        when(mockHttpServletRequest.getAttribute("Multipart-Items")).
                thenReturn(Collections.EMPTY_LIST);
        Object result =
                importKMLAction.createObject(mockHttpServletRequest);
        PlainTextConvertible expected =
                new PlainTextConvertibleImpl(EMPTY_KML.toString());
        assertEquals(expected, result);
    }

    @Test
    public void shouldReturnEmptyKMLIfListIsNull()
            throws IOException, ServletException
    {
        when(mockHttpServletRequest.getAttribute("Multipart-Items")).
                thenReturn(null);
        Object result =
                importKMLAction.createObject(mockHttpServletRequest);
        PlainTextConvertible expected =
                new PlainTextConvertibleImpl(EMPTY_KML.toString());
        assertEquals(expected, result);
    }

    @Test
    public void shouldReadTheFirstFileUploaded()
            throws IOException, ServletException
    {
        List uploadList = new ArrayList();
        FileItem mockFile1 = mock(FileItem.class);
        when(mockFile1.getString(anyString())).thenReturn("file1");

        FileItem mockFile2 = mock(FileItem.class);
        when(mockFile2.getString(anyString())).thenReturn("file2");
        // upload 2 files
        uploadList.add(mockFile1);
        uploadList.add(mockFile2);

        when(mockHttpServletRequest.getAttribute("Multipart-Items")).
                thenReturn(uploadList);

        Object result =
                importKMLAction.createObject(mockHttpServletRequest);
        PlainTextConvertible expected = new PlainTextConvertibleImpl("file1");
        assertEquals(expected, result);
    }
}
