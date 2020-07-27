package com.pb.gazetteer.rest;

import com.pb.gazetteer.PopulateResponse;
import com.pb.gazetteer.webservice.GazetteerNames;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.rmi.ServerException;

/**
 * Created by JU002AH on 4/11/2019.
 */
@Path("/")
public interface RESTLocateServiceInterface {

    @POST
    @Path("/{tenantName}/{gazetteerName}/populate/")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public PopulateResponse populateGazetteer(@PathParam("gazetteerName") String gazetteerName,
                                             @PathParam("tenantName") String tenantName,
                                             @Multipart(value = "addressDataSetFile") Attachment attachment,
                                             @Multipart("projection") String projection,
                                             @Multipart("addressColumn") int addressColumn,
                                             @Multipart("xColumn") int xColumn,
                                             @Multipart("yColumn") int yColumn,
                                             @Multipart("delimiter") String delimiter,
                                             @Multipart(value = "searchLogic", required = false) String searchLogic
    ) throws ServerException;

    @GET
    @Path("/{tenantName}/getGazetteerNames")
    @Produces(MediaType.APPLICATION_JSON)
    public GazetteerNames getGazetteerNames(@PathParam("tenantName") String tenantName) throws ServerException;
}
