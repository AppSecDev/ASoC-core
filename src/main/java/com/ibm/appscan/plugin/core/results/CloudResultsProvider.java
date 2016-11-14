/**
 * © Copyright IBM Corporation 2016.
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.ibm.appscan.plugin.core.results;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.appscan.plugin.core.CoreConstants;
import com.ibm.appscan.plugin.core.Messages;
import com.ibm.appscan.plugin.core.auth.IAuthenticationProvider;
import com.ibm.appscan.plugin.core.http.HttpClient;
import com.ibm.appscan.plugin.core.http.HttpResponse;
import com.ibm.appscan.plugin.core.logging.IProgress;
import com.ibm.appscan.plugin.core.logging.Message;
import com.ibm.appscan.plugin.core.scan.IScanServiceProvider;
import com.ibm.appscan.plugin.core.utils.SystemUtil;

public class CloudResultsProvider implements IResultsProvider, CoreConstants {

	private static String DEFAULT_REPORT_FORMAT = "html"; //$NON-NLS-1$
	
	private String m_type;
	private String m_scanId;
	private String m_status;
	private String m_reportFormat;
	private boolean m_hasResults;
	private IScanServiceProvider m_scanProvider;
	private IProgress m_progress;
	
	private int m_totalFindings;
	private int m_highFindings;
	private int m_mediumFindings;
	private int m_lowFindings;
	private int m_infoFindings;
	
	public CloudResultsProvider(String scanId, String type, IScanServiceProvider provider, IProgress progress) {
		m_type = type;
		m_scanId = scanId;
		m_hasResults = false;
		m_scanProvider = provider;
		m_progress = progress;
		m_reportFormat = DEFAULT_REPORT_FORMAT;
	}

	@Override
	public void getResultsFile(File file, String format) {
		if(format == null)
			format = m_reportFormat;
		
		if(file != null && !file.exists()) {
			try {
				getReport(m_scanId, format, file);
			} catch (IOException | JSONException e) {
				m_progress.setStatus(new Message(Message.ERROR, Messages.getMessage(ERROR_GETTING_RESULT)), e);
			}
		}
	}

	@Override
	public Collection<?> getFindings() {
		return null;
	}

	@Override
	public int getFindingsCount() {
		checkResults();
		return m_totalFindings;
	}

	@Override
	public int getHighCount() {
		checkResults();
		return m_highFindings;
	}

	@Override
	public int getMediumCount() {
		checkResults();
		return m_mediumFindings;
	}

	@Override
	public int getLowCount() {
		checkResults();
		return m_lowFindings;
	}

	@Override
	public int getInfoCount() {
		checkResults();
		return m_infoFindings;
	}

	@Override
	public String getType() {
		return m_type;
	}

	@Override
	public boolean hasResults() {
		checkResults();
		return m_hasResults;
	}
	
	@Override
	public String getStatus() {
		checkResults();
		return m_status;
	}
	
	@Override
	public String getResultsFormat() {
		return m_reportFormat;
	}
	
	/**
	 * Specifies the format to use for reports.
	 * 
	 * @param format The format of the report. 
	 */
	public void setReportFormat(String format) {
		m_reportFormat = format;
	}
	
	private void loadResults() {
		try {
			JSONObject obj = m_scanProvider.getScanDetails(m_scanId);
			obj = (JSONObject) obj.get(LATEST_EXECUTION);
			m_status = obj.getString(STATUS);
			if(m_status != null && !m_status.equalsIgnoreCase(RUNNING)) {
				m_totalFindings = obj.getInt(TOTAL_ISSUES);
				m_highFindings = obj.getInt(HIGH_ISSUES);
				m_mediumFindings = obj.getInt(MEDIUM_ISSUES);
				m_lowFindings = obj.getInt(LOW_ISSUES);
				m_infoFindings = obj.getInt(INFO_ISSUES);
				m_hasResults = true;
			}
		} catch (IOException | JSONException | NullPointerException e) {
			m_progress.setStatus(new Message(Message.ERROR, Messages.getMessage(ERROR_GETTING_DETAILS)), e);
		}
	}
	
	private void getReport(String scanId, String format, File destination) throws IOException, JSONException {
		IAuthenticationProvider authProvider = m_scanProvider.getAuthenticationProvider();
		if(authProvider.isTokenExpired()) {
			m_progress.setStatus(new Message(Message.ERROR, Messages.getMessage(ERROR_LOGIN_EXPIRED)));
			return;
		}
	
		String request_url = authProvider.getServer() + String.format(API_SCANS_REPORT, scanId, format);
		Map<String, String> request_headers = authProvider.getAuthorizationHeader(true);
		request_headers.put(CONTENT_LENGTH, "0"); //$NON-NLS-1$
	
		HttpClient client = new HttpClient();
		HttpResponse response = client.get(request_url, request_headers, null);
	
		if (response.getResponseCode() == HttpsURLConnection.HTTP_OK) {
			if (destination.isDirectory()) {
				String fileName = DEFAULT_RESULT_NAME + "_" + SystemUtil.getTimeStamp() + "." + format; //$NON-NLS-1$ //$NON-NLS-2$
				destination = new File(destination, fileName);
			}
	
			destination.getParentFile().mkdirs();
			response.getResponseBodyAsFile(destination);
		} else {
			JSONObject object = (JSONObject) response.getResponseBodyAsJSON();
			if (object.has(MESSAGE)) {
				if (response.getResponseCode() == HttpsURLConnection.HTTP_BAD_REQUEST)
					m_progress.setStatus(new Message(Message.ERROR, Messages.getMessage(ERROR_GETTING_RESULT)));
				else
					m_progress.setStatus(new Message(Message.ERROR, object.getString(MESSAGE)));
			}
		}
	}
	
	private void checkResults() {
		if(!m_hasResults)
			loadResults();
	}
}
