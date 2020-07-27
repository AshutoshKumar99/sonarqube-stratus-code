package com.pb.stratus.controller.service;

import com.pb.stratus.controller.queryutils.QueryMetadata;
import com.pb.stratus.controller.queryutils.CriteriaParams;
import org.apache.commons.io.FilenameUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ReadInputQueryFromXMLTest
{

    String path = "";
    ReadInputQueryFromXML object;
    SearchByQueryParams params;
    QueryMetadata xmlData;
    CriteriaParams criteriaParams;

    @Before
    public void setUp()
    {
        object = new ReadInputQueryFromXML();
        path = "src" +  File.separatorChar + "test" +  File.separatorChar + "resources" +  File.separatorChar + "com" +  File.separatorChar + "pb" +  File.separatorChar + "stratus" +  File.separatorChar + ""
            + "controller" +  File.separatorChar + "action" +  File.separatorChar + "stratus" +  File.separatorChar + "queryconfig" +  File.separatorChar + ""
            + "planningApplications" +  File.separatorChar + "SampleQueryData.xml";
        params = new SearchByQueryParams();
        params.setPath(path);
        params.setParams("{\"0\":[\"param1\"]}");
    }

    @Test
    public void createQueryFromNewXML()
    {

        QueryMetadata data = object.createQueryMetadata(params);
        Map<String, QueryMetadata> xmlMap = ReadInputQueryFromXML
            .getXmlMap();

        String fileName = FilenameUtils.getBaseName(path);
        Long var = new File(path).lastModified();
        String key = fileName + var.toString();

        assertNotNull(xmlMap.get(key));
        assertEquals(data, xmlMap.get(key));
    }

}