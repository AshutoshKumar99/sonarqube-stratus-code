package com.pb.stratus.controller.action;

import com.pb.stratus.controller.exception.QueryConfigException;
import com.pb.stratus.core.common.Constants;
import com.pb.stratus.core.configuration.ConfigFileType;
import com.pb.stratus.core.configuration.CustomerConfigDirHolder;
import com.pb.stratus.security.core.resourceauthorization.ResourceAuthorizationConfig;
import com.pb.stratus.security.core.resourceauthorization.ResourceException;
import com.pb.stratus.security.core.resourceauthorization.ResourceType;
import com.pb.stratus.security.core.util.AuthorizationUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Provides the list of templates based on type parameter.
 * type=info for info templates
 * type=edit for edit templates
 *
 * @author vi001ty
 */
public class ListTemplatesAction extends DataInterchangeFormatControllerAction {

	private static final String INFO_TEMPLATE_TYPE = "info";
	private static final String EDIT_TEMPLATE_TYPE = "edit";
	private static final String EDIT_TEMPLATE_EXT = "xml";
	private static final String INFO_TEMPLATE_EXT = "ts";
	private CustomerConfigDirHolder customerConfigDirHolder;
	private String tenantName;

	public ListTemplatesAction(CustomerConfigDirHolder configDirHolder, String tenantName) {
		this.customerConfigDirHolder = configDirHolder;
		this.tenantName = tenantName;
	}

	protected Object createObject(HttpServletRequest request) {
		String templateType = request.getParameter("type");
		List<String> templateList = new ArrayList<String>();
		String templatePath, ext;
		if (templateType != null) {
			if (INFO_TEMPLATE_TYPE.equals(templateType)) {
				templatePath = Constants.INFO_TEMPLATE_URL;
				ext = INFO_TEMPLATE_EXT;
			} else if (EDIT_TEMPLATE_TYPE.equals(templateType)) {
				templatePath = Constants.EDIT_TEMPLATE_URL;
				ext = EDIT_TEMPLATE_EXT;
			} else {
				return templateList;
			}
			String path = this.customerConfigDirHolder.getCustomerConfigDir().getAbsolutePath() + File.separator + this.tenantName + templatePath;
			File[] directories = new File(path).listFiles((dir, name) -> name.toLowerCase().endsWith(ext));

			for (int i = 0; i < directories.length; i++) {
				templateList.add(FilenameUtils.removeExtension(directories[i].getName()));
			}
		}
		return templateList;
	}
}
