package com.ibm.appscan.plugin.core.scan;

public interface ITarget {

	/**
	 * Retrieves the target of a scan as a String. Depending upon the type of scanner, this may be a url or a file.
	 * @return
	 */
	String getTarget();
}
