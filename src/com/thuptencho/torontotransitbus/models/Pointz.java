package com.thuptencho.torontotransitbus.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Pointz implements Parcelable{
	public static final String KEY_ID = "_id", KEY_LAT = "lat", KEY_LON = "lon", KEY_PATHS__ID = "paths__id";
	public  String mLat = "", mLon = "";
	public int mPath__Id=-1;

	public Pointz(String lat, String lon, int path__Id) {
		super();
		this.mLat = lat;
		this.mLon = lon;
		this.mPath__Id = path__Id;
	}

	public Pointz() {
		super();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mLat);
		dest.writeString(mLon);
		dest.writeInt(mPath__Id);
		
	}
	
	public static Parcelable.Creator<Pointz> CREATOR = new Creator<Pointz>() {
		
		@Override
		public Pointz[] newArray(int size) {
			return new Pointz[size];
		}
		
		@Override
		public Pointz createFromParcel(Parcel source) {
			Pointz po = new Pointz();
			po.mLat = source.readString();
			po.mLat = source.readString();
			po.mPath__Id = source.readInt();
			return po;
		}
	};

	
}
