package com.jamasan.eyedropper;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.TypedValue;

public class Utils {
	public static boolean isFullVersion(Context context) {
		return context.getApplicationContext()
				.getResources()
				.getBoolean(R.bool.full_version);
	}
	
	public static boolean hasConnectivity(Context context) {
		ConnectivityManager cm = (ConnectivityManager)
				context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo network = cm.getActiveNetworkInfo();
		if (network != null && network.isConnected()) {
			return true;
		}
		return false;
	}

	public static Location getLastKnownLocation(Context context) {

		LocationManager locationManager = (LocationManager)
				context.getSystemService(Context.LOCATION_SERVICE);
		Location ret = locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		return ret;
	}
	
	public static int getDip(Context context, int pixels) {
		Resources r = context.getResources();
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				pixels, r.getDisplayMetrics());
	}
}
