/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.location

import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.annotation.RequiresPermission

/**
 * This is location tracker which uses `LocationManager` of framework.
 */
class DkLocationTracker(context: Context) {
	interface Listener {
		fun onStatusChanged(provider: String?, status: Int, extras: Bundle?)
		fun onProviderEnabled(provider: String?)
		fun onProviderDisabled(provider: String?)
		fun onLocationChanged(location: Location?)
	}

	private var minTimePeriodUpdate: Long = 1000
	private var minDistanceUpdate = 0.1f
	private val locationManager: LocationManager
	private var location: Location? = null
	private val criteria: Criteria
	private var listener: Listener? = null

	fun setListener(listener: Listener?) {
		this.listener = listener
	}

	/**
	 * Note: before call this, should required 2 permissions [
	 * DkConst.ACCESS_FINE_LOCATION,
	 * DkConst.ACCESS_COARSE_LOCATION
	 * ]
	 */
	@RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
	fun start(context: Context?): Location? {
		var bestProviderName = locationManager.getBestProvider(criteria, true)
		if (bestProviderName == null) {
			bestProviderName = LocationManager.GPS_PROVIDER
		}
		locationManager.requestLocationUpdates(
			bestProviderName,
			minTimePeriodUpdate,
			minDistanceUpdate,
			locationListener
		)
		location = locationManager.getLastKnownLocation(bestProviderName)
		val networkProviderName = LocationManager.NETWORK_PROVIDER
		val networkEnabled = locationManager.isProviderEnabled(networkProviderName)
		if (location == null && networkEnabled) {
			locationManager.requestLocationUpdates(
				networkProviderName,
				minTimePeriodUpdate,
				minDistanceUpdate,
				locationListener
			)
			location = locationManager.getLastKnownLocation(networkProviderName)
		}
		return location
	}

	@RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
	fun stop() {
		locationManager.removeUpdates(locationListener)
		location = null
	}

	fun setIntervalTimeUpdate(millis: Long): DkLocationTracker {
		if (millis > 0) {
			minTimePeriodUpdate = millis
		}
		return this
	}

	fun setDistanceUpdate(meter: Float): DkLocationTracker {
		if (meter > 0) {
			minDistanceUpdate = meter
		}
		return this
	}

	private val locationListener: LocationListener = object : LocationListener {
		@Deprecated("Deprecated in Java")
		override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
			if (listener != null) {
				listener!!.onStatusChanged(provider, status, extras)
			}
		}

		override fun onProviderEnabled(provider: String) {
			if (listener != null) {
				listener!!.onProviderEnabled(provider)
			}
		}

		override fun onProviderDisabled(provider: String) {
			if (listener != null) {
				listener!!.onProviderDisabled(provider)
			}
		}

		override fun onLocationChanged(location: Location) {
			synchronized(this) { this@DkLocationTracker.location = location }
			if (listener != null) {
				listener!!.onLocationChanged(location)
			}
		}
	}

	init {
		locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
		criteria = Criteria()
		criteria.accuracy = Criteria.ACCURACY_COARSE
		criteria.isAltitudeRequired = true
	}
}