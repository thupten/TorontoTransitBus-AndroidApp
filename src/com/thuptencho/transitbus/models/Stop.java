package com.thuptencho.transitbus.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Stop implements Parcelable{

	public static final String KEY_ID = "_id", KEY_TAG = "tag",
			KEY_TITLE = "title", KEY_LAT = "lat", KEY_LON = "lon",
			KEY_STOPID = "stopId", 
			KEY_ROUTE__TAG = "Route__tag",
			KEY_DIRECTION__TAG = "direction__tag";

	public String mTag = "", mTitle = "", mLat = "", mLon = "", mStopId = "", mDirection__tag="", mRoute__tag="";
	public int _id = -1;
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(_id);
		dest.writeString(mTag);
		dest.writeString(mTitle);
		dest.writeString(mLat);
		dest.writeString(mLon);
		dest.writeString(mStopId);
		dest.writeString(mDirection__tag);
		dest.writeString(mRoute__tag);
	}
	
	public static final Parcelable.Creator<Stop> CREATOR = new Parcelable.Creator<Stop>() {

		@Override
		public Stop createFromParcel(Parcel source) {
			Stop s =  new Stop();
			s._id = source.readInt();
			s.mTag =source.readString();
			s.mTitle=source.readString();
			s.mLat=source.readString();
			s.mLon=source.readString();
			s.mStopId=source.readString();
			s.mDirection__tag = source.readString();
			s.mRoute__tag = source.readString();
			return s;
		}

		@Override
		public Stop[] newArray(int size) {
			Stop[] stops = new Stop[size];
			return stops;
		}
	};
	
}
