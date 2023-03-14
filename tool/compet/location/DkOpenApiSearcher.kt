/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.location

import org.json.JSONArray
import org.json.JSONObject
import tool.compet.core.DkLogcats
import tool.compet.core.DkStrings
import tool.compet.googlemap.DkGmapLocation
import tool.compet.http.DkHttpClient
import tool.compet.http.DkHttpConst

/**
 * Search location (address, elevation...) with open api.
 */
object DkOpenApiSearcher {
	private const val OPEN_ELEVATION = "https://api.open-elevation.com/api/v1/lookup?locations="
	private const val ELEVATION_IO = "https://elevation-api.io/api/elevation?points="
	fun searchElevation(latitude: Double, longitude: Double): List<DkGmapLocation?>? {
		val format = OPEN_ELEVATION + "%f,%f"
		val link = DkStrings.format(format, latitude, longitude)
		val data = try {
			DkHttpClient() //.addToHeader("Content-Type", "application/x-www-form-urlencoded")
				.execute(link, DkHttpConst.GET).body().readAsString()
		}
		catch (e: Exception) {
			DkLogcats.error(DkOpenApiSearcher::class.java, e)
			""
		}
		var result: MutableList<DkGmapLocation?>? = null
		try {
			val jsonObject = JSONObject(data)
			val array = jsonObject["results"] as JSONArray
			val N = array.length()
			if (N > 0) {
				result = ArrayList()
			}
			for (index in 0 until N) {
				val obj = array.getJSONObject(index)
				val lat = obj.getDouble("latitude")
				val lng = obj.getDouble("longitude")
				val alt = obj.getDouble("elevation")
				val loc = DkGmapLocation(lat, lng)
				loc.alt = alt
				result!!.add(loc)
			}
		}
		catch (e: Exception) {
			DkLogcats.error(LocationConverter::class.java, e)
		}
		return result
	}
}