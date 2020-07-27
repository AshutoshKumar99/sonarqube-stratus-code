package com.pb.stratus.connect.services.rest;


import com.pb.gazetteer.webservice.LocateException_Exception;
import com.pb.gazetteer.webservice.SearchParameters;
import com.pb.stratus.controller.InvalidGazetteerException;
import com.pb.stratus.controller.UnknownSrsException;
import com.pb.stratus.controller.UnknownTenantException;
import com.pb.stratus.controller.application.Application;
import com.pb.stratus.controller.application.ApplicationStartupFilter;
import com.pb.stratus.controller.configuration.TenantProfileManager;
import com.pb.stratus.controller.service.RESTLocatorProxy;
import com.pb.stratus.core.exception.ConfigurationException;
import com.pb.stratus.security.core.common.Constants;
import com.pb.stratus.security.core.common.MimeMapping;
import com.pb.stratus.security.core.common.MimeTypes;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;


/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 7/23/12
 * Time: 5:17 PM
 * To change this template use File | Settings | File Templates.
 */

@Path("/")
public class RESTLocateService {
    /**
     * Introduced logging
     */
    private Logger logger = LogManager.getLogger(RESTLocateService.class);
    private static final Logger LOG = LogManager.getLogger(RESTLocateService.class);

    @Context
    ServletContext context;

    @Context
    MessageContext mContext;

    /** Conn-15817 **/
    private final int DEFAULT_SEARCH_MAX_RESULT_COUNT = 500;

    private String getFirstTenant()
    {
        String tenantList[] = ((String) mContext.get("TenantName")).split(" ");
        return tenantList[0];
    }

    @GET
    @Path("/{tenant:.*}/gazetteers/{gazetteername:.*}/search.{rep}")
    public Response search(
            @PathParam(Constants.KEY_TENANT) String tenant,
            @QueryParam(Constants.KEY_SRS) String srs,        //optional
            @PathParam(Constants.KEY_GAZETTEER_NAME) String gazetteer,
            @QueryParam(Constants.KEY_QUERY) String query,
            @QueryParam(Constants.KEY_MAX_RESULT_COUNT) String count,    //optional
            @PathParam(Constants.KEY_REP) String rep) throws LocateException_Exception {
        if (rep == null) {
            LOG.error("representation not specified.");
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("representation parameter not specified (i.e. json).")
                    .build();
        }
        String responseMimeType = getMimeType(rep);
        if (responseMimeType == null) {
            LOG.error("Unknown representation: " + rep);
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("representation not supported.")
                    .build();
        }

        if (tenant == null) {
            LOG.error("tenant parameter must be specified");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("tenant parameter must be specified.").build();
        }

        if (gazetteer == null) {
            LOG.error("gazetteer parameter must be specified");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("gazetteer parameter must be specified.").build();
        }



        switch (MimeTypes.fromValue(responseMimeType)) {
            case GEOJSON:
            case JSON:
                if (!StringUtils.isEmpty(query)) {
                    try {
                        SearchParameters params = new SearchParameters();
                        if (!StringUtils.isEmpty(count)) {
                            params.setMaxRecords(Integer.parseInt(count));
                        } else {
                            params.setMaxRecords(DEFAULT_SEARCH_MAX_RESULT_COUNT);
                        }
                        params.setGazetteerName(gazetteer);
                        params.setTenantName(tenant);
                        params.setSearchString(query);

                        JSONObject obj = getRestLocateProxy(context, tenant).search(params, srs);
                        return Response.ok(new LocatorJsonObject(obj), "application/json").build();
                    } catch (UnknownTenantException ex) {
                        LOG.error("Unknown tenant is specified");
                        return Response.status(Response.Status.BAD_REQUEST)
                                .entity("unknown tenant is specified.").build();
                    } catch (ConfigurationException e) {
                        LOG.error("Unknown tenant is specified");
                        return Response.status(Response.Status.BAD_REQUEST)
                                .entity("unknown tenant is specified.").build();
                    }catch (UnknownSrsException e) {
                        LOG.error("Unknown srs is specified");
                        return Response.status(Response.Status.BAD_REQUEST)
                                .entity("unknown srs is specified.").build();
                    }catch (NumberFormatException e) {
                        LOG.error("count specified should be integer");
                        return Response.status(Response.Status.BAD_REQUEST)
                                .entity("count specified should be integer.").build();
                    } catch (IllegalArgumentException e) {
                        LOG.error(e.getMessage());
                        return Response.status(Response.Status.BAD_REQUEST)
                                .entity(e.getMessage()).build();

                    } catch (InvalidGazetteerException e) {
                        LOG.error("Invalid gazetteer name is specified.");
                        return Response.status(Response.Status.BAD_REQUEST)
                                .entity("Invalid gazetteer name is specified.").build();

                    }
                } else {
                    JSONObject obj = new JSONObject();
                    obj.put("SearchResults", new JSONObject());
                    return Response.ok(new LocatorJsonObject(obj), "application/json").build();
                }
            default:
                return Response.status(Response.Status.NOT_FOUND).entity(
                        "representation not supported.").build();
        }
    }


