package com.thuptencho.transitbus.routes;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CustomSimpleCursorAdapter extends SimpleCursorAdapter  {
	public CustomSimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
	}
	

	@Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v = null;
        if (position == 0) {
            TextView tv = new TextView(parent.getContext());
            tv.setVisibility(View.GONE);
            v = tv;
        } else {
            v = super.getDropDownView(position, null, parent);
        }
        return v; 
    }
}
