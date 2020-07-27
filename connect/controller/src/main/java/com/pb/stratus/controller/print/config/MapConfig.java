package com.pb.stratus.controller.print.config;

import com.pb.stratus.controller.IllegalRequestException;
import com.pb.stratus.controller.print.PrintUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/*
 * This would have been better implemented by creating Java or XML beans from
 * Schema definition, since there is none at the moment creating a Java class
 * to populate the parsed information. 
 */

public class MapConfig {

    private String defaultCopyright;

	private String defaultGazetteerName;
    private String defaultGazetteerService;

    private String internationalGeocoderTargetCountry;

    private Watermark watermark;

	private MapConfigDefinition mapConfigDefinition;

    private List<MapDefinition> mapDefinitions;

    private List<ThirdPartyAPIKey> thirdPartyAPIKeys;

	private static final String ILLEGAL_MAP_REQUEST = "Map does not exist in the map configuration: ";

	public static class Watermark {

        private String imagePath;

        private float opacity;

        private int tileWidth;

        private int tileHeight;

		public String getImagePath() {
            return imagePath;
        }

		public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }

		public float getOpacity() {
            return opacity;
        }

		public void setOpacity(float opacity) {
            this.opacity = opacity;
        }

		public int getTileWidth() {
            return tileWidth;
        }

		public void setTileWidth(int tileWidth) {
            this.tileWidth = tileWidth;
        }

		public int getTileHeight() {
            return tileHeight;
        }

