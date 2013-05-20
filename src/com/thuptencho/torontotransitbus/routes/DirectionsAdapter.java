package com.thuptencho.torontotransitbus.routes;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.SpinnerAdapter;

public class DirectionsAdapter extends SimpleCursorAdapter implements SpinnerAdapter {
	private Context context = null;
	private String[] data = null;

	public DirectionsAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to, int flags) {
		super(context, layout, cursor, from, to, flags);
		this.context = context;
		this.data = from;
	}

//	@Override
//	public View getDropDownView(int position, View convertView, ViewGroup parent) {
//		View view;
//		if (convertView == null) {
//			view = LayoutInflater.from(context).inflate(R.layout.route_list_view, parent, false);
//		}else {
//			view = convertView;
//		}
//		TextView tv = (TextView)view.findViewById(R.id.direction_text_view);
//		tv.setText(data[position]);
//		return view;
//	}

}
