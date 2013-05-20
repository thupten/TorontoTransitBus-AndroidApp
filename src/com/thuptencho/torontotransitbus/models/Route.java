package com.thuptencho.torontotransitbus.models;

import java.util.ArrayList;
import java.util.List;


public class Route {
	public static final String KEY_ID = "_id";
	public static final String KEY_TAG = "tag";
	public static final String KEY_TITLE = "title";
	public static final String KEY_SHORT_TITLE = "shortTitle";
	public static final String KEY_COLOR = "color";
	public static final String KEY_OPPOSITE_COLOR = "oppositeColor";
	public static final String KEY_LAT_MIN = "latMin";
	public static final String KEY_LAT_MAX = "latMax";
	public static final String KEY_LON_MIN = "lonMin";
	public static final String KEY_LON_MAX = "lonMax";
	public static final String KEY_AGENCY__TAG = "agencies__tag";

	private String tag = "", title = "", color = "", oppositeColor = "",
			latMin = "", latMax = "", lonMin = "", lonMax = "", shortTitle="";
	private int _id;

	private List<Direction> directions = new ArrayList<Direction>();
	private List<Stop> stops = new ArrayList<Stop>();
	private List<Pathz> paths = new ArrayList<Pathz>();
	private String agencyTag = "ttc";

	public Route() {
	}

	public Route(String title, List<Direction> directions) {
		this.title = title;
		this.directions = directions;
	}

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

	public List<Direction> getDirections() {
		return directions;
	}

	public void setDirections(List<Direction> directions) {
		this.directions = directions;
	}

	public List<Pathz> getPaths() {
		return paths;
	}

	public void setPaths(List<Pathz> paths) {
		this.paths = paths;
	}

	public List<Stop> getStops() {
		return stops;
	}

	public void setStops(List<Stop> stops) {
		this.stops = stops;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getOppositeColor() {
		return oppositeColor;
	}

	public void setOppositeColor(String oppositeColor) {
		this.oppositeColor = oppositeColor;
	}

	public String getLatMin() {
		return latMin;
	}

	public void setLatMin(String latMin) {
		this.latMin = latMin;
	}

	public String getLonMin() {
		return lonMin;
	}

	public void setLonMin(String lonMin) {
		this.lonMin = lonMin;
	}

	public String getLonMax() {
		return lonMax;
	}

	public void setLonMax(String lonMax) {
		this.lonMax = lonMax;
	}

	public String getLatMax() {
		return latMax;
	}

	public void setLatMax(String latMax) {
		this.latMax = latMax;
	}

	public String getShortTitle() {
		return shortTitle;
	}

	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}

	public String getAgency__Tag() {
		return agencyTag;
	}
	
	public String setAgency__Tag() {
		return agencyTag;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

}
