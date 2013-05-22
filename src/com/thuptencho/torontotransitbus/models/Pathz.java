package com.thuptencho.torontotransitbus.models;

import java.util.ArrayList;
import java.util.List;


public class Pathz {
	public static final String KEY_ID = "_id";
	public static final String KEY_ROUTE__TAG="route__tag";
	public List<Pointz> points = new ArrayList<Pointz>();
	private String route__tag;

	public List<Pointz> getPoints() {
		return points;
	}

	public void setPoints(List<Pointz> points) {
		this.points = points;
	}

	public String getRoute__tag() {
		return route__tag;
	}

	public void setRoute__tag(String route__tag) {
		this.route__tag = route__tag;
	}
}
