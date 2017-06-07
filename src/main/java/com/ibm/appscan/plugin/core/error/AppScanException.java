/*
 * (c) Copyright IBM Corp. 2016. 
 * (c) Copyright HCL Technologies Ltd. 2017. 
*/

package com.ibm.appscan.plugin.core.error;

public class AppScanException extends Exception {

	private static final long serialVersionUID = 1L;

	public AppScanException(String message) {
		super(message);  
	}

	public AppScanException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
