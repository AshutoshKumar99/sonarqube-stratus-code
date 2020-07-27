/*****************************************************************************
 * Copyright (c) 2008 Pitney Bowes Software Inc.
 * All rights reserved.
 * Confidential Property of Pitney Bowes Software Inc.
 *
 * $Workfile: $
 * $Revision: 15805 $
 * $LastChangedDate: 2011-05-05 22:51:35 -0400 (Thu, 05 May 2011) $
 *
 * $HeadURL: http://repository/repos/manager/MapInfoDeveloper/trunk/Service/Feature/src/com/mapinfo/midev/service/feature/rest/v1/FeatureJSONProvider.java $
 *****************************************************************************/
package com.pb.stratus.connect.services.rest;

import net.sf.json.util.JSONUtils;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Produces({"application/json"})
@Provider
public final class LocatorJSONProvider implements MessageBodyWriter<LocatorJsonObject> {

	private static final String CHARSET = "UTF-8";
	/**
	 * Write response object into output stream.
	 */
	public void writeTo(LocatorJsonObject r, Class<?> cl,
			Type type, Annotation[] annot, MediaType mtype,
			MultivaluedMap<String, Object> map, OutputStream os)
			throws IOException, WebApplicationException {
		os.write(JSONUtils.valueToString(r.getJSON()).getBytes(CHARSET));
	}

	/**
	 * Return size of the object to be send as response.
	 * Current implementation always return -1,
	 * size will be calculated by rest engine.
	 */
	public long getSize(LocatorJsonObject r, Class<?> cl,
			Type type, Annotation[] annot, MediaType mediatype) {
		return -1;
	}

	/**
	 * Verify that cl can be published using this provider.
	 *
	 */
	public boolean isWriteable(Class<?> cl, Type type, Annotation[] annot,
			MediaType mediaType) 
	{
		return LocatorJsonObject.class.isAssignableFrom(cl);
	}
}
