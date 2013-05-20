package com.thuptencho.torontotransitbus.models;

public class Pointz {
	public static final String KEY_ID = "_id", KEY_LAT = "lat", KEY_LON = "lon", KEY_PATHS__ID = "paths__id";
	private String lat = "", lon = "";
	private int path__Id;

	public Pointz(String lat, String lon, int path__Id) {
		this.setLat(lat);
		this.setLon(lon);
		this.setPath__Id(path__Id);
	}

	public Pointz() {
		// TODO Auto-generated constructor stub
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

	public int getPath__Id() {
		return path__Id;
	}

	public void setPath__Id(int pathId) {
		this.path__Id = pathId;
	}
}
