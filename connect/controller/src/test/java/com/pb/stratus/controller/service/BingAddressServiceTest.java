/*****************************************************************************
 *       Copyright © 2011, Pitney Bowes Software Inc.
 *       All  rights reserved.
 *       Confidential Property of Pitney Bowes Software Inc.
 *
 * $Author: AL021CH $
 * $Revision: 20401 $
 * $LastChangedDate: 2011-09-11 11:47:27 +0530 (Sun, 11 Sep 2011) $
 *
 * $HeadURL: http://noisvnmsprod/svn/stratus-connect/stratus/trunk/connect/controller/src/test/java/com/pb/stratus/controller/service/BingAddressServiceTest.java$
 *****************************************************************************/
package com.pb.stratus.controller.service;

import com.pb.gazetteer.webservice.Address;
import com.pb.gazetteer.webservice.SearchParameters;
import com.pb.stratus.core.configuration.ControllerConfiguration;
import com.pb.stratus.security.core.util.AuthorizationUtils;
import net.sf.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public final class BingAddressServiceTest {

	private ControllerConfiguration mockConfig;
    private AuthorizationUtils mockAuthorizationUtils;
	private Authentication authentication;
    private SecurityContext m_context;
    private String apiKey = "ThisIsMyMapKey";
	
	@Before
    public void setUp() throws Exception
    {
        mockConfig = mock(ControllerConfiguration.class);
        mockAuthorizationUtils = mock(AuthorizationUtils.class);
        authentication = createAnonymousToken();
        m_context = mock(SecurityContext.class);
        SecurityContextHolder.setContext(m_context);
        when(m_context.getAuthentication()).thenReturn(authentication);
    }

	@Test
	public void testBuildURL() throws UnsupportedEncodingException
	{
		when(mockConfig.getBingServicesPublicApiKey()).thenReturn(apiKey);
        when(mockAuthorizationUtils.isAnonymousUser()).thenReturn(true);
		BingAddressService bas = new BingAddressService(mockConfig, mockAuthorizationUtils);
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setCulture("fr");
        searchParameters.setSearchString("This is my search string");
        searchParameters.setMaxRecords(9);
     	String req = bas.buildURL (searchParameters);
		Assert.assertEquals("https://dev.virtualearth.net/REST/v1/Locations?key=ThisIsMyMapKey&maxResults=9&culture=fr&q=This+is+my+search+string", req);
	}

	@Test
	public void testParseResult() {
		// contains 4 unsorted addresses
		String jsonString = "{\"authenticationResultCode\":\"ValidCredentials\",\"brandLogoUri\":\"https:\\/\\/dev.virtualearth.net\\/Branding\\/logo_powered_by.png\",\"copyright\":\"Copyright © 2011 Microsoft and its suppliers. All rights reserved. This API cannot be accessed and the content and any results may not be used, reproduced or transmitted in any manner without express written permission from Microsoft Corporation.\",\"resourceSets\":[{\"estimatedTotal\":1,\"resources\":[{\"__type\":\"Location:http:\\/\\/schemas.microsoft.com\\/search\\/local\\/ws\\/rest\\/v1\",\"bbox\":[42.679068575727868,-73.709746493434636,42.686794010869221,-73.695735192385428],\"name\":\"My Address Unknown\",\"point\":{\"type\":\"Point\",\"coordinates\":[42.682931293298545,-73.702740842910032]},\"address\":{\"addressLine\":\"1 Global View\",\"adminDistrict\":\"NY\",\"countryRegion\":\"United States\",\"formattedAddress\":\"My Address Unknown\",\"locality\":\"Troy\",\"postalCode\":\"12180\"},\"confidence\":\"Unknown\",\"entityType\":\"Address\"},{\"__type\":\"Location:http:\\/\\/schemas.microsoft.com\\/search\\/local\\/ws\\/rest\\/v1\",\"bbox\":[42.679068575727868,-73.709746493434636,42.686794010869221,-73.695735192385428],\"name\":\"My Address Low\",\"point\":{\"type\":\"Point\",\"coordinates\":[42.682931293298545,-73.702740842910032]},\"address\":{\"addressLine\":\"1 Global View\",\"adminDistrict\":\"NY\",\"countryRegion\":\"United States\",\"formattedAddress\":\"My Address Low\",\"locality\":\"Troy\",\"postalCode\":\"12180\"},\"confidence\":\"Low\",\"entityType\":\"Address\"},{\"__type\":\"Location:http:\\/\\/schemas.microsoft.com\\/search\\/local\\/ws\\/rest\\/v1\",\"bbox\":[42.679068575727868,-73.709746493434636,42.686794010869221,-73.695735192385428],\"name\":\"My Address Medium\",\"point\":{\"type\":\"Point\",\"coordinates\":[42.682931293298545,-73.702740842910032]},\"address\":{\"addressLine\":\"1 Global View\",\"adminDistrict\":\"NY\",\"countryRegion\":\"United States\",\"formattedAddress\":\"My Address Medium\",\"locality\":\"Troy\",\"postalCode\":\"12180\"},\"confidence\":\"Medium\",\"entityType\":\"Address\"},{\"__type\":\"Location:http:\\/\\/schemas.microsoft.com\\/search\\/local\\/ws\\/rest\\/v1\",\"bbox\":[42.679068575727868,-73.709746493434636,42.686794010869221,-73.695735192385428],\"name\":\"My Address High\",\"point\":{\"type\":\"Point\",\"coordinates\":[42.682931293298545,-73.702740842910032]},\"address\":{\"addressLine\":\"1 Global View\",\"adminDistrict\":\"NY\",\"countryRegion\":\"United States\",\"formattedAddress\":\"My Address High\",\"locality\":\"Troy\",\"postalCode\":\"12180\"},\"confidence\":\"High\",\"entityType\":\"Address\"}]}],\"statusCode\":200,\"statusDescription\":\"OK\",\"traceId\":\"78965a93aa4846f0aeae9367da70e1b0|EWRM001657|02.00.115.3100|EWRMSNVM001740, EWRMSNVM001734, EWRMSNVM001744\"}";

		List<Address> addressList = BingAddressService.parseResult(jsonString);
		Assert.assertEquals(4, addressList.size());

		Assert.assertEquals("My Address High", addressList.get(0).getAddress());
		Assert.assertEquals("My Address Medium", addressList.get(1).getAddress());
		Assert.assertEquals("My Address Low", addressList.get(2).getAddress());
		Assert.assertEquals("My Address Unknown", addressList.get(3).getAddress());
	}

	@Test
	public void testParseAddress() {
		String jsonString = "{\"__type\":\"Location:http:\\/\\/schemas.microsoft.com\\/search\\/local\\/ws\\/rest\\/v1\",\"bbox\":[42.678407282429326,-73.710954575967449,42.686132717570679,-73.69694342403254],\"name\":\"1 Global Vw, Troy, NY 12180-8371\",\"point\":{\"type\":\"Point\",\"coordinates\":[42.68227,-73.703949]},\"address\":{\"addressLine\":\"1 Global Vw\",\"adminDistrict\":\"NY\",\"adminDistrict2\":\"Rensselaer Co.\",\"countryRegion\":\"United States\",\"formattedAddress\":\"1 Global Vw, Troy, NY 12180-8371\",\"locality\":\"Troy\",\"postalCode\":\"12180-8371\"},\"confidence\":\"High\",\"entityType\":\"Address\"}";

		Address address = BingAddressService.parseAddress(JSONObject.fromObject(jsonString));
		Assert.assertEquals("1 Global Vw, Troy, NY 12180-8371", address.getAddress());
		Assert.assertEquals(null, address.getId());
		Assert.assertEquals("epsg:4326", address.getSrs());
		Assert.assertEquals(-73.703949, address.getX(), 0);
		Assert.assertEquals(42.68227, address.getY(), 0);
	}

	@Test
	public void testTrim() throws IOException
	{
		when(mockConfig.getBingServicesPublicApiKey()).thenReturn("ArhuygLSRCEDbOHuN65xNbndYZTwzD9P9tjYXvsVNxF3gZxF0lUU3lv5hXAS2H1W");

		// contains 4 unsorted addresses
		String jsonString = "{\"authenticationResultCode\":\"ValidCredentials\",\"brandLogoUri\":\"https:\\/\\/dev.virtualearth.net\\/Branding\\/logo_powered_by.png\",\"copyright\":\"Copyright © 2011 Microsoft and its suppliers. All rights reserved. This API cannot be accessed and the content and any results may not be used, reproduced or transmitted in any manner without express written permission from Microsoft Corporation.\",\"resourceSets\":[{\"estimatedTotal\":1,\"resources\":[{\"__type\":\"Location:http:\\/\\/schemas.microsoft.com\\/search\\/local\\/ws\\/rest\\/v1\",\"bbox\":[42.679068575727868,-73.709746493434636,42.686794010869221,-73.695735192385428],\"name\":\"My Address Unknown\",\"point\":{\"type\":\"Point\",\"coordinates\":[42.682931293298545,-73.702740842910032]},\"address\":{\"addressLine\":\"1 Global View\",\"adminDistrict\":\"NY\",\"countryRegion\":\"United States\",\"formattedAddress\":\"My Address Unknown\",\"locality\":\"Troy\",\"postalCode\":\"12180\"},\"confidence\":\"Unknown\",\"entityType\":\"Address\"},{\"__type\":\"Location:http:\\/\\/schemas.microsoft.com\\/search\\/local\\/ws\\/rest\\/v1\",\"bbox\":[42.679068575727868,-73.709746493434636,42.686794010869221,-73.695735192385428],\"name\":\"My Address Low\",\"point\":{\"type\":\"Point\",\"coordinates\":[42.682931293298545,-73.702740842910032]},\"address\":{\"addressLine\":\"1 Global View\",\"adminDistrict\":\"NY\",\"countryRegion\":\"United States\",\"formattedAddress\":\"My Address Low\",\"locality\":\"Troy\",\"postalCode\":\"12180\"},\"confidence\":\"Low\",\"entityType\":\"Address\"},{\"__type\":\"Location:http:\\/\\/schemas.microsoft.com\\/search\\/local\\/ws\\/rest\\/v1\",\"bbox\":[42.679068575727868,-73.709746493434636,42.686794010869221,-73.695735192385428],\"name\":\"My Address Medium\",\"point\":{\"type\":\"Point\",\"coordinates\":[42.682931293298545,-73.702740842910032]},\"address\":{\"addressLine\":\"1 Global View\",\"adminDistrict\":\"NY\",\"countryRegion\":\"United States\",\"formattedAddress\":\"My Address Medium\",\"locality\":\"Troy\",\"postalCode\":\"12180\"},\"confidence\":\"Medium\",\"entityType\":\"Address\"},{\"__type\":\"Location:http:\\/\\/schemas.microsoft.com\\/search\\/local\\/ws\\/rest\\/v1\",\"bbox\":[42.679068575727868,-73.709746493434636,42.686794010869221,-73.695735192385428],\"name\":\"My Address High\",\"point\":{\"type\":\"Point\",\"coordinates\":[42.682931293298545,-73.702740842910032]},\"address\":{\"addressLine\":\"1 Global View\",\"adminDistrict\":\"NY\",\"countryRegion\":\"United States\",\"formattedAddress\":\"My Address High\",\"locality\":\"Troy\",\"postalCode\":\"12180\"},\"confidence\":\"High\",\"entityType\":\"Address\"}]}],\"statusCode\":200,\"statusDescription\":\"OK\",\"traceId\":\"78965a93aa4846f0aeae9367da70e1b0|EWRM001657|02.00.115.3100|EWRMSNVM001740, EWRMSNVM001734, EWRMSNVM001744\"}";
		SearchParameters params = new SearchParameters();
		params.setSearchString("1 Global Vw 12180");
        params.setCulture("ja");
		params.setMaxRecords(2);

		BingAddressService bas = new BingAddressService(mockConfig, mockAuthorizationUtils);

		BingAddressService spy = spy(bas);
        URL url = new URL(bas.buildURL(params));
        doReturn(jsonString).when(spy).makeCallout(url);

		List<Address> addressList = spy.search(params);
		Assert.assertEquals(2, addressList.size());
		Assert.assertEquals("My Address High", addressList.get(0).getAddress());
		Assert.assertEquals("My Address Medium", addressList.get(1).getAddress());
	}
	
	private Authentication createAnonymousToken()
    {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new GrantedAuthorityImpl("ROLE_ANONYMOUS"));
        return  new AnonymousAuthenticationToken("stratus", "Guest", authorities);
}
}
