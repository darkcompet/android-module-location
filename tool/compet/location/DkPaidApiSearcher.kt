/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.location

import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray
import org.json.JSONObject
import tool.compet.core.DkLogcats
import tool.compet.core.DkStrings
import tool.compet.core.trimWhiteSpaceDk
import tool.compet.googlemap.DkGmapLocation
import tool.compet.http.DkHttpClient
import java.util.*

class DkPaidApiSearcher(private val gmapKey: String) {
	fun searchLocation(
		latLng: LatLng?,
		address: String,
		useGeo: Boolean,
		usePlace: Boolean,
		useText: Boolean
	): List<DkGmapLocation> {

		val result: MutableList<DkGmapLocation> = ArrayList()
		val finalAddress = address.apply { trimWhiteSpaceDk().replace(" ".toRegex(), "%20") }
		var geoLink = ""
		var placeLink = ""
		var textLink = ""
		val keyParam = String.format(Locale.US, "key=%s&radius=500", gmapKey)

		if (useGeo) {
			var geoParam = keyParam
			if (finalAddress.isNotBlank()) {
				geoParam += String.format(Locale.US, "&address=%s&sensor=false", finalAddress)
			}
			if (latLng != null) {
				geoParam += String.format(Locale.US, "&latlng=%f,%f", latLng.latitude, latLng.longitude)
			}
			geoLink = DkPaidApiConst.URL_GEO + geoParam
		}
		if (usePlace) {
			var placeParam = keyParam
			if (finalAddress.isNotBlank()) {
				placeParam += String.format(Locale.US, "&name=%s&sensor=false", finalAddress)
			}
			if (latLng != null) {
				placeParam += String.format(Locale.US, "&location=%f,%f", latLng.latitude, latLng.longitude)
			}
			placeLink = DkPaidApiConst.URL_PLACES + placeParam
		}
		if (useText) {
			var textParam = keyParam
			if (finalAddress.isNotBlank()) {
				textParam += String.format(Locale.US, "&query=%s&sensor=false", finalAddress)
			}
			if (latLng != null) {
				textParam += String.format(Locale.US, "&location=%f,%f", latLng.latitude, latLng.longitude)
			}
			textLink = DkPaidApiConst.URL_PLACES_TEXT + textParam
		}
		if (useGeo) {
			val jsonGeo = getResponse(geoLink)
			val geoLocations = DkLocations.jsonGeo2myLocation(jsonGeo)

			result.addAll(geoLocations)
		}
		if (usePlace) {
			val jsonPlace = getResponse(placeLink)
			val placeLocations = DkLocations.jsonPlace2myLocation(jsonPlace)

			result.addAll(placeLocations)
		}
		if (useText) {
			val jsonText = getResponse(textLink)
			val textLocations = DkLocations.jsonPlace2myLocation(jsonText)

			result.addAll(textLocations)
		}

		return result
	}

	private fun getResponse(link: String): String {
		return try {
			DkHttpClient(link).execute().body().asString()!!
		} catch (e: Exception) {
			DkLogcats.error(DkLocations::class.java, e)
			""
		}
	}

	fun searchAltitude(latLng: LatLng?): Array<DkGmapLocation?>? {
		return if (latLng == null) null else searchAltitude(latLng.latitude, latLng.longitude)
	}

	fun searchAltitude(latitude: Double, longitude: Double): Array<DkGmapLocation?>? {
		val format = DkPaidApiConst.URL_ALTITUDE + "key=%s&locations=%f,%f"
		val link = DkStrings.format(format, gmapKey, latitude, longitude)
		val data: String = try {
			DkHttpClient(link)
				.addToHeader(
					"Content-Type",
					"application/x-www-form-urlencoded"
				)
				.execute().body().asString()!!
		} catch (e: Exception) {
			DkLogcats.error("DkPaidApiSearcher", e)
			""
		}

		var result: Array<DkGmapLocation?>? = null

		try {
			val jsonObject = JSONObject(data)
			val array = jsonObject["results"] as JSONArray
			val N = array.length()
			result = arrayOfNulls(N)

			for (i in 0 until N) {
				val jsonDataObj = array.getJSONObject(i)
				val alt = jsonDataObj.getDouble("elevation")
				val lat = jsonDataObj.getJSONObject("location").getDouble("lat")
				val lng = jsonDataObj.getJSONObject("location").getDouble("lng")
				val resolution = jsonDataObj.getDouble("resolution")
				result[i] = DkGmapLocation()
				result[i]!!.setLatLng(lat, lng)
				result[i]!!.alt = alt
				result[i]!!.resolution = resolution
			}
		} catch (e: Exception) {
			DkLogcats.error(DkLocations::class.java, e)
		}
		return result
	}
}
