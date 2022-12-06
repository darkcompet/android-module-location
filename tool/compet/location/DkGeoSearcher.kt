/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.location

import android.content.Context
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
	fun searchByAddress(address: String, maxResults: Int): List<DkGmapLocation> {
		return try {
			val items = geocoder.getFromLocationName(address, maxResults) ?: emptyList()
			val result = mutableListOf<DkGmapLocation>()

			for (item in items) {
				result.add(LocationConverter.address2location(item))
			}

			result
		}
		catch (e: Exception) {
			DkLogcats.error(this, e)

			emptyList()
		}
	}

	/**
	 * Search for result to `DkLocation`.
	 */
	fun searchByLatLng(lat: Double, lng: Double, maxResults: Int): List<DkGmapLocation> {
		return try {
			val items = geocoder.getFromLocation(lat, lng, maxResults) ?: emptyList()

			val result = mutableListOf<DkGmapLocation>()
			for (address in items) {
				result.add(LocationConverter.address2location(address))
			}

			result
		}
		catch (e: Exception) {
			DkLogcats.error(this, e)

			emptyList()
		}
	}
}