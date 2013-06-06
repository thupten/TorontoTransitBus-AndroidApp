package com.thuptencho.transitbus.utilities;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.util.Log;

public class MyLogger {
	private static int priority = Log.ASSERT;
	private static String tag = "MyLog";

	public static void log(String s) {
		Log.println(priority, tag, s);
	}

	public static void log(Cursor c) {
		StringBuilder sb = new StringBuilder("");
		DatabaseUtils.dumpCursor(c, sb);
		Log.println(priority, tag, c.getCount() + " records in following cursor");
		Log.println(priority, tag, sb.toString());
	}

	public static void log(boolean status) {
		Log.println(priority, tag, (status==true?"true":"false"));
	}

	public static void log(long selectedItemId) {
		Log.println(priority, tag, ""+selectedItemId);
		
	}


}
