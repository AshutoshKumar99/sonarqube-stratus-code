package com.pb.stratus.controller.print;

import com.pb.stratus.core.configuration.ConfigReader;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TemplateRepositoryTest
{
    
    @Test
    public void shouldRequestExpectedTemplatePath() throws Exception
    {
        ConfigReader mockReader = mock(ConfigReader.class);
        String expectedContent = "someContent";
        InputStream is = new ByteArrayInputStream(expectedContent.getBytes(
                "UTF-8"));
        when(mockReader.getConfigFile("/printtemplates/someTemplate.fo"))
                .thenReturn(is);
        TemplateRepository repo = new TemplateRepository(mockReader);
        Template t = repo.getTemplate("someTemplate");
        assertEquals(new Template(expectedContent), t);
    }

}
