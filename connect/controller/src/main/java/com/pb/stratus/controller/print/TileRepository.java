package com.pb.stratus.controller.print;

import com.pb.stratus.controller.print.image.ImageReader;
import com.pb.stratus.security.core.http.IHttpRequestExecutor;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.util.UriUtils;

import javax.servlet.ServletException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;


/**
 * A repository of tile images.  
 */
public class TileRepository
{
    private ClientHttpRequestFactory clientRequestFactory;
    private IHttpRequestExecutor iHttpRequestExecutor;
    private ImageReader imageReader;
    private URL tileServiceUrl;
    private String tenantName;
    private static final String NAMED_TILES = "NamedTiles/";
    private static final String FORWARD_SLASH = "/";
    
    /**
     * 
     * @param tenantName
     * @param iHttpRequestExecutor
     * @param reader
     * @param tileServiceUrl
     * @param clientRequestFactory
     */
	public TileRepository(String tenantName,
			IHttpRequestExecutor iHttpRequestExecutor, ImageReader reader,
			URL tileServiceUrl, ClientHttpRequestFactory clientRequestFactory) {
		this.tenantName = tenantName;
		this.iHttpRequestExecutor = iHttpRequestExecutor;
		this.imageReader = reader;
		this.tileServiceUrl = tileServiceUrl;
		this.clientRequestFactory = clientRequestFactory;
	}

    /**
     * Retrieves a single tile image
     * 
     * @param layerName the name of the layer the tile should be queried from
     * @param row the row of the tile 
     * @param col the column of the tile
     * @param level the zoom level from which to retrieve the tile
     * @param imageMimeType 
     * @return a BufferedImage containing the image data of the requested tile
     * @throws IOException
     */
	public BufferedImage getTile(String layerName, int row, int col, int level,
			String imageMimeType) throws IOException {
		URL url = createTileUrl(layerName, row, col, level, imageMimeType
				.substring(6));

		ClientHttpResponse restResponse = null;
		try {
			restResponse = getClientResponse(new URI(url.toString()));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
        if (restResponse != null)
        {
            HttpStatus status = restResponse.getStatusCode();
            InputStream input = null;
            if (status == HttpStatus.OK) {
                input = restResponse.getBody();
                return imageReader.readFromStream(input);
            }
        }

		return null;
	}
    
    /**
     * 
     * @param layerName
     * @param row
     * @param col
     * @param level
     * @param imageMimeType
     * @return
     * @throws MalformedURLException
     */
    private URL createTileUrl(String layerName, int row, int col, int level, 
            String imageMimeType) throws MalformedURLException
    {
		StringBuilder builder = new StringBuilder(tileServiceUrl.toString());
        String encodedUri ="";
        if(!layerName.startsWith("/")){
            builder.append(FORWARD_SLASH + this.tenantName.toLowerCase()
                    + FORWARD_SLASH + NAMED_TILES);
        }
		try {
            builder.append(layerName);
			builder.append(FORWARD_SLASH + (level + 1));
			builder.append(FORWARD_SLASH + (col + 1) + ":" + (row + 1));
            //fix for barnsley,CONN-14689
            if (imageMimeType.equalsIgnoreCase("jpeg"))
            {
                imageMimeType = "jpg";
            }
			builder.append(FORWARD_SLASH + "tile."
					+ URLEncoder.encode(imageMimeType, "UTF-8"));
            // Using UriUtils instead of URLEncoder to encode final uri so that any / (forward slashes) present in repository path(layerName)
            // do not encoded while other special chars like space get encoded.
            encodedUri = UriUtils.encodeUri(builder.toString(), "UTF-8");
		} catch (UnsupportedEncodingException uex) {
			// UTF-8 must be supported
			throw new Error(uex);
		}
        return new URL(encodedUri);
	}

    /**
     * Describes the given layer.
     * 
     * @param layerName the name of the layer to describe
     * @return a description object 
     * @throws IOException if the describe operation fails due to an I/O 
     *         communication problem
     * @throws IllegalArgumentException if the layer with the given name cannot 
     *         be found
     */
    public TileLayerDescription describe(String layerName) throws ServletException,IOException
    {
    	ClientHttpResponse restResponse = getClientResponse(createDescribeUrl(layerName));
		HttpStatus status = restResponse.getStatusCode();

		if (status == HttpStatus.OK)
		{
			InputStream input = restResponse.getBody();
			JSONObject json = JSONObject.fromObject(IOUtils.toString(input));
			if (json.has("Response")
					&& json.getString("Response").contains(
							"Description not found for map")) {
				throw new IllegalArgumentException("Tile layer '" + layerName
						+ "' doesn't exist");
			}
			JSONObject jsonResponse = json.getJSONObject("Response");
			JSONObject jsonBounds = jsonResponse.getJSONObject("bounds");
			String srs = jsonResponse.getString("coordSys");
			BoundingBox bounds = new BoundingBox(jsonBounds.getDouble("maxY"),
					jsonBounds.getDouble("minY"), jsonBounds.getDouble("minX"),
					jsonBounds.getDouble("maxX"), srs);
			int tileSize = jsonResponse.getInt("tileWidth");
			if (tileSize != jsonResponse.getInt("tileHeight")) {
				throw new IllegalStateException("Only square tiles are "
						+ "currently supported");
			}
			return new TileLayerDescription(bounds, tileSize);
		}
        return null;
    }
    
    /**
     * 
     * @param uri
     * @return
     * @throws IOException
     */
	private ClientHttpResponse getClientResponse(URI uri) throws IOException {
		ClientHttpRequest restRequest = clientRequestFactory.createRequest(uri,
				HttpMethod.GET);
		return iHttpRequestExecutor.executeRequest(restRequest);
	}
    
    /**
     * 
     * @param layerName
     * @return
     * @throws ServletException
     */
    private URI createDescribeUrl(String layerName) throws ServletException
    {
		StringBuilder b = new StringBuilder(tileServiceUrl.toString());
        String encodedUri = "";
        if(!layerName.startsWith("/")){
            b.append(FORWARD_SLASH + this.tenantName + FORWARD_SLASH + NAMED_TILES);
        }
		try {
            b.append(layerName);
            b.append(FORWARD_SLASH + "description.json");
            // Using UriUtils instead of URLEncoder to encode final uri so that any /(forward slashes) present in repository path(layerName)
            // do not encoded while other special chars like space get encoded.
            encodedUri = UriUtils.encodeUri(b.toString(), "UTF-8");
		} catch (UnsupportedEncodingException uex) {
			// UTF-8 must be supported
			throw new Error(uex);
		}
		try {
			return new URI(encodedUri);
		} catch (URISyntaxException ex)
		{
			throw new ServletException(
					"Some parameter(s) cannot be used as URI components");
		}
    }

}