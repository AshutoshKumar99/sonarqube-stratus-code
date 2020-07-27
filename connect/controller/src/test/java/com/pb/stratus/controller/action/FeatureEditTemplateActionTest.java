package com.pb.stratus.controller.action;

import com.pb.stratus.core.configuration.ConfigReader;
import com.pb.stratus.core.configuration.ControllerConfiguration;
import com.pb.stratus.security.core.util.AuthorizationUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class FeatureEditTemplateActionTest extends ControllerActionTestBase
{
    
    private FeatureEditTemplateAction action;
    private ControllerConfiguration config;
    private AuthorizationUtils authorizationUtils;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private InputStream is;
    private String xmlData1;
    private String xmlData2;


    @Before
    public void setUpAction() throws Exception
    {
        ConfigReader mockConfigReader = mock(ConfigReader.class);
        config = mock(ControllerConfiguration.class);
        authorizationUtils = mock(AuthorizationUtils.class);
        request = new MockHttpServletRequest();
        xmlData1 = "<FeatureEditTemplateDefinition><MapConfigTableTemplateMappings><MapConfigTableTemplateMapping><MapConfig>defaultmap</MapConfig>"+
                "<Mappings><Mapping><Table>table1</Table><Template>Template1.xml</Template></Mapping><Mapping><Table>table2</Table>" +
                "<Template>Template2.xml</Template></Mapping></Mappings></MapConfigTableTemplateMapping></MapConfigTableTemplateMappings><TableTemplateMappings>"+
                "<Mapping><Table>table3</Table><Template>Template3.xml</Template></Mapping></TableTemplateMappings>"+
                "<DefaultTemplate>DefaultTemplate.xml</DefaultTemplate></FeatureEditTemplateDefinition>";

        xmlData2="<FeatureEditTemplateDefinition><MapConfigTableTemplateMappings><MapConfigTableTemplateMapping><MapConfig>defaultmap</MapConfig><Mappings><Mapping>"+
                "<Table>table1</Table><Template>Template1.xml</Template></Mapping><Mapping><Table>table2</Table><Template>Template2.xml</Template>"+
                "</Mapping></Mappings></MapConfigTableTemplateMapping><MapConfigTableTemplateMapping><MapConfig>customconfig</MapConfig><Mappings>"+
                "<Mapping><Table>table1</Table><Template>Template3.xml</Template></Mapping></Mappings></MapConfigTableTemplateMapping>"+
                "</MapConfigTableTemplateMappings><TableTemplateMappings><Mapping><Table>table3</Table><Template>Template3.xml</Template></Mapping>"+
                "<Mapping><Table>table4</Table><Template>Template4.xml</Template></Mapping></TableTemplateMappings><DefaultTemplate>DefaultTemplate.xml</DefaultTemplate>"+
                "</FeatureEditTemplateDefinition>";


        request.setParameter("file","\resources\\com\\pb\\stratus\\controller\\action\\featureEditTemplateDefinitions.xmlfeatureEditTemplateDefinitions");
        request.setParameter("type","infotemplates");
        response = new MockHttpServletResponse();
        action = new FeatureEditTemplateAction(mockConfigReader, config, authorizationUtils);
    }
    
    @Test
    public void shouldBeABaseConfigProviderAction()
    {
        assertTrue(action instanceof BaseConfigProviderAction);
    }
    
    @Test
    public void shouldSetMimeTypeToApplicationXml()
    {
        assertEquals("application/xml", action.getMimeType());
    }

    @Test
    public void shouldGetMappingByMapConfigIfSingleMapConfigEntryExists()
    {
        try {
            is = new ByteArrayInputStream(xmlData1.getBytes());
            request.setParameter("mapconfig","defaultmap");
            request.setParameter("table","table1");
            action.sendJSON(response, request, is);
            String jsonStr = "/*{\"table1\":\"Template1.xml\"}*/";
            assertEquals(response.getContentAsString(), jsonStr);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldGetMappingByMapConfigIfIfSingleMapConfigEntry()
    {
        try {
            is = new ByteArrayInputStream(xmlData2.getBytes());
            request.setParameter("mapconfig","customconfig");
            request.setParameter("table","table1");
            action.sendJSON(response, request, is);
            String jsonStr = "/*{\"table1\":\"Template3.xml\"}*/";
            assertEquals(response.getContentAsString(), jsonStr);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldGetMappingByTableIfExistsWithSingleMappingEntry()
    {
        try {
            is = new ByteArrayInputStream(xmlData1.getBytes());
            request.setParameter("table","table3");
            request.setParameter("mapconfig","defaultmap");
            action.sendJSON(response, request, is);
            String jsonStr = "/*{\"table3\":\"Template3.xml\"}*/";
            assertEquals(response.getContentAsString(), jsonStr);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldGetMappingByTableIfExistsWithMultipleMappingEntry()
    {
        try {
            is = new ByteArrayInputStream(xmlData2.getBytes());
            request.setParameter("table","table4");
            request.setParameter("mapconfig","customconfig");
            action.sendJSON(response, request, is);
            String jsonStr = "/*{\"table4\":\"Template4.xml\"}*/";
            assertEquals(response.getContentAsString(), jsonStr);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void shouldGetDefaultTemplateMappingIfNoOtherExists()
    {
        try {
            is = new ByteArrayInputStream(xmlData1.getBytes());
            request.setParameter("table","table5");
            request.setParameter("mapconfig","defaultmap");
            action.sendJSON(response, request, is);
            String jsonStr = "/*{\"table5\":\"DefaultTemplate.xml\"}*/";
            assertEquals(response.getContentAsString(), jsonStr);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
