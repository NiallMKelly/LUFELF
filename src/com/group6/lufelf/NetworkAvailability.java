package com.group6.lufelf;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 
 * This class checks if there is a Network Connection Available.
 * 
 * @author Niall Kelly
 * @version 1.0.0
 * 
 */

public class NetworkAvailability {

	public NetworkAvailability() {
		// EMPTY CONSTRUCTOR
	}

	/**
	 * This method checks if there is a Network available and returns the
	 * appropriate boolean.
	 * 
	 * @param context
	 *            - The context of the activity you call it in.
	 * @return True is network is available, False if a network is no available.
	 */
	boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}

}
