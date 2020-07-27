package com.pb.gazetteer.rest;

import com.pb.gazetteer.*;
import com.pb.gazetteer.webservice.GazetteerNames;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.rmi.ServerException;

/**
 * Created by JU002AH on 3/29/2019.
 * This is main rest locate class which have rest api like populate gazetter and getIndexes .
 * Contains locate rest apis
 */
@Path("/")
public class RESTLocateService implements RESTLocateServiceInterface {

    private static final Logger log = LogManager.getLogger(RESTLocateService.class);
    private static final String SERVER_MESSAGE =
            "An exception occurred on "
                    + "the server, please try the query again. If it continues to "
                    + "fail, please contact the owner of the application";

    public LocateService getLocateService() {
        return locateService;
    }

    public void setLocateService(LocateService locateService) {
        this.locateService = locateService;
    }

    private LocateService locateService ;

    @Context
    private MessageContext messageContext;


    @Override
    public PopulateResponse populateGazetteer(String gazetteerName,
                                              String tenantName,
                                              @Multipart(value = "addressDataSetFile") Attachment attachment,
                                              @Multipart("projection") String projection,
                                              @Multipart("addressColumn") int addressColumn,
                                              @Multipart("xColumn") int xColumn,
                                              @Multipart("yColumn") int yColumn,
                                              @Multipart("delimiter") String delimiter,

                                              @Multipart(value = "searchLogic", required = false) String searchLogic) throws ServerException {
        PopulateResponse populateResponse = new PopulateResponse();
        try {
            populateResponse = locateService.populateGazetteer(gazetteerName, tenantName, attachment, projection, addressColumn, xColumn, yColumn, delimiter, searchLogic ,messageContext);
        } catch (Exception e) {
            populateResponse.setSuccess(false);
            log.error(e);
            return populateResponse;
        }
        return populateResponse;
    }

    @Override
    public GazetteerNames getGazetteerNames(String tenantName) throws ServerException {
        locateService = new LocateService();
        GazetteerNames gazetteerNames = new GazetteerNames();
        try {
            gazetteerNames = locateService.getGazetteerNames(tenantName, messageContext);
        } catch (Exception exception) {
            log.error(exception);
            throw new ServerException(exception.getMessage());
        }
        return gazetteerNames;
    }
}
