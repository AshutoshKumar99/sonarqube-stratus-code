package com.pb.stratus.controller.action;

import com.pb.stratus.controller.IllegalRequestException;
import com.pb.stratus.core.configuration.FileSystemConfigReader;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

public abstract class QueryConfigActionImpl extends
		DataInterchangeFormatControllerAction {

	private static final Logger logger = LogManager.getLogger(QueryConfigActionImpl.class);
	
	protected Object createObject(HttpServletRequest request)
			throws IOException {
		String tableName = request.getParameter("tableName");
		String queryName = request.getParameter("queryName");
	
		if (StringUtils.isBlank(tableName)) {
			throw new IllegalRequestException(
					"tableName cannot be null");
		}
		
		if (StringUtils.isBlank(queryName)){
			
			//
		}
		
		File file = null;
		Object response = null;
		String filePath = null;
		try {
			filePath = getFilePath(tableName);
			file = new File(filePath);
			if (file.isDirectory()) {

				//clientResponse = readQuery(queryName, filePath);
			} else {
				logger.error("Directory with given table name does not exist ");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (null == response) {
			// sending empty JSON Object as per the front end requirements
			response = new JSONObject();
		}
		return response;
	}

	private FileSystemConfigReader reader = null;

	/*
	 * get the file from the query config folder
	 */
	public QueryConfigActionImpl(FileSystemConfigReader FileSystemConfigReader) {
		this.reader = FileSystemConfigReader;
	}

	public String getFilePath(String tableName) throws Exception {

		String filePath = reader.getBasePath() + "\\"
				+ reader.getCustomerName() + "\\queryconfig\\" + tableName;
		return filePath;
	}

}
