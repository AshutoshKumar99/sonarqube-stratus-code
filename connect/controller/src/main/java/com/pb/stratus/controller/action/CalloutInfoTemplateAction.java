package com.pb.stratus.controller.action;

import com.pb.stratus.controller.Constants;
import com.pb.stratus.core.configuration.ConfigReader;
import com.pb.stratus.core.configuration.ControllerConfiguration;
import com.pb.stratus.security.core.util.AuthorizationUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.*;

/**
 * @Author: Sikhar
 * Action class to get InfoTemplate File names for a MapConfig and Table files from the analyst/infotemplates/InfoTemplateDefinitions.json
 */
public class CalloutInfoTemplateAction extends BaseConfigProviderAction {
    private final static Logger logger = LogManager
            .getLogger(CalloutInfoTemplateAction.class);
    private static final String MAP_CONFIG_NAME = "mapConfigName";
    private static final String TABLES = "tables";

    public CalloutInfoTemplateAction(ConfigReader configReader, ControllerConfiguration config,
                                     AuthorizationUtils authorizationUtils) {
        super(configReader, config, authorizationUtils);
    }

    @Override
    protected String getMimeType() {
        return "application/xml";
    }

    protected void sendJSON(HttpServletResponse response, HttpServletRequest request, InputStream is) throws IOException {
        String mapConfig=request.getParameter("mapconfig");
        String tablesStr=request.getParameter("tables");
        //Otherwise we will get a fixed Size List which can not be altered later
        List<String> tables= new ArrayList<String>(Arrays.asList(tablesStr.split(",")));
        Map<String, String> tableTemplateMapping=getTableTemplateMapping(mapConfig,tables,is);
        response.setContentType(Constants.ACCEPT_HEADER_VALUE_JSON);
        OutputStreamWriter osw = null;
        try {
            JSONObject objJson = new JSONObject();
            objJson.putAll( tableTemplateMapping );
            //write JSON data to OutputStream
            osw = new OutputStreamWriter(response.getOutputStream());
            osw.write("/*");
            objJson.write(osw);
            osw.write("*/");
        } finally {
            if (osw != null) {
                osw.flush();
                osw.close();
            }
        }
    }

    private Map<String, String> getTableTemplateMapping(String mapConfig, List<String> tables, InputStream is) throws IOException {
        List<String> mappedTables = new ArrayList<String>();
        Map<String, String> tableTemplateMapping = new HashMap<String, String>();
        JSONObject infoTemplateConfig = (JSONObject)new XMLSerializer().readFromStream(is);
        //The Mapping between Table and Template.
        JSONArray tableTemplateMappingArray = infoTemplateConfig.getJSONArray("TableTemplateMappings");
        //The Mapping for MapConfigs Table and Template. It will override Table Template Mapping
        JSONArray mapConfigTableTemplateMappingArray = infoTemplateConfig.getJSONArray("MapConfigTableTemplateMappings");
        //JSON Array for the Mapping that matches with Map Config
        JSONArray tableTemplateMappingArrayForSelectedConfig = null;
        //For All Tables which are not mapped
        String defaultTemplate = infoTemplateConfig.getString("DefaultTemplate");
        for (int i = 0; i < mapConfigTableTemplateMappingArray.size(); i++) {
            JSONObject obj = mapConfigTableTemplateMappingArray.getJSONObject(i);
            if (obj.getString("MapConfig").equals(mapConfig)) {
                tableTemplateMappingArrayForSelectedConfig = obj.getJSONArray("Mappings");
                break;
            }
        }
        //Iterate to see if there is a match for Tables inside the selected MapConfig
        if (tableTemplateMappingArrayForSelectedConfig != null && !tableTemplateMappingArrayForSelectedConfig.isEmpty()) {
            for (int i = 0; i < tableTemplateMappingArrayForSelectedConfig.size(); i++) {
                JSONObject obj = tableTemplateMappingArrayForSelectedConfig.getJSONObject(i);
                String table = obj.getString("Table");
                if (tables.contains(table)) {
                    tableTemplateMapping.put(table, obj.getString("Template"));
                    mappedTables.add(table);
                }
            }
        }
        //Remove All Mapped Tables which will give us a List of Tables for which a Template is yet to be found
        tables.removeAll(mappedTables);
        if (tables.size() > 0) {
            for (int i = 0; i < tableTemplateMappingArray.size(); i++) {
                //Iterate through the Table Teamplate Mapping without Map Config
                JSONObject obj = tableTemplateMappingArray.getJSONObject(i);
                String table = obj.getString("Table");
                if (tables.contains(table)) {
                    tableTemplateMapping.put(table, obj.getString("Template"));
                }
            }
        }
        //Default Template for the rest of the tables
        tableTemplateMapping.put("default",defaultTemplate);
        return tableTemplateMapping;
    }
}
