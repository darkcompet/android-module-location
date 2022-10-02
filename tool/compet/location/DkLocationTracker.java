/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.location;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;

import tool.compet.core.DkConst;
import tool.compet.core.DkUtils;

/**
 * This is location tracker which uses `LocationManager` of framework.
 */
public class DkLocationTracker {
	public interface Listener {
		void onStatusChanged(String provider, int status, Bundle extras);

		void onProviderEnabled(String provider);

		void onProviderDisabled(String provider);

		void onLocationChanged(Location location);
	}

	private long minTimePeriodUpdate = 1000;
	private float minDistanceUpdate = 0.1f;
	private final LocationManager locationManager;
	private Location curLocation;
	private final Criteria criteria;
	private Listener listener;

	public DkLocationTracker(Context context) {
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setAltitudeRequired(true);
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}

	public Location getLocation() {
		return curLocation;
	}

	/**
	 * Note: before call this, should required 2 permissions [
	 *   DkConst.ACCESS_FINE_LOCATION,
	 *   DkConst.ACCESS_COARSE_LOCATION
	 * ]
	 */
	public Location start(Context context) {
		if (! DkUtils.checkPermission(context, DkConst.ACCESS_FINE_LOCATION, DkConst.ACCESS_COARSE_LOCATION)) {
			return null;
		}
		String bestProviderName = locationManager.getBestProvider(criteria, true);
		if (bestProviderName == null) {
			bestProviderName = LocationManager.GPS_PROVIDER;
		}
		//<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
		locationManager.requestLocationUpdates(
			bestProviderName,
			minTimePeriodUpdate,
			minDistanceUpdate,
			locationListener
		);
		curLocation = locationManager.getLastKnownLocation(bestProviderName);

		String networkProviderName = LocationManager.NETWORK_PROVIDER;
		boolean networkEnabled = locationManager.isProviderEnabled(networkProviderName);
		if (curLocation == null && networkEnabled) {
			locationManager.requestLocationUpdates(
				networkProviderName,
				minTimePeriodUpdate,
				minDistanceUpdate,
				locationListener
			);
			curLocation = locationManager.getLastKnownLocation(networkProviderName);
		}
		return curLocation;
	}

	public void stop() {
		locationManager.removeUpdates(locationListener);
		curLocation = null;
	}

	public DkLocationTracker setIntervalTimeUpdate(long millis) {
		if (millis > 0) {
			minTimePeriodUpdate = millis;
		}
		return this;
	}

	public DkLocationTracker setDistanceUpdate(float meter) {
		if (meter > 0) {
			minDistanceUpdate = meter;
		}
		return this;
	}

	private final LocationListener locationListener = new LocationListener() {
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			if (listener != null) {
				listener.onStatusChanged(provider, status, extras);
			}
		}

		@Override
		public void onProviderEnabled(@NonNull String provider) {
			if (listener != null) {
				listener.onProviderEnabled(provider);
			}
		}

		@Override
		public void onProviderDisabled(@NonNull String provider) {
			if (listener != null) {
				listener.onProviderDisabled(provider);
			}
		}

		@Override
		public void onLocationChanged(@NonNull Location location) {
			synchronized (this) {
				DkLocationTracker.this.curLocation = location;
			}
			if (listener != null) {
				listener.onLocationChanged(location);
			}
		}
	};
}
