package com.thuptencho.transitbus.models;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Direction implements Parcelable {
	public static final String KEY_ID = "_id", KEY_TAG = "tag", KEY_TITLE = "title", KEY_NAME = "name",
			KEY_USEFORUI = "useForUI", KEY_ROUTE__TAG = "route__tag";

	public String mTag = "", mTitle = "", mName = "", mUseForUI = "", mRoute__tag = "";

	public List<Stop> mStops = new ArrayList<Stop>();

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mTag);
		dest.writeString(mTitle);
		dest.writeString(mName);
		dest.writeString(mUseForUI);
		dest.writeString(mRoute__tag);
		dest.writeTypedList(mStops);
	}

	public static final Parcelable.Creator<Direction> CREATOR = new Creator<Direction>() {

		@Override
		public Direction[] newArray(int size) {
			return new Direction[size];
		}

		@Override
		public Direction createFromParcel(Parcel source) {
			Direction d = new Direction();
			d.mTag = source.readString();
			d.mTitle = source.readString();
			d.mName = source.readString();
			d.mUseForUI = source.readString();
			d.mRoute__tag = source.readString();
			source.readTypedList(d.mStops, Stop.CREATOR);
			return d;
		}
	};

}