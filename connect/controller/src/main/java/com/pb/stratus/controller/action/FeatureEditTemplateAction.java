package com.pb.stratus.controller.action;

import com.google.gson.Gson;
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

 * Action class to get Feature Editing Template File names for a MapConfig and Table files from the analyst/FeatureEditTemplateDefinitions.xml
 */
public class FeatureEditTemplateAction extends BaseConfigProviderAction {
    private final static Logger logger = LogManager
            .getLogger(FeatureEditTemplateAction.class);
    private static final String MAP_CONFIG_NAME = "mapConfigName";
    private static final String TABLE = "table";

    public FeatureEditTemplateAction(ConfigReader configReader, ControllerConfiguration config,
                                     AuthorizationUtils authorizationUtils) {
        super(configReader, config, authorizationUtils);
    }

    @Override
    protected String getMimeType() {
        return "application/xml";
    }

    protected void sendJSON(HttpServletResponse response, HttpServletRequest request, InputStream is) throws IOException {
        Map<String, String> tableTemplateMapping = new HashMap<>();
        String tableTemplateMcfgMapping = "";
        String mapConfig = request.getParameter("mapconfig");
        String table = request.getParameter("table");
        if(table.isEmpty())
        {
            tableTemplateMcfgMapping = getAllTableTemplateMapping(mapConfig,is);
            tableTemplateMapping.put(mapConfig,tableTemplateMcfgMapping);
        }else{
            tableTemplateMapping=getTableTemplateMapping(mapConfig,table,is);
        }
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

    private Map<String, String> getTableTemplateMapping(String mapConfig, String table, InputStream is) throws IOException {
        Map<String, String> tableTemplateMapping = new HashMap<String, String>();

        JSONObject infoTemplateConfig = (JSONObject)new XMLSerializer().readFromStream(is);

        //JSON Array/Obj for the Mapping that matches with Map Config
        JSONArray tableTemplateMappingArrayForSelectedConfig = null;
        Object mappingsBySelectedConfig= null;

        //Precedence 1
        //The Mapping for MapConfigs Table and Template. It will override Table Template Mapping
        JSONArray mapConfigTableTemplateMappingArray = new JSONArray();
        Object mappingsObj = infoTemplateConfig.get("MapConfigTableTemplateMappings");
        if( mappingsObj instanceof JSONArray){
            mapConfigTableTemplateMappingArray = (JSONArray)mappingsObj;
            for (int i = 0; i < mapConfigTableTemplateMappingArray.size(); i++) {
                JSONObject obj = mapConfigTableTemplateMappingArray.getJSONObject(i);
                if (obj.getString("MapConfig").equals(mapConfig)) {
                    mappingsBySelectedConfig = obj.get("Mappings");
                    break;
                }
            }
        }else if (mappingsObj instanceof JSONObject){
            Object mapConfigTableTemplateMapping = ((JSONObject) mappingsObj).get("MapConfigTableTemplateMapping");
            String mapCfgValue = ((JSONObject)mapConfigTableTemplateMapping).getString("MapConfig");
            if (mapCfgValue.equals(mapConfig)) {
                mappingsBySelectedConfig = ((JSONObject) mapConfigTableTemplateMapping).get("Mappings");
            }
        }

        if( mappingsBySelectedConfig instanceof JSONArray){
            tableTemplateMappingArrayForSelectedConfig = (JSONArray) mappingsBySelectedConfig;
        }else if(mappingsBySelectedConfig instanceof JSONObject) {
            Object tableMapping = ((JSONObject) mappingsBySelectedConfig).get("Mapping");
            String tablePath = ((JSONObject)tableMapping).getString("Table");
            if (tablePath.equals(table)) {
                tableTemplateMapping.put(table, ((JSONObject)tableMapping).getString("Template"));
                return tableTemplateMapping;
            }

        }

        //Iterate to see if there is a match for Table inside the selected MapConfig
        if (tableTemplateMappingArrayForSelectedConfig != null && !tableTemplateMappingArrayForSelectedConfig.isEmpty()) {
            for (int i = 0; i < tableTemplateMappingArrayForSelectedConfig.size(); i++) {
                JSONObject obj = tableTemplateMappingArrayForSelectedConfig.getJSONObject(i);
                String tablePath = obj.getString("Table");
                if (tablePath.equals(table)) {
                    tableTemplateMapping.put(table, obj.getString("Template"));
                    return tableTemplateMapping;
                }
            }
        }

        //Precedence 2
        //Iterate to see if there is a match for Table inside the Mappings between Table and Template.
        JSONArray tableTemplateMappingArray;
        Object tableMappingsObj = infoTemplateConfig.get("TableTemplateMappings");
        if( tableMappingsObj instanceof JSONArray){
            tableTemplateMappingArray = (JSONArray)tableMappingsObj;
            for (int i = 0; i < tableTemplateMappingArray.size(); i++) {
                JSONObject obj = tableTemplateMappingArray.getJSONObject(i);
                String tablePath = obj.getString("Table");
                if (tablePath.equals(table)) {
                    tableTemplateMapping.put(table, obj.getString("Template"));
                    return tableTemplateMapping;
                }
            }
        }else if (tableMappingsObj instanceof JSONObject){
            Object tableMapping = ((JSONObject) tableMappingsObj).get("Mapping");
            String tablePath = ((JSONObject)tableMapping).getString("Table");
            if (tablePath.equals(table)) {
                tableTemplateMapping.put(table, ((JSONObject)tableMapping).getString("Template"));
                return tableTemplateMapping;
            }

        }

        //Precedence 3
        String defaultTemplate = infoTemplateConfig.getString("DefaultTemplate");
        tableTemplateMapping.put(table,defaultTemplate);
        return tableTemplateMapping;
    }
    private String getAllTableTemplateMapping(String mapConfig, InputStream is) throws IOException {
        JSONObject infoTemplateConfig = (JSONObject)new XMLSerializer().readFromStream(is);
        //JSON Array/Obj for the Mapping that matches with Map Config
        JSONArray tableTemplateMappingArrayForSelectedConfig = null;
        Object mappingsBySelectedConfig= null;

        JSONArray mapConfigTableTemplateMappingArray = new JSONArray();
        Object mappingsObj = infoTemplateConfig.get("MapConfigTableTemplateMappings");
        if( mappingsObj instanceof JSONArray){
            mapConfigTableTemplateMappingArray = (JSONArray)mappingsObj;
            for (int i = 0; i < mapConfigTableTemplateMappingArray.size(); i++) {
                JSONObject obj = mapConfigTableTemplateMappingArray.getJSONObject(i);
                if (obj.getString("MapConfig").equals(mapConfig)) {
                    mappingsBySelectedConfig = obj.get("Mappings");
                    break;
                }
            }
        }else if (mappingsObj instanceof JSONObject){
            Object mapConfigTableTemplateMapping = ((JSONObject) mappingsObj).get("MapConfigTableTemplateMapping");
            String mapCfgValue = ((JSONObject)mapConfigTableTemplateMapping).getString("MapConfig");
            if (mapCfgValue.equals(mapConfig)) {
                mappingsBySelectedConfig = ((JSONObject) mapConfigTableTemplateMapping).get("Mappings");
            }
        }
        if( mappingsBySelectedConfig instanceof JSONArray){
            tableTemplateMappingArrayForSelectedConfig = (JSONArray) mappingsBySelectedConfig;
        }
        // get the default template name along with mapconfi listings
        String defaultTemplateName = infoTemplateConfig.getString("DefaultTemplate");
        JSONObject templateMappings = new JSONObject();
        templateMappings.put("defaultTemplateName",defaultTemplateName);
        templateMappings.put("mappings",tableTemplateMappingArrayForSelectedConfig);
        Gson gson = new Gson();
        String json = gson.toJson(templateMappings);
        return json;
    }
}
