package com.pb.stratus.controller.action;


import com.pb.stratus.controller.kml.ExportAnnotationsAsKMLResponse;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExportKMLActionTest {

    private ExportKMLAction exportKMLAction;
    private HttpServletRequest mockHttpServletRequest;

    @Before
    public void setUp()
    {
        exportKMLAction = new ExportKMLAction();
        mockHttpServletRequest = mock(HttpServletRequest.class);
        when(mockHttpServletRequest.getParameter(ExportKMLAction.KML_STRING)).
                thenReturn("some value");
    }

    @Test
    public void shouldPassRequestArgumentAsItIs() throws IOException, ServletException {
        ExportAnnotationsAsKMLResponse exportAnnotationsAsKMLResponse =
                (ExportAnnotationsAsKMLResponse)
                        exportKMLAction.createObject(mockHttpServletRequest);
        CharSequence actual = exportAnnotationsAsKMLResponse.getKMLString();
        assertEquals("some value", actual.toString());


    }
}