		public void setTileHeight(int tileHeight) {
            this.tileHeight = tileHeight;
        }

    }

	public static class ThirdPartyAPIKey {
        private LayerServiceType service;
        private String key;

		public void setService(LayerServiceType service) {
            this.service = service;
        }

		public LayerServiceType getService() {
            return service;
        }

		public void setKey(String key) {
            this.key = key;
        }

		public String getKey() {
            return key;
        }
    }

	/**
	 * Represents the ATTRIBUTE information from the XML configuration file.
	 * 
	 * @author sa021sh
	 * 
	 */
	public static class MapConfigDefinition {

		private double maxBoundsLeft;
		private double maxBoundsRight;
		private double maxBoundsTop;
		private double maxBoundsBottom;
		private double initialX;
		private double initialY;
		private int maxZoomLevels;
		private double maxResolution;
		private String units;
		private String projection;
		private int initialZoom;
		private int searchZoom;
        private int addressSearchResultsCount;

		public double getMaxBoundsLeft() {
			return maxBoundsLeft;
		}

		public void setMaxBoundsLeft(double maxBoundsLeft) {
			this.maxBoundsLeft = maxBoundsLeft;
		}

		public double getMaxBoundsRight() {
			return maxBoundsRight;
		}

		public void setMaxBoundsRight(double maxBoundsRight) {
			this.maxBoundsRight = maxBoundsRight;
		}

		public double getMaxBoundsTop() {
			return maxBoundsTop;
		}

		public void setMaxBoundsTop(double maxBoundsTop) {
			this.maxBoundsTop = maxBoundsTop;
		}

		public double getMaxBoundsBottom() {
			return maxBoundsBottom;
		}

		public void setMaxBoundsBottom(double maxBoundsBottom) {
			this.maxBoundsBottom = maxBoundsBottom;
		}

		public double getInitialX() {
			return initialX;
		}

		public void setInitialX(double initialX) {
			this.initialX = initialX;
		}

		public double getInitialY() {
			return initialY;
		}

		public void setInitialY(double initialY) {
			this.initialY = initialY;
		}

		public int getMaxZoomLevels() {
			return maxZoomLevels;
		}

		public void setMaxZoomLevels(int maxZoomLevels) {
			this.maxZoomLevels = maxZoomLevels;
		}

		public double getMaxResolution() {
			return maxResolution;
		}

		public void setMaxResolution(double maxResolution) {
			this.maxResolution = maxResolution;
		}

		public String getUnits() {
			return units;
		}

		public void setUnits(String units) {
			this.units = units;
		}

		public String getProjection() {
			return projection;
		}

		public void setProjection(String projection) {
			this.projection = projection;
		}

		public int getInitialZoom() {
			return initialZoom;
		}

		public void setInitialZoom(int initialZoom) {
			this.initialZoom = initialZoom;
		}

		public int getSearchZoom() {
			return searchZoom;
		}

		public void setSearchZoom(int searchZoom) {
			this.searchZoom = searchZoom;
		}

        public int getAddressSearchResultsCount() {
            return addressSearchResultsCount;
        }

        public void setAddressSearchResultsCount(int addressSearchResultsCount) {
            this.addressSearchResultsCount = addressSearchResultsCount;
        }

	}

	public static class MapDefinition {

        private String mapName;

        private String repositoryPath;

        private String copyright;

        private float opacity;

        private LayerServiceType service;

        private String friendlyName;
        
        private String imageMimeType;

        private String url;

        private List<String> legendUrlList;

        public List<String> getLegendUrlList() {
            return legendUrlList;
        }

        public void setLegendUrlList(List<String> legendUrlList) {
            this.legendUrlList = legendUrlList;
        }

        public String getMapName() {
            return mapName;
        }

		public void setMapName(String mapName) {
            this.mapName = mapName;
        }

        public String getRepositoryPath() {
            return repositoryPath;
        }

        public void setRepositoryPath(String repositoryPath) {
            this.repositoryPath = repositoryPath;
        }

        public String getFriendlyName() {
            return friendlyName;
        }
        
		public void setFriendlyName(String friendlyName) {
            this.friendlyName = friendlyName;
        }
        
		public String getCopyright() {
            return copyright;
        }

		public void setCopyright(String copyright) {
            this.copyright = copyright;
        }

		public float getOpacity() {
            return opacity;
        }

		public void setOpacity(float opacity) {
            this.opacity = opacity;
        }

		public LayerServiceType getService() {
            return service;
        }

		public void setService(LayerServiceType service) {
            this.service = service;
        }

		public String getImageMimeType() {
            return imageMimeType;
    }

		public void setImageMimeType(String imageMimeType) {
            this.imageMimeType = imageMimeType;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }


    }

	public String getDefaultCopyright() {
        return defaultCopyright;
    }

	public void setDefaultCopyright(String defaultCopyright) {
        this.defaultCopyright = defaultCopyright;
    }

	public String getDefaultGazetteerName() {
		return defaultGazetteerName;
	}

	public void setDefaultGazetteerName(String defaultGazetteerName) {
		this.defaultGazetteerName = defaultGazetteerName;
	}

    public void setDefaultGazetteerService(String defaultGazetteerService) {
        this.defaultGazetteerService = defaultGazetteerService;
    }

    public String getDefaultGazetteerService() {
        return defaultGazetteerService;
    }

    public String getInternationalGeocoderTargetCountry() {
        return internationalGeocoderTargetCountry;
    }

    public void setInternationalGeocoderTargetCountry(String internationalGeocoderTargetCountry) {
        this.internationalGeocoderTargetCountry = internationalGeocoderTargetCountry;
    }

	public Watermark createWatermark() {
        if (watermark == null)
            watermark = new Watermark();
        return watermark;
    }

	public ThirdPartyAPIKey createThirdPartyAPIKey() {
        return new ThirdPartyAPIKey();
    }

	public MapDefinition createMapDefinition() {
        return new MapDefinition();
    }

	public Watermark getWatermark() {
        return watermark;
    }

	public void setWatermark(Watermark watermark) {
        this.watermark = watermark;
    }

	public List<MapDefinition> getMapDefinitions() {
        return mapDefinitions;
    }

	public void setMapDefinitions(List<MapDefinition> mapDefinitions) {
        this.mapDefinitions = mapDefinitions;
    }

	public MapConfigDefinition getMapConfigDefinition() {
		return mapConfigDefinition;
	}

	public void setMapConfigDefinition(MapConfigDefinition mapConfigDefinition) {
		this.mapConfigDefinition = mapConfigDefinition;
	}

	public void setThirdPartyAPIKeys(List<ThirdPartyAPIKey> thirdPartyAPIKeys) {
        this.thirdPartyAPIKeys = thirdPartyAPIKeys;
    }

	public List<ThirdPartyAPIKey> getThirdPartyAPIKeys() {
        return thirdPartyAPIKeys;
    }

	public MapDefinition getMapDefinitionByMapName(String mapName) {
		if (mapDefinitions == null || StringUtils.isEmpty(mapName)) {
            throw new IllegalRequestException(ILLEGAL_MAP_REQUEST + mapName);
        }
		for (MapDefinition mapDefinition : mapDefinitions) {
            String mapNameFromService = PrintUtils.processMapName(mapName);
			if (mapDefinition.getMapName().equals(mapNameFromService)) {
                return mapDefinition;
            }
        }
		// It should not happen that the maps requested are different than the
		// map config.
        throw new IllegalRequestException(ILLEGAL_MAP_REQUEST + mapName);
    }

    public MapDefinition getMapDefinitionByMapNameOrNull(String mapName) {
        if (mapDefinitions == null || StringUtils.isEmpty(mapName)) {
            throw new IllegalRequestException(ILLEGAL_MAP_REQUEST + mapName);
        }
        for (MapDefinition mapDefinition : mapDefinitions) {
            String mapNameFromService = PrintUtils.processMapName(mapName);
            if (mapDefinition.getMapName().equals(mapNameFromService)) {
                return mapDefinition;
            }
        }
        return null;
    }
}
