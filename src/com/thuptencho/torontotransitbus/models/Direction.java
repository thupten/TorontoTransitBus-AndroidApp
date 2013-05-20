package com.thuptencho.torontotransitbus.models;

import java.util.ArrayList;
import java.util.List;

public class Direction {
	public static final String KEY_ID = "_id", 
			KEY_TAG = "tag", KEY_TITLE = "title",
			KEY_NAME = "name", KEY_USEFORUI = "useForUI", KEY_ROUTE__TAG="route__tag";
	
	private String tag = "", title = "", name = "", useForUI = "", route__tag="";
	
	private List<Stop> stops = new ArrayList<Stop>();

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUseForUI() {
		return useForUI;
	}

	public void setUseForUI(String useForUI) {
		this.useForUI = useForUI;
	}

	public List<Stop> getStops() {
		return stops;
	}

	public void setStops(List<Stop> stops) {
		this.stops = stops;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getRoute__tag() {
		return route__tag;
	}

	public void setRoute__tag(String route__tag) {
		this.route__tag = route__tag;
	}

}