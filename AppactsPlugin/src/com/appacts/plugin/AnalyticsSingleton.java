package com.appacts.plugin;

import com.appacts.plugin.Interfaces.IAnalytics;

public final class AnalyticsSingleton {
	
	private static IAnalytics iAnalytics;
	private static boolean appIsInForeground = false;
	
	/*
	 * Get Analytics Instance
	 * @return iAnalytics new or cached instance
	 * @see Integration Guidelines SDK Document
	 */
	public static synchronized IAnalytics GetInstance() {

		if(iAnalytics == null) {
			iAnalytics = new Analytics();
		}
		
		return iAnalytics;
	}
	
	/*
	 * Get App Is In Foreground
	 * @see Integration Guidelines SDK Document
	 */
	public static synchronized boolean GetAppIsInForeground() {
		return appIsInForeground;
	}
	
	/*
	 * Set App Is In Foreground
	 * @see Integration Guidelines SDK Document
	 */
	public static synchronized void SetAppIsInForeground(boolean inForeground) {
		appIsInForeground = inForeground;
	}
	
}
