/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.location

import android.content.Context
import android.location.Address
import android.location.Geocoder
import tool.compet.core.DkLogcats
import tool.compet.googlemap.DkGmapLocation

/**
 * Search address with `Geocoder` of Android framework.
 */
class DkGeoSearcher(context: Context) {
	private val geocoder = Geocoder(context)

	/**
	 * Search for result to `Address`.
	 */
	fun searchForAddress(lat: Double, lng: Double, maxResults: Int): List<Address> {
		return try {
			geocoder.getFromLocation(lat, lng, maxResults)
		}
		catch (e: Exception) {
			DkLogcats.error(this, e)
			emptyList()
		}
	}

	/**
	 * Search for result to `Address`.
	 */
	fun searchForAddress(name: String, maxResults: Int): List<Address> {
		return try {
			geocoder.getFromLocationName(name, maxResults)
		}
		catch (e: Exception) {
			DkLogcats.error(this, e)
			emptyList()
		}
	}

	/**
	 * Search for result to `DkLocation`.
	 */
	fun searchForLocation(lat: Double, lng: Double, maxResults: Int): List<DkGmapLocation> {
		val result = mutableListOf<DkGmapLocation>()
		for (address in searchForAddress(lat, lng, maxResults)) {
			result.add(DkLocations.address2myLocation(address))
		}
		return result
	}

	/**
	 * Search for result to `DkLocation`.
	 */
	fun searchForLocation(name: String, maxResults: Int): List<DkGmapLocation> {
		val res = mutableListOf<DkGmapLocation>()
		for (address in searchForAddress(name, maxResults)) {
			res.add(DkLocations.address2myLocation(address))
		}
		return res
	}

}