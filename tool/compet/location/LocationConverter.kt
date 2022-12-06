/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.location

import android.location.Address
import org.json.JSONArray
import org.json.JSONObject
import tool.compet.core.DkLogcats
import tool.compet.googlemap.DkGmapLocation

object LocationConverter {
	@JvmStatic
	fun address2location(address: Address): DkGmapLocation {
		return DkGmapLocation().apply {
			this.setLatLng(address.latitude, address.longitude)
			this.address = if (address.maxAddressLineIndex >= 0) address.getAddressLine(0) else ""
		}
	}

	fun geocode2location(jsonGeo: String): List<DkGmapLocation> {
		val result = mutableListOf<DkGmapLocation>()

		try {
			val jsonObject = JSONObject(jsonGeo)
			val jsonArray = jsonObject["results"] as JSONArray

			for (i in 0 until jsonArray.length()) {
				val jsonObj = jsonArray.getJSONObject(i)
				val lat = jsonObj.getJSONObject("geometry").getJSONObject("location").getDouble("lat")
				val lon = jsonObj.getJSONObject("geometry").getJSONObject("location").getDouble("lng")

				result.add(DkGmapLocation().apply {
					this.setLatLng(lat, lon)
					this.address = jsonObj.getString("formatted_address")
				})
			}
		}
		catch (e: Exception) {
			DkLogcats.error(LocationConverter::class.java, e)
		}

		return result
	}

	fun place2location(jsonPlace: String): List<DkGmapLocation> {
		val result = mutableListOf<DkGmapLocation>()

		try {
			val jsonObject = JSONObject(jsonPlace)
			val jsonArray = jsonObject["results"] as JSONArray

			for (i in 0 until jsonArray.length()) {
				val jsonObj = jsonArray.getJSONObject(i)
				val lat = jsonObj.getJSONObject("geometry").getJSONObject("location").getDouble("latitude")
				val lng = jsonObj.getJSONObject("geometry").getJSONObject("location").getDouble("longitude")

				result.add(DkGmapLocation(lat, lng).apply {
					this.address = jsonObj.getString("name")
				})
			}
		}
		catch (e: Exception) {
			DkLogcats.error(LocationConverter::class.java, e)
		}

		return result
	}
}