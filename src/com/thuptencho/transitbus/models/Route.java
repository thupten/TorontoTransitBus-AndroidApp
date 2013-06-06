package com.thuptencho.transitbus.models;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Route implements Parcelable {
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

	public int _id = -1;
	public String mTag = "", mTitle = "", nColor = "", mOppositeColor = "", mLatMin = "", mLatMax = "", mLonMin = "",
			mLonMax = "", mShortTitle = "", mAgencyTag = "ttc";
	public List<Direction> mDirections = new ArrayList<Direction>();
	public List<Stop> mStops = new ArrayList<Stop>();
	public List<Pathz> mPaths = new ArrayList<Pathz>();

	@Override
	public String toString() {
		return "Tag:"+ mTag + " Title:"+mTitle;
		
	}
	
	public Route() {
	}

	public Route(String title, List<Direction> directions) {
		this.mTitle = title;
		this.mDirections = directions;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this._id);
		dest.writeString(this.mTag);
		dest.writeString(this.mTitle);
		dest.writeString(this.nColor);
		dest.writeString(this.mOppositeColor);
		dest.writeString(this.mLatMin);
		dest.writeString(this.mLatMax);
		dest.writeString(this.mLonMin);
		dest.writeString(this.mLonMax);
		dest.writeString(this.mShortTitle);
		dest.writeString(this.mAgencyTag);
		dest.writeTypedList(this.mDirections);
		dest.writeTypedList(this.mStops);
		dest.writeTypedList(this.mPaths);
	}
	
	

	public static final Parcelable.Creator<Route> CREATOR = new Parcelable.Creator<Route>() {

		@Override
		public Route createFromParcel(Parcel source) {
			Route r = new Route();
			r._id = source.readInt();
			r.mTag = source.readString();
			r.mTitle = source.readString();
			r.nColor = source.readString();
			r.mOppositeColor = source.readString();
			r.mLatMin = source.readString();
			r.mLatMax = source.readString();
			r.mLonMin = source.readString();
			r.mLonMax = source.readString();
			r.mShortTitle = source.readString();
			r.mAgencyTag = source.readString();
			source.readTypedList(r.mDirections, Direction.CREATOR);
			source.readTypedList(r.mStops, Stop.CREATOR);
			source.readTypedList(r.mPaths, Pathz.CREATOR);
			return r;
		}

		@Override
		public Route[] newArray(int size) {
			return new Route[size];
		}

	};
}
