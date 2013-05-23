package com.thuptencho.torontotransitbus.models;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;


public class Pathz implements Parcelable{
	public static final String KEY_ID = "_id";
	public static final String KEY_ROUTE__TAG="route__tag";
	public List<Pointz> mPoints = new ArrayList<Pointz>();
	public String mRoute__tag="";

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mRoute__tag);
		dest.writeTypedList(mPoints);
	}
	
	public static final Parcelable.Creator<Pathz> CREATOR = new Parcelable.Creator<Pathz>() {

		@Override
		public Pathz createFromParcel(Parcel source) {
			Pathz pa = new Pathz();
			pa.mRoute__tag = source.readString();
			source.readTypedList(pa.mPoints, Pointz.CREATOR);
			return null;
		}

		@Override
		public Pathz[] newArray(int size) {
			// TODO Auto-generated method stub
			return null;
		}
	};
}
