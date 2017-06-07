/*
 * (c) Copyright IBM Corp. 2016. 
 * (c) Copyright HCL Technologies Ltd. 2017. 
*/

package com.ibm.appscan.plugin.core.scan;

import java.util.Map;

import com.ibm.appscan.plugin.core.error.AppScanException;
import com.ibm.appscan.plugin.core.logging.IProgress;
import com.ibm.appscan.plugin.core.scan.IScanServiceProvider;

public interface IScanManager {
	
	/**
	 * Performs any steps necessary to prepare for a scan.
	 * @param progress Tracks the progress of preparation.
	 * @param properties Additional properties to be used.
	 * @throws AppScanException
	 */
	void prepare(IProgress progress, Map<String, String> properties)  throws AppScanException;
	
	/**
	 * Runs analysis.
	 * @param progress Tracks the progress of analysis.
	 * @param properties Additional properties to be used in analysis.
	 * @param provider A provider of scanning services.
	 * @throws AppScanException
	 */
	void analyze(IProgress progress, Map<String, String> properties, IScanServiceProvider provider) throws AppScanException;
	
	/**
	 * Adds a target to the scan.
	 * @param target
	 */
	void addScanTarget(ITarget target);	
}
