package com.thuptencho.torontotransitbus.utilities;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.util.Log;

public class LogCursor {
	public static void d(String tag, Cursor c){
		StringBuilder sb = new StringBuilder("");
		DatabaseUtils.dumpCursor(c, sb);
		Log.d(tag, sb.toString());
	}
}
