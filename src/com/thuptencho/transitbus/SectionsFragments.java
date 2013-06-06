package com.thuptencho.transitbus;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;

import com.google.android.gms.maps.SupportMapFragment;
import com.thuptencho.transitbus.MainActivity.DummySectionFragment;
import com.thuptencho.transitbus.nearme.NearmeMapFragment;
import com.thuptencho.transitbus.routes.RoutesListFragment;

public class SectionsFragments {
	public static final String ARG_SECTION_NUMBER = "section_number";
	public static Fragment newInstance(int num) {
		Bundle arguments = new Bundle();
		arguments.putInt(ARG_SECTION_NUMBER, num);
		switch (num) { 
		case 0:
			ListFragment lf = new RoutesListFragment();
			lf.setArguments(arguments);
			return lf;
		case 1:
			SupportMapFragment dummyf = new NearmeMapFragment();
			dummyf.setArguments(arguments);
			return dummyf;
		case 2:
		default:
			DummySectionFragment dummyff = new DummySectionFragment();
			dummyff.setArguments(arguments);
			return dummyff;
		}

	}
}
