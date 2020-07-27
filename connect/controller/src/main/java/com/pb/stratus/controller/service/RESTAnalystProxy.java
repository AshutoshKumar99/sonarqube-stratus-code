package com.pb.stratus.controller.service;

import com.pb.gazetteer.webservice.LocateException_Exception;
import com.pb.stratus.core.configuration.CustomerConfigDirHolder;
import net.sf.json.JSONObject;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.graphdata.utilities.contract.Contract;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is a proxy class for Connect Analyst REST APIs.
 */
public class RESTAnalystProxy {


	public static final String WATERMARK_DIR = "/theme/images/watermark/";

	private static final Logger LOG = LogManager.getLogger(RESTAnalystProxy.class);
	private CustomerConfigDirHolder configDirHolder;

	public RESTAnalystProxy(CustomerConfigDirHolder configDirHolder) {
		this.configDirHolder = configDirHolder;
	}

	public JSONObject listWatermarks(String tenant) throws LocateException_Exception {
		Contract.pre(!StringUtils.isBlank(tenant),
				"Tenant name required");
		JSONObject obj = new JSONObject();
		Path watermarkDir = Paths.get(configDirHolder.getCustomerConfigDir().toString(), tenant, WATERMARK_DIR);
		List<String> watermarks = null;
		try {
			watermarks = getListOfSupportedFilesInDirectory(watermarkDir);
		} catch (IOException e) {
			LOG.error("Error in reading files from watermark folder : " + e.getStackTrace());
			throw new LocateException_Exception("Error in reading files from watermark folder");
		}
		obj.put("Watermarks", watermarks);
		return obj;
	}

	private List<String> getListOfSupportedFilesInDirectory(Path folder) throws IOException {

		Stream<Path> walk = Files.walk(folder);
		return walk.filter(Files::isRegularFile)
				.map(Path::toString)
				.filter((name) -> hasSupportedExtension(name))
				.map(FilenameUtils::getName)
				.collect(Collectors.toList());
	}

	private boolean hasSupportedExtension(String fileName) {
		String name = fileName.toLowerCase();
		return name.endsWith(".gif") || name.endsWith(".jpg") || name.endsWith(".png") ||
				name.endsWith(".jpeg") || name.endsWith(".bmp");
	}

}
