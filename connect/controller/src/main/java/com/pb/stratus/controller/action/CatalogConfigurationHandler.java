package com.pb.stratus.controller.action;

import com.pb.stratus.core.configuration.ConfigFileType;
import com.pb.stratus.core.configuration.ConfigReader;
import com.pb.stratus.security.core.util.AuthorizationUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Agreement for catalog configuration handler.
 * 
 */
public interface CatalogConfigurationHandler {

	/**
	 * This method will check whether the catalog configuration is authorized or
	 * not. If not then we throw exception and show a dialog with a message. If
	 * the configuration is authorized but the requested table in case of
	 * "showNearest" params is not authorized or not present in the
	 * configuration, then we throw the exception and show a information dialog.
	 * 
	 * @param request
	 * @param fileName
	 * @param configFileType
	 * @param configReader
	 * @return void
	 * @throws FileNotFoundException , IOException
	 */
	public boolean handleAuthorization(HttpServletRequest request,
			String fileName, ConfigFileType configFileType, ConfigReader configReader,
            AuthorizationUtils authorizationUtils) throws FileNotFoundException, IOException;

}
