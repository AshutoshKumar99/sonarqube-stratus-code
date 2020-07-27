package com.pb.stratus.controller.service;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.pb.stratus.controller.Constants;
import com.pb.stratus.core.configuration.CustomTemplateConfiguration;
import com.sun.xml.bind.v2.runtime.reflect.opt.Const;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * This service provides methods for generating code for a given template config.
 * Created by gu003du on 06-Feb-18.
 */
public class CustomTemplateService {

    // Configuration indicating where to write generated custom templates & the base templates for generating code.
    private CustomTemplateConfiguration templateConfiguration;

    // Mark as volatile to ensure double check locking work as expected in case of concurrent requests.
    private volatile Configuration cfg;

    public CustomTemplateService(CustomTemplateConfiguration templateConfiguration) {
        this.templateConfiguration = templateConfiguration;
    }

    /**
     * Save a given template config.
     *
     * @param templateName
     * @param templateConfig
     * @throws IOException
     * @throws TemplateException
     */
    public void save(String templateName, String templateConfig, boolean isOverrite) throws Exception {

        // If overrite true, don't check file existence
        if(!isOverrite){
            // Check if file exists.
            if (Files.exists(Paths.get(templateConfiguration.getCustomTemplateDirectory().toString(),
                    templateName + Constants.FILE_EXT_TYPESCRIPT))) {
                throw new FileAlreadyExistsException(templateName);
            }
        }

        // Generate  a TS file for custom template component & module.
        Gson gson = new Gson();
        JsonArray elementsJson = gson.fromJson(templateConfig, JsonArray.class);
        // A key value pair to be used in TS file to create members, referenced by name in angular template.
        // Save the element configuration individually.
        Map<String, String> elementsMap = new HashMap<>();
        Iterator<JsonElement> iterator = elementsJson.iterator();

        // Json instance for pretty print
        gson = new GsonBuilder().setPrettyPrinting().create();

        while (iterator.hasNext()) {
            JsonObject elementJson = iterator.next().getAsJsonObject();
            // Columns are uniquely identified by name, charts etc by id
            JsonElement elementConfig = elementJson.get(Constants.ID) != null ?
                    elementJson.get(Constants.ID) : elementJson.get(Constants.NAME);
            // Pretty print the element config properties JSON
            elementsMap.put(elementConfig.getAsString(), gson.toJson(elementJson));
        }

        // Create a data-model
        Map input = new HashMap();
        input.put(Constants.TEMPLATE_NAME, templateName);
        input.put(Constants.TEMPLATE_ELEMENTS, elementsMap);
        generateOutput(input, CustomTemplateConfiguration.BASE_TEMPLATE_TS_FTL,
                templateName, Constants.FILE_EXT_TYPESCRIPT);

        // Generate a HTML file for component.
        // Create a data model.
        input = new HashMap<String, Object>();
        ArrayList templateElements = gson.fromJson(elementsJson, ArrayList.class);
        input.put(Constants.TEMPLATE_ELEMENTS, templateElements);
        generateOutput(input, templateConfiguration.BASE_TEMPLATE_HTML_FTL,
                templateName, Constants.FILE_EXT_HTML);

        // Pretty print the template configuration JSON & save the to a file.
        saveTemplateConfig(templateName, templateConfig);
    }

    /**
     * Updates and saves table template mapping file.
     * Creates the mapping file if it is not already created.
     * @param tableName String
     * @param templateName String
     * @param isDefault boolean
     */
    public void saveTableTemplateMappingFile(String tableName, String templateName, boolean isDefault , boolean isSave) throws IOException {
        String dirPath = templateConfiguration.getBaseTemplateDirectory().toString();
        Path filePath = Paths.get(dirPath, Constants.TABLE_TEMPLATE_MAPPING_FILENAME + Constants.FILE_EXT_JSON);

        Map<String, List<TemplateDetails>> tableTemplateMap;
        Gson gson = new Gson();

        // Check if file exists.
        if (Files.exists(filePath)) {
            String jsonStr = new String(Files.readAllBytes(filePath));
            Type collectionType = new TypeToken<HashMap<String, List<TemplateDetails>>>(){}.getType();
            tableTemplateMap = gson.fromJson(jsonStr, collectionType);
        } else {
            tableTemplateMap = new HashMap<>();
        }

        boolean isTableMappingFound = false;
        for(String key: tableTemplateMap.keySet()){
            if(key.equals(tableName)){
                List<TemplateDetails> detailsList = tableTemplateMap.get(tableName);
                boolean isTempDetailsFound = false;
                for(TemplateDetails tempDetails: detailsList){
                    if(templateName.equals(tempDetails.getTemplateName())){
                        tempDetails.setIsDefault(isDefault);
                        tempDetails.setTimeStamp(System.currentTimeMillis());
                        isTempDetailsFound = true;
                    }else if(!isSave){
                        tempDetails.setIsDefault(false);
                    }
                }
                if(!isTempDetailsFound){
                    TemplateDetails templateDetails = new TemplateDetails(templateName, System.currentTimeMillis(), isDefault);
                    detailsList.add(templateDetails);
                }
                isTableMappingFound = true;
            }
        }

        if(!isTableMappingFound){
            List<TemplateDetails> templateDetailsList = new ArrayList<>();
            templateDetailsList.add(new TemplateDetails(templateName, System.currentTimeMillis(), isDefault));
            tableTemplateMap.put(tableName, templateDetailsList);
        }

        // Json instance for pretty print
        gson = new GsonBuilder().setPrettyPrinting().create();

        Files.write(filePath, gson.toJson(tableTemplateMap).getBytes());
    }


