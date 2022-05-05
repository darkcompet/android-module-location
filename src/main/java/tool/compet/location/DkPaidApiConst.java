/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.location;

public interface DkPaidApiConst {
	String URL_PLACES_DETAILS = "https://maps.googleapis.com/maps/api/place/details/json?key=%s";
	String URL_NEARBY = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=%s";
	String URL_GEO = "https://maps.googleapis.com/maps/api/geocode/json?key=%s";
	String URL_PLACES = "https://maps.googleapis.com/maps/api/place/search/json?key=%s";
	String URL_PLACES_TEXT = "https://maps.googleapis.com/maps/api/place/textsearch/json?key=%s";
	String URL_PLACES_AUTOCOMPLETE = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key=%s";
	String URL_STATIC_MAP = "https://maps.googleapis.com/maps/api/staticmap?key=%s";
	String URL_ALTITUDE = "https://maps.googleapis.com/maps/api/elevation/json?key=%s";
}
