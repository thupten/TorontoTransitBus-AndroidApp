package com.thuptencho.torontotransitbus.models;

public class Prediction {
	public String epochTime = "", seconds = "", minutes = "", isDeparture = "", affectedByLayover = "", branch = "",
			dirTag = "", vehicle = "", block = "", tripTag = "";

	public Prediction(){
		super();
	}
	@Override
	public String toString() {
		return "epochTime:" + this.epochTime + " seconds:" + this.seconds + " minutes:" + this.minutes
				+ " isDeparture:" + this.isDeparture + " affectedByLayover:" + this.affectedByLayover + " branch:"
				+ this.branch + " dirTag:" + this.dirTag + " vehicle:" + this.vehicle + " block:" + this.block
				+ " triptag:" + this.tripTag;
	}
}
