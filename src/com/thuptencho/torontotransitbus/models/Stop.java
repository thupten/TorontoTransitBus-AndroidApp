package com.thuptencho.torontotransitbus.models;

public class Stop {

	public static final String KEY_ID = "_id", KEY_TAG = "tag",
			KEY_TITLE = "title", KEY_LAT = "lat", KEY_LON = "lon",
			KEY_STOPID = "stopId", 
			KEY_ROUTE__TAG = "Route__tag",
			KEY_DIRECTION__TAG = "direction__tag";

	private String tag = "", title = "", lat = "", lon = "", stopId = "", direction__tag="", route__tag="";

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String getStopId() {
		return stopId;
	}

	public void setStopId(String stopId) {
		this.stopId = stopId;
	}

	public String getDirection__tag() {
		return direction__tag;
	}

	public void setDirection__tag(String direction__tag) {
		this.direction__tag = direction__tag;
	}

	public String getRoute__tag() {
		return route__tag;
	}

	public void setRoute__tag(String route__tag) {
		this.route__tag = route__tag;
	}

}
