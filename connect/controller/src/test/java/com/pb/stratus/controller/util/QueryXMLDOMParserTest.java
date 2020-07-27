package com.pb.stratus.controller.util;

import com.pb.stratus.controller.queryutils.QueryMetadata;
import com.pb.stratus.controller.queryutils.CriteriaParams;
import com.pb.stratus.controller.queryutils.QueryXMLDOMParser;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class QueryXMLDOMParserTest
{
    @Test
    public void shouldParseDocumentwithNoSpecialCharacters()
    {
        QueryXMLDOMParser instance = new QueryXMLDOMParser();
        QueryMetadata xmlData1 = instance
            .parseDocument("src" +  File.separatorChar + "test" +  File.separatorChar + "resources" +  File.separatorChar + "com" +  File.separatorChar + "pb" +  File.separatorChar + "stratus" +  File.separatorChar + ""
                + "controller" +  File.separatorChar + "action" +  File.separatorChar + "stratus" +  File.separatorChar + "queryconfig" +  File.separatorChar + "planningApplications" +  File.separatorChar + "SampleQueryData.xml");

        QueryMetadata xmlData2 = new QueryMetadata();
        xmlData2.setTableName("PlanningApplications");
        xmlData2.setQueryName("SampleQueryData");

        assertEquals(xmlData1.getTableName(), xmlData2.getTableName());
        assertEquals(xmlData1.getQueryName(), xmlData2.getQueryName());

        List<CriteriaParams> list = xmlData1.getCriteriaParams();
        CriteriaParams paramsObject = list.get(0);

        assertEquals(paramsObject.getOperator(), "=");
        assertEquals(paramsObject.getColumnName(), "AppNumber");
        assertEquals(paramsObject.getColumnType(), "STRING");
        assertEquals(paramsObject.getDisplayType(), "User Input");
        assertEquals(paramsObject.getId(), "0");
        assertEquals(paramsObject.getLabel(), "Enter AppNumber");
    }

    @Test
    public void shouldParseDocumentwithSpecialCharacters()
    {

        QueryXMLDOMParser instance = new QueryXMLDOMParser();
        QueryMetadata xmlData1 = instance
            .parseDocument("src" +  File.separatorChar + "test" +  File.separatorChar + "resources" +  File.separatorChar + "com" +  File.separatorChar + "pb" +  File.separatorChar + "stratus" +  File.separatorChar + ""
                + "controller" +  File.separatorChar + "action" +  File.separatorChar + "stratus" + File.separatorChar + "queryconfig" +  File.separatorChar + "planningApplications" +  File.separatorChar + "uête.xml");

        QueryMetadata xmlData2 = new QueryMetadata();
        xmlData2.setQueryName("uête");

        assertEquals( xmlData2.getQueryName(), xmlData1.getQueryName());

    }

}
