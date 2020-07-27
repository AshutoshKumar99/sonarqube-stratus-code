package com.pb.gazetteer.rest;

import com.pb.gazetteer.*;
import com.pb.gazetteer.lucene.LuceneIndexGenerator;
import com.pb.gazetteer.lucene.LuceneInstance;
import com.pb.gazetteer.search.SearchLogic;
import com.pb.gazetteer.webservice.ApplicationContextListener;
import com.pb.gazetteer.webservice.GazetteerInstance;
import com.pb.gazetteer.webservice.GazetteerNames;
import com.pb.gazetteer.webservice.LocateConfigProvider;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.activation.DataHandler;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JU002AH on 4/16/2019.
 */
public class LocateService {

    private static final Logger log = LogManager.getLogger(RESTLocateService.class);
    private static final int MAX_FAILURE_COUNT = 19;
    private static final String SERVER_MESSAGE =
            "An exception occurred on "
                    + "the server, please try the query again. If it continues to "
                    + "fail, please contact the owner of the application";

    public PopulateResponse populateGazetteer(String gazetteerName, String tenantName,
                                              Attachment attachment, String projection,
                                              int addressColumn, int xColumn, int yColumn, String delimiter,
                                              String searchLogic, MessageContext messageContext) throws ServerException, TransformerException, Exception {

        AddressCSVUploader csvUploader = new AddressCSVUploader();
        InputStreamReader fileInputStream;
        DataHandler dh = attachment.getDataHandler();
        PopulateResponse response = new PopulateResponse();
        try {

            boolean isValidInputParam = isValidateInput(projection, delimiter, response);
            PopulateParameters populateParameters = new PopulateParameters();
            populateParameters.setGazetteerName(gazetteerName);
            populateParameters.setTenantName(tenantName);
            populateParameters.setAddressColumn(addressColumn);
            if(delimiter.equals("tab")){
                populateParameters.setDelimiter("\t");
            }else{
                populateParameters.setDelimiter(delimiter);
            }
            // fails if 20 errors occur
            populateParameters.setMaxFailures(MAX_FAILURE_COUNT);
            populateParameters.setxColumn(xColumn);
            populateParameters.setyColumn(yColumn);
            populateParameters.setProjection(projection);

            byte[] bytes = IOUtils.toByteArray(dh.getInputStream());
            InputStream is1 = new ByteArrayInputStream(bytes);
            InputStream is2 = new ByteArrayInputStream(bytes);

            // setting search logic as per user request
            if ("LUCENE_IMPLICIT".equalsIgnoreCase(searchLogic)) {
                populateParameters.setSearchLogic(SearchLogic.LUCENE_IMPLICIT);
            } else {
                populateParameters.setSearchLogic(SearchLogic.DEFAULT_LOGIC);
            }


            String configFilePath = getProvider(messageContext).getLocateConfigPath(tenantName);
            LocateConfig locateConfig =
                    getProvider(messageContext).getConfig(tenantName);
            LocateInstance locateInstance =
                    locateConfig.getLocateInstance(gazetteerName);

            try {
                if (isValidInputParam) {
                    response = LuceneIndexGenerator
                            .generate((LuceneInstance) locateInstance,
                                    populateParameters, is1);
                }
                if (response != null && response.getSuccess() && isValidInputParam) {
                    //update locate config file
                    ConfigurationWriter configWriter = new ConfigurationWriter();
                    configWriter.modifyLocateConfigXMLFile(
                            new File(configFilePath), populateParameters);
                    // save address CSV file
                    String csvPath = getProvider(messageContext).getLocateConfigCSVPath(tenantName);
                    csvUploader.writeCSVFile(is2, gazetteerName, csvPath);

                } else {
                    log.info("No row added while generating the index. SRS and SearchLogic is not updated" +
                            " in locate config file.");
                }

                return response;
            } finally {
                IOUtils.closeQuietly(is1);
                IOUtils.closeQuietly(is2);
            }
        } catch (TransformerException tfe) {
            log.error(tfe);
            throw new ServerException(SERVER_MESSAGE);
        } catch (Exception e) {
            log.error(e);
            throw new ServerException(SERVER_MESSAGE);
        }
    }

    public GazetteerNames getGazetteerNames(String tenantName,MessageContext messageContext) throws ServerException {
        try {
            LocateConfig locateConfig = getProvider(messageContext).getConfig(tenantName);
            List<GazetteerInstance> gazetteerInstances = new ArrayList<GazetteerInstance>();
            for (LocateInstance li : locateConfig.getLocateInstances()) {
                GazetteerInstance gazetteerInstance = new GazetteerInstance();
                gazetteerInstance.setGazetteerName(li.getName());
                if (li instanceof LuceneInstance) {
                    gazetteerInstance.setSrs(((LuceneInstance) li).getSrs());
                    gazetteerInstance.setEngineName(((LuceneInstance) li).getEngine().getName());
                }
                gazetteerInstances.add(gazetteerInstance);
            }
            GazetteerNames gazetteerNames = new GazetteerNames();
            gazetteerNames.getGazetteerInstances().addAll(gazetteerInstances);
            gazetteerNames.setDefaultGazetteerName(locateConfig.getDefaultInstance()
                    .getName());
            return gazetteerNames;
        } catch (Exception x) {
            log.error(x);
            throw new ServerException(SERVER_MESSAGE);
        }
    }
    /**
     * Validate projection and delimitter
     *
     * @param projection
     * @param delimiter
     * @param response
     * @return
     */
    private boolean isValidateInput(String projection, String delimiter, PopulateResponse response) {

        if (StringUtils.isEmpty(projection)) {
            response.setSuccess(false);
            response.setFailureCode(FailureCode.PROJECTION_NOT_FOUND);
            return false;
        } else if (StringUtils.isEmpty(delimiter)) {
            response.setSuccess(false);
            response.setFailureCode(FailureCode.DELIMITER_NOT_FOUND);
            return false;
        } else {
            return true;
        }
    }

    /**
     *  This gives customer locator folder instance
     * @param messageContext
     * @return locator provider
     */
    private LocateConfigProvider getProvider(MessageContext messageContext) {
        ServletContext servletContext =
                (ServletContext) messageContext.getServletContext();
        LocateConfigProvider provider =
                (LocateConfigProvider) servletContext
                        .getAttribute(ApplicationContextListener.PROVIDER_KEY);
        if (provider == null) {
            log.error("LocateConfigProvider not initialized.");
            throw new ConfigurationException(
                    "LocateConfigProvider not initialized.");
        }

        return provider;
    }
}
