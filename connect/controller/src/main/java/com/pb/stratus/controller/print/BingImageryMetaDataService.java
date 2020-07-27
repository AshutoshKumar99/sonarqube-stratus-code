package com.pb.stratus.controller.print;

import com.pb.stratus.core.configuration.ControllerConfiguration;
import com.pb.stratus.security.core.util.AuthorizationUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BingImageryMetaDataService {

	
	private static Map<String, BingImageryMetaData> cache = new HashMap<String, BingImageryMetaData>();
	private static BingUrlBuilder urlBuilder = new BingUrlBuilder();

	/**
	 * Get Imagery metadata for Bing map
	 * 
	 * @param layerName
	 *            map name.
	 * @return json string containing metadata.
	 */
	public static BingImageryMetaData getImageryMetadata(String layerName, ControllerConfiguration config,
                                                         AuthorizationUtils authorizationUtils)
			throws IOException {
		String bingKey = authorizationUtils.isAnonymousUser() ? config
				.getBingServicesPublicApiKey() : config
				.getBingServicesPrivateApiKey();
		URL url = urlBuilder.constructBingMetaDataURL(layerName, bingKey);
		String jsonString = IOUtils.toString(url.openConnection()
				.getInputStream(), "UTF-8");
		JSONObject json = JSONObject.fromObject(jsonString);
		BingImageryMetaData metadata = new BingImageryMetaData();
		if(json != null ){
			if(json.has(BingImageryMetaData.BRAND_LOGO_URI_KEY)){
				metadata.setLogoUrl(json.getString(BingImageryMetaData.BRAND_LOGO_URI_KEY));
			}
			if (json.has(BingImageryMetaData.RESOURCE_SETS_KEY)
					&& json.getJSONArray(BingImageryMetaData.RESOURCE_SETS_KEY) != null) {
				JSONObject jsonResourceSets = json.getJSONArray(BingImageryMetaData.RESOURCE_SETS_KEY)
						.getJSONObject(0);
				if (jsonResourceSets != null && jsonResourceSets.has(BingImageryMetaData.RESOURCES_KEY)
						&& jsonResourceSets.getJSONArray(BingImageryMetaData.RESOURCES_KEY) != null) {
					JSONObject jsonResources = jsonResourceSets.getJSONArray(
							BingImageryMetaData.RESOURCES_KEY).getJSONObject(0);
					metadata.setImageUrl(jsonResources.getString(BingImageryMetaData.IMAGE_URL_KEY));
					metadata.setImageWidth(jsonResources.getInt(BingImageryMetaData.IMAGE_WIDTH_KEY));
					populateImageryProviders(metadata, jsonResources);
					 
					JSONArray imageUrlSubdomains = jsonResources
							.getJSONArray(BingImageryMetaData.IMAGE_URL_SUBDOMAINS_KEY);
					List<String> subdomainsList = new ArrayList<String>();
					for (Object subdomain : imageUrlSubdomains.toArray()) {
						subdomainsList.add((String) subdomain);
					}
					metadata.setImageUrlSubdomains(subdomainsList);
				}
			}
		}
		cache.put(layerName, metadata);
		return metadata;

	}
	
	private static void populateImageryProviders(BingImageryMetaData metadata,
			JSONObject jsonResources) {
		List<ImageryProvider> providers = new ArrayList<ImageryProvider>();
		List<ProviderCoverageArea> providerCoverageAreas = null;
		ImageryProvider provider = null;
		ProviderCoverageArea coverageArea = null;
		double[] boundingBox = null;
		if(jsonResources != null && jsonResources.has(BingImageryMetaData.IMAGERY_PROVIDERS_KEY)){
			JSONArray imageryProviders = jsonResources.getJSONArray(BingImageryMetaData.IMAGERY_PROVIDERS_KEY);
			for(Object providerJson : imageryProviders.toArray()){
				provider = new ImageryProvider();
				provider.setAttribution(((JSONObject)providerJson).getString(BingImageryMetaData.ATTRIBUTION_KEY));
				providerCoverageAreas = new ArrayList<ProviderCoverageArea>();
				JSONArray coverageAreas = ((JSONObject)providerJson).getJSONArray(BingImageryMetaData.COVERAGE_AREAS_KEY);
				for(Object coverageAreaJson : coverageAreas.toArray()){
					coverageArea = new ProviderCoverageArea();
					coverageArea.setZoomMax(((JSONObject)coverageAreaJson).getInt(BingImageryMetaData.ZOOM_MAX_KEY));
					coverageArea.setZoomMin(((JSONObject)coverageAreaJson).getInt(BingImageryMetaData.ZOOM_MIN_KEY));
					JSONArray bbox = ((JSONObject)coverageAreaJson).getJSONArray(BingImageryMetaData.BBOX_KEY);
					
					boundingBox = new double[4];
					boundingBox[0] = bbox.getDouble(0);
					boundingBox[1] = bbox.getDouble(1);
					boundingBox[2] = bbox.getDouble(2);
					boundingBox[3] = bbox.getDouble(3);
					coverageArea.setBoundingBox(boundingBox);
					providerCoverageAreas.add(coverageArea);
				}
				provider.setCoverageAreas(providerCoverageAreas);
				providers.add(provider);
			}
			metadata.setProviders(providers);
		}
		return;
	}

	public static BingImageryMetaData getImageryMetadataForAttribution(String layerName)
	{
		return cache.get(layerName);
		
	}
	
}
