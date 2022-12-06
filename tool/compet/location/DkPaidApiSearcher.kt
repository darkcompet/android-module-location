/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.location

import org.json.JSONArray
import org.json.JSONObject
import tool.compet.core.DkLogcats
import tool.compet.core.trimWhiteSpaceDk
import tool.compet.googlemap.DkGmapLocation
import tool.compet.http.DkHttpClient
import tool.compet.http.DkHttpConst

class DkPaidApiSearcher(private val apiKey: String) {
	companion object {
		const val URL_GEOCODE = "https://maps.googleapis.com/maps/api/geocode/json"
		const val URL_PLACES = "https://maps.googleapis.com/maps/api/place/search/json?key=%s"
		const val URL_PLACES_TEXT = "https://maps.googleapis.com/maps/api/place/textsearch/json?key=%s"
		const val URL_PLACES_DETAILS = "https://maps.googleapis.com/maps/api/place/details/json?key=%s"
		const val URL_PLACES_AUTOCOMPLETE = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key=%s"
		const val URL_STATIC_MAP = "https://maps.googleapis.com/maps/api/staticmap?key=%s"
		const val URL_NEARBY = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=%s"
		const val URL_ALTITUDE = "https://maps.googleapis.com/maps/api/elevation/json"
	}

	fun searchLocationWithGeocode(address: String, radius: Long = 500): List<DkGmapLocation> {
		val result = mutableListOf<DkGmapLocation>()

		try {
			val fAddress = address.apply { trimWhiteSpaceDk().replace(" ".toRegex(), "%20") }
			val url = "${URL_GEOCODE}?key=${apiKey}&address=${fAddress}&sensor=false&radius=${radius}"

			val response = DkHttpClient().execute(url, DkHttpConst.GET).body().readAsString() ?: ""

			result.addAll(LocationConverter.geocode2location(response))
		}
		catch (e: Exception) {
			DkLogcats.error(this, e)
		}

		return result
	}

	fun searchLocationWithGeocode(lat: Double, lng: Double, radius: Long = 500): List<DkGmapLocation> {
		val result = mutableListOf<DkGmapLocation>()

		try {
			val url = "${URL_GEOCODE}?key=${apiKey}&latlng=${lat},${lng}&sensor=false&radius=${radius}"

			val response = DkHttpClient().execute(url, DkHttpConst.GET).body().readAsString() ?: ""

			result.addAll(LocationConverter.geocode2location(response))
		}
		catch (e: Exception) {
			DkLogcats.error(this, e)
		}

		return result
	}

	fun searchAltitude(_lat: Double, _lng: Double): List<DkGmapLocation> {
		val url = "${URL_ALTITUDE}?key=${apiKey}&locations=${_lat},${_lng}"

		try {
			val response = DkHttpClient()
				.putToHeader(
					"Content-Type",
					"application/x-www-form-urlencoded"
				)
				.execute(url, DkHttpConst.GET).body().readAsString() ?: ""

			val result = mutableListOf<DkGmapLocation>()
			val jsonObject = JSONObject(response)
			val array = jsonObject["results"] as JSONArray

			for (index in 0 until array.length()) {
				val jsonDataObj = array.getJSONObject(index)
				val alt = jsonDataObj.getDouble("elevation")
				val lat = jsonDataObj.getJSONObject("location").getDouble("lat")
				val lng = jsonDataObj.getJSONObject("location").getDouble("lng")
				val resolution = jsonDataObj.getDouble("resolution")

				result.add(DkGmapLocation().apply {
					this.setLatLng(lat, lng)
					this.alt = alt
					this.resolution = resolution
				})
			}

			return result
		}
		catch (e: Exception) {
			DkLogcats.error(this, e)

			return emptyList()
		}
	}
}
