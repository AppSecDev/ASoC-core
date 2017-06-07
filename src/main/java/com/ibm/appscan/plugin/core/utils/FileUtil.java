/*
 * (c) Copyright IBM Corp. 2016. 
 * (c) Copyright HCL Technologies Ltd. 2017. 
*/

package com.ibm.appscan.plugin.core.utils;

import java.util.regex.Pattern;

public class FileUtil {

	private static String INVALID_CHAR_REGEX = "^[()._ -]+$"; //$NON-NLS-1$
	
	 /**
	 * Return a name with invalid characters removed.
	 * 
	 * @param filename The filename whose invalid characters are to be removed.
	 * @return The filename whose invalid characters are removed.
	 */
	public static String getValidFilename(String filename) {
		StringBuilder builder = new StringBuilder();
		for (int i=0;i<filename.length();i++) {
			char c = filename.charAt(i);
			if (Character.isSpaceChar(c) 
					|| Character.isLetterOrDigit(c) 
					|| Pattern.matches(INVALID_CHAR_REGEX, c + "")) { //$NON-NLS-1$
				builder.append(c);
			}
		}
		return builder.toString();
	}
}
