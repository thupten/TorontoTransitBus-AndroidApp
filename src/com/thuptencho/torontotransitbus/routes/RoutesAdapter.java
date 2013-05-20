package com.thuptencho.torontotransitbus.routes;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thuptencho.torontotransitbus.R;
import com.thuptencho.torontotransitbus.models.Route;

public class RoutesAdapter extends BaseAdapter {
	private List<Route> routes = Collections.emptyList();
	private Context context;

	public RoutesAdapter(Context context, List<Route> routes) {
		this.routes = routes;
		this.context = context;
	}

	@Override
	public int getCount() {
		return routes.size();
	}

	@Override
	public Route getItem(int position) {
		return routes.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		if (convertView == null) {
			view = LayoutInflater.from(context).inflate(
					R.layout.route_list_view, parent, false);
		} else {
			view = convertView;
		}
		// set the content of the view
		TextView textView0 = (TextView) view.findViewById(R.id.route_text_view0);
		Route route = this.routes.get(position);
		textView0.setText(route.getTitle());
		return view;
	}

}
