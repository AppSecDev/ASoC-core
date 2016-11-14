/**
 * © Copyright IBM Corporation 2016.
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.ibm.appscan.plugin.core.utils;

import java.io.File;
import java.io.IOException;

import javax.net.ssl.HttpsURLConnection;

import org.apache.wink.json4j.JSONArtifact;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.appscan.plugin.core.CoreConstants;
import com.ibm.appscan.plugin.core.http.HttpClient;
import com.ibm.appscan.plugin.core.http.HttpResponse;

/**
 * Provides scan service utilities.
 */
public class ServiceUtil implements CoreConstants {
	
	/**
	 * Gets the SAClientUtil package used for running static analysis.
	 * 
	 * @param destination The file to save the package to.
	 * @throws IOException
	 */
	public static void getSAClientUtil(File destination) throws IOException {
		String request_url = SystemUtil.getDefaultServer() + String.format(API_SACLIENT_DOWNLOAD, API_SCX, SystemUtil.getOS());
		
		HttpClient client = new HttpClient();
		HttpResponse response = client.get(request_url, null, null);
		
		if (response.getResponseCode() == HttpsURLConnection.HTTP_OK || response.getResponseCode() == HttpsURLConnection.HTTP_CREATED) {
			if(!destination.getParentFile().isDirectory())
				destination.getParentFile().mkdirs();
			
			response.getResponseBodyAsFile(destination);
		}
	}
	
	/**
	 * Gets the latest available version of the SAClientUtil package used for running static analysis.
	 * 
	 * @return The current version of the package.
	 * @throws IOException
	 */
	public static String getSAClientVersion() throws IOException {
		String request_url = SystemUtil.getDefaultServer() + String.format(API_SACLIENT_VERSION, API_SCX, SystemUtil.getOS(), "true"); //$NON-NLS-1$
		
		HttpClient client = new HttpClient();
		HttpResponse response = client.get(request_url, null, null);
		
		if (response.getResponseCode() == HttpsURLConnection.HTTP_OK || response.getResponseCode() == HttpsURLConnection.HTTP_CREATED) {
			try {
				JSONArtifact responseContent = response.getResponseBodyAsJSON();
				if (responseContent != null) {
					JSONObject object = (JSONObject) responseContent;
					return object.getString(VERSION_NUMBER);
				}
			} catch (JSONException e) {
				return "0"; //$NON-NLS-1$
			}
		}
		return null;
	}
}