    /**
     * Merge a given input with specified base template & write it to a file.
     *
     * @param input
     * @param baseTemplateName
     * @param templateName
     * @throws IOException
     * @throws TemplateException
     */
    private void generateOutput(Map input, String baseTemplateName, String templateName, String type)
            throws Exception {
        String outFileName = templateName + type;
        BufferedWriter writer = null;
        // Create a path for output file.
        Path path = null;
        try {
            path = Paths.get(templateConfiguration.getCustomTemplateDirectory().toString(),
                    outFileName);

            // Create a writer to generate output file
            writer = Files.newBufferedWriter(path);

            //Get the template (uses cache internally)
            Template template = getConfig().getTemplate(baseTemplateName);

            // Merge data-model with template
            template.process(input, writer);
        } catch (Exception e) {
            // Delete the generated file in case there was error during code generation.
            if (path != null && Files.exists(path)) {
                Files.delete(path);
            }
            if (templateConfiguration.BASE_TEMPLATE_HTML_FTL.equals(baseTemplateName)) {
                Path tsFilePath = Paths.get(templateConfiguration.getCustomTemplateDirectory().toString(),
                        templateName + Constants.FILE_EXT_TYPESCRIPT);
                if (Files.exists(tsFilePath)) {
                    Files.delete(tsFilePath);
                }
            }
            throw e;
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }


    /**
     * Save the template configuration JSON
     *
     * @param templateName
     * @param templateConfig
     */
    private void saveTemplateConfig(String templateName, String templateConfig) throws IOException {
        Files.write(Paths.get(templateConfiguration.getCustomTemplateDirectory().toString(),
                templateName + Constants.FILE_EXT_JSON), templateConfig.getBytes());
    }

    /**
     * Checks if the provided custom template file has been manually edited.
     * It basically checks if the last modified timestamps(in seconds) of json file is different than the other two html and ts files.
     * @param templateName String
     * @return true | false
     * @throws IOException
     */
    public boolean isTemplateManuallyEdited(String templateName) throws IOException {
        String dirPath = templateConfiguration.getCustomTemplateDirectory().toString();
        Path jsonFile = Paths.get(dirPath, templateName + Constants.FILE_EXT_JSON);
        Path htmlFile = Paths.get(dirPath, templateName + Constants.FILE_EXT_HTML);
        Path tsFile = Paths.get(dirPath, templateName + Constants.FILE_EXT_TYPESCRIPT);

        if (Files.exists(jsonFile) && Files.exists(htmlFile) && Files.exists(tsFile)) {
            long jsonFileTimestamp = Files.getLastModifiedTime(jsonFile).to(TimeUnit.SECONDS);
            long htmlFileTimestamp = Files.getLastModifiedTime(htmlFile).to(TimeUnit.SECONDS);
            long tsFileTimestamp = Files.getLastModifiedTime(tsFile).to(TimeUnit.SECONDS);
            if(jsonFileTimestamp != htmlFileTimestamp || jsonFileTimestamp != tsFileTimestamp){
                return true;
            }
        } else {
            throw new FileNotFoundException(Constants.TEMPLATE_FILE_NOT_FOUND);
        }
        return false;
    }

    /**
     * Create and adjust the configuration singleton. You should do this ONLY ONCE in
     * the whole application life-cycle.
     *
     * @return
     * @throws IOException
     */
    private Configuration getConfig() throws IOException {
        // Use double check lock to ensure only one configuration instance is created.
        if (this.cfg == null) { // 1st Check
            synchronized (this) {
                if (this.cfg == null) {
                    // Create and adjust the configuration singleton
                    cfg = new Configuration(Configuration.VERSION_2_3_27);
                    cfg.setDirectoryForTemplateLoading(templateConfiguration.getBaseTemplateDirectory());
                    cfg.setDefaultEncoding(StandardCharsets.UTF_8.name());
                    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
                    cfg.setLogTemplateExceptions(false);
                    cfg.setWrapUncheckedExceptions(true);
                }
            }
        }
        return cfg;
    }
}
