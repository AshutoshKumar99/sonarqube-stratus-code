/*****************************************************************************
 * Copyright (c) 2008 Pitney Bowes Software Inc.
 * All rights reserved.
 * Confidential Property of Pitney Bowes Software Inc.
 *
 * $Workfile: $
 * $Revision: 15805 $
 * $LastChangedDate: 2011-05-06 08:21:35 +0530 (Fri, 06 May 2011) $
 *
 * $HeadURL: http://repository/repos/manager/MapInfoDeveloper/sandbox/alsarawa/trunk/Service/Feature/src/com/mapinfo/midev/service/feature/rest/v1/FeatureExceptionProvider.java $
 *****************************************************************************/
package com.pb.stratus.connect.services.rest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public final class LocatorExceptionProvider implements ExceptionMapper<Throwable>{
	
	
	/**
	 * Wraps the exception with a Response object. 
	 * 
	 * @param throwable - exception
	 * 
	 * @return Response object with exception inside
	 */
	public Response toResponse(Throwable t) {
		/*
		 * by setting the media type to JSON, our MappingJSONProvider will be used.
		 * if the media type is not set, then text/plain type is used.
		 */
		ResponseBuilder builder = Response.status(Status.INTERNAL_SERVER_ERROR).entity(t.getMessage()).type(MediaType.APPLICATION_JSON_TYPE);
			
		Response error	=  builder.build();
		
		return error;		
	}
}