    /**
     * July 27th Adding authorization param
     *
     * @param tenant
     * @param rep
     * @return
     */
    @GET
    @Path("/{tenant:.*}/gazetteers.{rep}")
    public Response listGazetteers(
            @PathParam(Constants.KEY_TENANT) String tenant,
            @PathParam(Constants.KEY_REP) String rep) throws LocateException_Exception {

        if (rep == null) {
            LOG.error("representation not specified.");
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("representation parameter not specified (i.e. json).")
                    .build();
        }

        String responseMimeType = getMimeType(rep);

        if (responseMimeType == null) {
            LOG.error("Unknown representation: " + rep);
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("representation not supported.")
                    .build();
        }

        //tenant = getFirstTenant();

        if (tenant == null) {
            LOG.error("tenant parameter must be specified");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("tenant parameter must be specified.").build();
        }


        switch (MimeTypes.fromValue(responseMimeType)) {
            case GEOJSON:
            case JSON:
                try {
                    JSONObject obj = getRestLocateProxy(context, tenant).listGazetteer(tenant);
                    return Response.ok(new LocatorJsonObject(obj), "application/json").build();

                } catch (UnknownTenantException e) {
                    LOG.error("Unknown tenant is specified");
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("unknown tenant is specified.").build();
                } catch (ConfigurationException e) {
                    LOG.error("Unknown tenant is specified");
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("unknown tenant is specified.").build();
                } catch (IllegalArgumentException e) {
                    LOG.error(e.getMessage());
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(e.getMessage()).build();
                }
            default:
                return Response.status(Response.Status.NOT_FOUND).entity(
                        "representation not supported.").build();
        }

    }

    @GET
    @Path("/{tenant:.*}/gazetteers/{gazetteername:.*}/describe.{rep}")
    public Response describeGazetteer(
            @PathParam(Constants.KEY_GAZETTEER_NAME) String gazetteerName,
            @PathParam(Constants.KEY_TENANT) String tenant,
            @PathParam(Constants.KEY_REP) String rep) throws LocateException_Exception {
        if (rep == null) {
            LOG.error("representation not specified.");
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("representation parameter not specified (i.e. json).")
                    .build();
        }
        String responseMimeType = getMimeType(rep);
        if (responseMimeType == null) {
            LOG.error("Unknown representation: " + rep);
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("representation not supported.")
                    .build();
        }

        //tenant = getFirstTenant();

        if (tenant == null) {
            LOG.error("tenant parameter must be specified");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("tenant parameter must be specified.").build();
        }


        switch (MimeTypes.fromValue(responseMimeType)) {
            case GEOJSON:
            case JSON:
                try {
                    JSONObject obj = getRestLocateProxy(context, tenant).describeGazetteer(tenant, gazetteerName);
                    return Response.ok(new LocatorJsonObject(obj), "application/json").build();
                } catch (UnknownTenantException e) {
                    LOG.error("Unknown tenant is specified");
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("unknown tenant is specified.").build();
                } catch (ConfigurationException e) {
                    LOG.error("Unknown tenant is specified");
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("unknown tenant is specified.").build();
                } catch (IllegalArgumentException e) {
                    LOG.error(e.getMessage());
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(e.getMessage()).build();
                } catch (InvalidGazetteerException e) {
                    LOG.error("Invalid Gazetteer Name entered.");
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("Enter a valid gazetteer name").build();
                }

            default:
                return Response.status(Response.Status.NOT_FOUND).entity(
                        "representation not supported.").build();

        }
    }

    private RESTLocatorProxy getRestLocateProxy(ServletContext context, String tenant) {
        Application application = (Application) context
                .getAttribute(ApplicationStartupFilter.APPLICATION_ATTRIBUTE_NAME);
        TenantProfileManager tenantProfileManager = application
                .getTenantProfileManager();
        return tenantProfileManager.getProfile(tenant.toLowerCase()).getLocateRestProxy();
    }

    private String getMimeType(String rep) {
        if (rep != null) {
            return MimeMapping.getMimeForType(rep);
        } else {
            return null;
        }
    }

}
