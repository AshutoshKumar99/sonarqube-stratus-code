package com.pb.stratus.controller.print;

import java.util.List;

/**
 * 
 * @author vi001ty
 *
 */
public class BingImageryMetaData {
	
	public static final String BRAND_LOGO_URI_KEY = "brandLogoUri";
	public static final String RESOURCE_SETS_KEY = "resourceSets";
	public static final String RESOURCES_KEY = "resources";
	public static final String IMAGE_URL_KEY = "imageUrl";
	public static final String IMAGE_URL_SUBDOMAINS_KEY = "imageUrlSubdomains";
	public static final String IMAGERY_PROVIDERS_KEY = "imageryProviders";
	public static final String ATTRIBUTION_KEY = "attribution";
	public static final String IMAGE_WIDTH_KEY = "imageWidth";
	public static final String COVERAGE_AREAS_KEY = "coverageAreas";
	public static final String BBOX_KEY = "bbox";
	public static final String ZOOM_MAX_KEY = "zoomMax";
	public static final String ZOOM_MIN_KEY = "zoomMin";
	
	private String imageUrl;
	private int statusCode;
	private int imageWidth;
	private String logoUrl;
	private List<String> imageUrlSubdomains;
	private List<ImageryProvider> providers;
	
	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public List<ImageryProvider> getProviders() {
		return providers;
	}

	public void setProviders(List<ImageryProvider> providers) {
		this.providers = providers;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public List<String> getImageUrlSubdomains() {
		return imageUrlSubdomains;
	}

	public void setImageUrlSubdomains(List<String> imageUrlSubdomains) {
		this.imageUrlSubdomains = imageUrlSubdomains;
	}

}

class ImageryProvider{
	private String attribution;
	private List<ProviderCoverageArea> CoverageAreas;
	
	public String getAttribution() {
		return attribution;
	}
	public void setAttribution(String attribution) {
		this.attribution = attribution;
	}
	public List<ProviderCoverageArea> getCoverageAreas() {
		return CoverageAreas;
	}
	public void setCoverageAreas(List<ProviderCoverageArea> coverageAreas) {
		CoverageAreas = coverageAreas;
	}
}

class ProviderCoverageArea{
	private double[] boundingBox;
	private int zoomMax;
	private int zoomMin;
	
	public double[] getBoundingBox() {
		return boundingBox;
	}
	public void setBoundingBox(double[] boundingBox) {
		this.boundingBox = boundingBox;
	}
	public int getZoomMax() {
		return zoomMax;
	}
	public void setZoomMax(int zoomMax) {
		this.zoomMax = zoomMax;
	}
	public int getZoomMin() {
		return zoomMin;
	}
	public void setZoomMin(int zoomMin) {
		this.zoomMin = zoomMin;
	}
	
}
