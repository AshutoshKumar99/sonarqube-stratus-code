package com.pb.stratus.controller.service;

import com.pb.stratus.controller.Constants;
import com.pb.stratus.core.configuration.CustomTemplateConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CustomTemplateServiceTest {
    private String templateName = "FirstCustomTemplate";
    private CustomTemplateConfiguration templateConfiguration;
    private CustomTemplateService customTemplateService;
    private File currentDir = Paths.get(".").toFile();
    private File templatesDir = Paths.get("src\\test\\resources\\com\\pb\\stratus\\controller\\service").toFile();

    @Before
    public void setUp() throws Exception {
        templateConfiguration = mock(CustomTemplateConfiguration.class);
        customTemplateService = new CustomTemplateService(templateConfiguration);
        when(templateConfiguration.getCustomTemplateDirectory()).thenReturn(currentDir);
        when(templateConfiguration.getBaseTemplateDirectory()).thenReturn(templatesDir);
    }

    @After
    public void tearDown() throws Exception {
        deleteFile(templateName + Constants.FILE_EXT_TYPESCRIPT);
        deleteFile(templateName + Constants.FILE_EXT_JSON);
        deleteFile(templateName + Constants.FILE_EXT_HTML);
        deleteFile(Constants.TABLE_TEMPLATE_MAPPING_FILENAME + Constants.FILE_EXT_JSON);
    }

    /**
     * Verify that a ts, html & JSON file is created as part of code generation.
     *
     * @throws Exception
     */
    @Test
    public void testSave() throws Exception {
        String templateConfig = "[{\n" +
                "\t\t\"name\" : \"Title\",\n" +
                "\t\t\"type\" : \"String\",\n" +
                "\t\t\"selected\" : true,\n" +
                "\t\t\"elementType\" : \"Column\",\n" +
                "\t\t\"showLabel\" : true,\n" +
                "\t\t\"showAs\" : \"Text\",\n" +
                "\t\t\"labelPosition\" : \"inline\"\n" +
                "\t},{\n" +
                "\t\t\"name\" : \"Branch_Service_Type_Code\",\n" +
                "\t\t\"type\" : \"Short\",\n" +
                "\t\t\"selected\" : true,\n" +
                "\t\t\"elementType\" : \"Column\",\n" +
                "\t\t\"showLabel\" : true,\n" +
                "\t\t\"labelPosition\" : \"inline\"\n" +
                "\t}, {\n" +
                "\t\t\"name\" : \"CensusBlockCode\",\n" +
                "\t\t\"type\" : \"String\",\n" +
                "\t\t\"selected\" : true,\n" +
                "\t\t\"elementType\" : \"Column\",\n" +
                "\t\t\"showLabel\" : true,\n" +
                "\t\t\"showAs\" : \"Text\",\n" +
                "\t\t\"labelPosition\" : \"inline\"\n" +
                "\t}, {\n" +
                "\t\t\"name\" : \"Total_deposits__CY\",\n" +
                "\t\t\"type\" : \"Integer\",\n" +
                "\t\t\"selected\" : true,\n" +
                "\t\t\"elementType\" : \"Column\",\n" +
                "\t\t\"showLabel\" : true,\n" +
                "\t\t\"labelPosition\" : \"inline\",\n" +
                "\t\t\"prefix\" : \"$   \",\n" +
                "\t\t\"suffix\" : \"   XXXXX\",\n" +
                "\t\t\"label\" : \"Total Deposits in this year\"\n" +
                "\t}, {\n" +
                "\t\t\"name\" : \"Total deposits over years\",\n" +
                "\t\t\"elementType\" : \"PieChart\",\n" +
                "\t\t\"id\" : \"PieChart_1\",\n" +
                "\t\t\"selectedColumns\" : [{\n" +
                "\t\t\t\t\"checkbox\" : true,\n" +
                "\t\t\t\t\"columnName\" : \"Total_deposits_PriorYr\",\n" +
                "\t\t\t\t\"alias\" : \"Deposits Current Year\"\n" +
                "\t\t\t}, {\n" +
                "\t\t\t\t\"checkbox\" : true,\n" +
                "\t\t\t\t\"columnName\" : \"Total_deposits_PriorYr_minus1\",\n" +
                "\t\t\t\t\"alias\" : \"Deposits Last Year\"\n" +
                "\t\t\t}, {\n" +
                "\t\t\t\t\"checkbox\" : true,\n" +
                "\t\t\t\t\"columnName\" : \"Total_deposits_PriorYr_minus2\",\n" +
                "\t\t\t\t\"alias\" : \"\"\n" +
                "\t\t\t}, {\n" +
                "\t\t\t\t\"checkbox\" : true,\n" +
                "\t\t\t\t\"columnName\" : \"Total_deposits_PriorYr_minus3\",\n" +
                "\t\t\t\t\"alias\" : \"\"\n" +
                "\t\t\t}\n" +
                "\t\t],\n" +
                "\t\t\"static\" : [{\n" +
                "\t\t\t\t\"label\" : \"Average Deposits\",\n" +
                "\t\t\t\t\"value\" : 100000\n" +
                "\t\t\t}\n" +
                "\t\t]\n" +
                "\t}, {\n" +
                "\t\t\"name\" : \"Deposits over the years\",\n" +
                "\t\t\"axisLabel\" : \"Deposit $\",\n" +
                "\t\t\"elementType\" : \"BarChart\",\n" +
                "\t\t\"id\" : \"BarChart_1\",\n" +
                "\t\t\"selectedItems\" : [{\n" +
                "\t\t\t\t\"checkbox\" : true,\n" +
                "\t\t\t\t\"columnName\" : \"Total_deposits__CY\",\n" +
                "\t\t\t\t\"alias\" : \"\"\n" +
                "\t\t\t}, {\n" +
                "\t\t\t\t\"checkbox\" : true,\n" +
                "\t\t\t\t\"columnName\" : \"Total_deposits_PriorYr\",\n" +
                "\t\t\t\t\"alias\" : \"\"\n" +
                "\t\t\t}, {\n" +
                "\t\t\t\t\"checkbox\" : true,\n" +
                "\t\t\t\t\"columnName\" : \"Total_deposits_PriorYr_minus1\",\n" +
                "\t\t\t\t\"alias\" : \"\"\n" +
                "\t\t\t}, {\n" +
                "\t\t\t\t\"checkbox\" : true,\n" +
                "\t\t\t\t\"columnName\" : \"Total_deposits_PriorYr_minus2\",\n" +
                "\t\t\t\t\"alias\" : \"\"\n" +
                "\t\t\t}, {\n" +
                "\t\t\t\t\"checkbox\" : true,\n" +
                "\t\t\t\t\"columnName\" : \"Total_deposits_PriorYr_minus3\",\n" +
                "\t\t\t\t\"alias\" : \"\"\n" +
                "\t\t\t}\n" +
                "\t\t],\n" +
                "\t\t\"static\" : [{\n" +
                "\t\t\t\t\"label\" : \"Target deposit\",\n" +
                "\t\t\t\t\"value\" : 100000\n" +
                "\t\t\t}\n" +
                "\t\t]\n" +
                "\t}\n" +
                "]";

        this.customTemplateService.save(templateName, templateConfig, false);

        // check if three files generated for code
        assertTrue("A TS file was generated", Files.exists(Paths.
                get(templateConfiguration.getCustomTemplateDirectory().toString(),
                        templateName + Constants.FILE_EXT_TYPESCRIPT)));
        assertTrue("A HTML file was generated", Files.exists(Paths.
                get(templateConfiguration.getCustomTemplateDirectory().toString(),
                        templateName + Constants.FILE_EXT_HTML)));

        assertTrue("A JSON file was generated.", Files.exists(Paths.
                get(templateConfiguration.getCustomTemplateDirectory().toString(),
                        templateName + Constants.FILE_EXT_JSON)));
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void testTemplateAlreadyExists() throws Exception {
        String content = "Code goes here";
        Files.write(Paths.get(templateConfiguration.getCustomTemplateDirectory().toString(),
                templateName + Constants.FILE_EXT_TYPESCRIPT), content.getBytes());
        this.customTemplateService.save(templateName, "templateConfig", false);
    }

    @Test()
    public void testSaveTableTemplateMappingFile() throws IOException {

        when(templateConfiguration.getBaseTemplateDirectory()).thenReturn(currentDir);

        this.customTemplateService.saveTableTemplateMappingFile("FirstTable", "FirstTemplate", true,false);

        this.customTemplateService.saveTableTemplateMappingFile("FirstTable", "FirstTemplate", false, false);

        this.customTemplateService.saveTableTemplateMappingFile("FirstTable", "SecondTemplate", false, true);

        this.customTemplateService.saveTableTemplateMappingFile("SecondTable", "SecondTemplate", false, true);

        assertTrue("A JSON file was generated.", Files.exists(Paths.get(Constants.TABLE_TEMPLATE_MAPPING_FILENAME + Constants.FILE_EXT_JSON)));
    }

    /*@Test()
    public void testIsTemplateManuallyEdited() throws IOException {
        boolean isModified = this.customTemplateService.isTemplateManuallyEdited("FirstCustomTemplate");
        assertTrue("The template file is manually edited", isModified);
    }*/

    private void deleteFile(String name) {
        Path p = Paths.get(templateConfiguration.getCustomTemplateDirectory().toString(),
                name);
        if (Files.exists(p)) {
            p.toFile().delete();
        }
    }
}