/**
 * © Copyright IBM Corporation 2016.
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.ibm.appscan.plugin.core.auth;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.appscan.plugin.core.CoreConstants;
import com.ibm.appscan.plugin.core.http.HttpClient;
import com.ibm.appscan.plugin.core.http.HttpResponse;

public class AuthenticationHandler implements CoreConstants {

	private IAuthenticationProvider m_authProvider;
	
	public AuthenticationHandler(IAuthenticationProvider provider) {
		m_authProvider = provider;
	}
	
	/**
	 * Authenticates a user with the service.
	 * @param username
	 * @param password
	 * @param persist True to persist the credentials.
	 * @return True if successful.
	 * @throws IOException 
	 * @throws JSONException 
	 */
	public boolean login(String username, String password, boolean persist) throws IOException, JSONException {
				
		Map<String, String> headers = new HashMap<String, String>();
		headers.put(CONTENT_TYPE, "application/x-www-form-urlencoded"); //$NON-NLS-1$
		headers.put(CHARSET, UTF8);
		
		Map<String, String> params = new HashMap<String, String>();
		params.put(USERNAME, username);
	    params.put(PASSWORD, password);
	    
		String url = m_authProvider.getServer() + API_IBM_LOGIN;

		
		HttpClient client = new HttpClient();
	    HttpResponse response = client.postForm(url, headers, params);
	    
		if(response.getResponseCode() == HttpsURLConnection.HTTP_OK || response.getResponseCode() == HttpsURLConnection.HTTP_CREATED) {
			if(persist) {
				JSONObject object = (JSONObject)response.getResponseBodyAsJSON();
				String token = object.getString(TOKEN);
				m_authProvider.saveConnection(token);
			}
			return true;
		}
		return false;
	}
	
	public boolean isTokenExpired() {
		boolean isExpired;
		String request_url = m_authProvider.getServer() + API_SCANS;
		
		Map<String, String> headers = m_authProvider.getAuthorizationHeader(false);
		headers.put("Accept", "application/json"); //$NON-NLS-1$ //$NON-NLS-2$
		headers.put(CHARSET, UTF8);
		
		HttpClient httpClient = new HttpClient();
		HttpResponse httpResponse;
		try {
			httpResponse = httpClient.get(request_url, headers, null);
			isExpired = httpResponse.getResponseCode() != HttpsURLConnection.HTTP_OK;
		} catch (IOException e) {
			isExpired = true;
		}
		return isExpired;
	}
}
