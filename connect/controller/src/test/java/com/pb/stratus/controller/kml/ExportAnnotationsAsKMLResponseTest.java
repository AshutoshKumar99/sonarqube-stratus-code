package com.pb.stratus.controller.kml;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class ExportAnnotationsAsKMLResponseTest {

    ExportAnnotationsAsKMLResponse exportAnnotationsAsKMLResponse;

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForEmptyString()
    {
        exportAnnotationsAsKMLResponse = new ExportAnnotationsAsKMLResponse(null);
        exportAnnotationsAsKMLResponse = new ExportAnnotationsAsKMLResponse("");
    }

    @Test
    public void shouldReturnTheSameCharacterSequence()
    {
        exportAnnotationsAsKMLResponse = new ExportAnnotationsAsKMLResponse("some value");
        assertEquals("some value", exportAnnotationsAsKMLResponse.getKMLString());
    }

}
