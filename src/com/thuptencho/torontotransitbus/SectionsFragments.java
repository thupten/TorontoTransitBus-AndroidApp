package com.thuptencho.torontotransitbus;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;

import com.thuptencho.torontotransitbus.MainActivity.DummySectionFragment;
import com.thuptencho.torontotransitbus.routes.RoutesFragment;

public class SectionsFragments {
	public static final String ARG_SECTION_NUMBER = "section_number";
	public static Fragment newInstance(int num) {
		Bundle arguments = new Bundle();
		arguments.putInt(ARG_SECTION_NUMBER, num);
		switch (num) { 
		case 0:
			ListFragment lf = new RoutesFragment();
			lf.setArguments(arguments);
			return lf;
		case 1:
			DummySectionFragment dummyf = new DummySectionFragment();
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
