/**
 * © Copyright IBM Corporation 2016.
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.ibm.appscan.plugin.core.scan;

import com.ibm.appscan.plugin.core.error.InvalidTargetException;
import com.ibm.appscan.plugin.core.error.ScannerException;
import com.ibm.appscan.plugin.core.results.IResultsProvider;

public interface IScan {

	/**
	 * Runs a scan.
	 * 
	 * @throws ScannerException if a fatal error occurs in the scan.
	 * @throws InvalidTargetException if the target is invalid.
	 */
	public void run() throws ScannerException, InvalidTargetException;
	
	/**
	 * Gets the id of the scan.
	 * 
	 * @return The scan Id.
	 */
	public String getScanId();
	
	/**
	 * The type of scan.
	 * 
	 * @return A String identifying the type of scan.
	 */
	public String getType();
	
	/**
	 * Gets a provider for accessing the scan results.
	 * 
	 * @return The {@link IResultsProvider}.
	 */
	public IResultsProvider getResultsProvider();
	
	/**
	 * Gets the name of the scan.
	 * 
	 * @return The name of the scan.
	 */
	public String getName();
}