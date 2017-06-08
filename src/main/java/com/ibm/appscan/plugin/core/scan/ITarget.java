package com.ibm.appscan.plugin.core.scan;

/**
 * Represents an item to be scanned. Depending upon the type of scan, this may be a url or a file.
 */
public interface ITarget {

	String getTarget();
}
