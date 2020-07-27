package com.pb.stratus.connect.services.rest;


import com.pb.gazetteer.webservice.LocateException_Exception;
import com.pb.stratus.controller.UnknownTenantException;
import com.pb.stratus.controller.application.Application;
import com.pb.stratus.controller.application.ApplicationStartupFilter;
import com.pb.stratus.controller.configuration.TenantProfileManager;
import com.pb.stratus.controller.service.RESTAnalystProxy;
import com.pb.stratus.core.exception.ConfigurationException;
import com.pb.stratus.security.core.common.Constants;
import com.pb.stratus.security.core.common.MimeMapping;
import com.pb.stratus.security.core.common.MimeTypes;
import net.sf.json.JSONObject;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path("/")
public class RESTAnalystService {

	private static final Logger LOG = LogManager.getLogger(RESTAnalystService.class);

	@Context
	ServletContext context;

	@Context
	MessageContext mContext;

	@GET
	@Path("/{tenant:.*}/watermarks.{rep}")
	public Response listWatermarks(
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

		if (tenant == null) {
			LOG.error("tenant parameter must be specified");
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("tenant parameter must be specified.").build();
		}

		switch (MimeTypes.fromValue(responseMimeType)) {
			case GEOJSON:
			case JSON:
				try {
					JSONObject obj = getRestAnalystProxy(context, tenant).listWatermarks(tenant);
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

	private RESTAnalystProxy getRestAnalystProxy(ServletContext context, String tenant) {
		Application application = (Application) context
				.getAttribute(ApplicationStartupFilter.APPLICATION_ATTRIBUTE_NAME);
		TenantProfileManager tenantProfileManager = application
				.getTenantProfileManager();
		return tenantProfileManager.getProfile(tenant.toLowerCase()).getAnalystRestProxy();
	}

	private String getMimeType(String rep) {
		if (rep != null) {
			return MimeMapping.getMimeForType(rep);
		} else {
			return null;
		}
	}

}
