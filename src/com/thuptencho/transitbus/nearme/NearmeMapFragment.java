package com.thuptencho.transitbus.nearme;

import android.app.Dialog;
import android.content.IntentSender.SendIntentException;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class NearmeMapFragment extends SupportMapFragment implements GooglePlayServicesClient.ConnectionCallbacks,
				GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {
	private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 158;

	private static final int CONNECTION_ERROR_DIALOG_REQUEST = 014;

	private static final long LOCATION_INTERVAL = 7000;

	private static final long LOCATION_FASTEST_INTERVAL = 3000;

	private LocationClient mLocationClient;

	private Location mLocation;

	private LocationRequest request;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	private void setUpMap() {
		LatLng latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
		getMap().addMarker(new MarkerOptions().position(latLng).title("Marker"));
		getMap().moveCamera(CameraUpdateFactory.newLatLng(latLng));
		getMap().animateCamera(CameraUpdateFactory.zoomTo(15), 1000, null);

	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (result.hasResolution()) {
			try {
				result.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
			} catch (SendIntentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			showErrorDialog(result.getErrorCode());
		}

	}

	private void showErrorDialog(int errorCode) {
		// create and show an error dialog
		Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, getActivity(),
						CONNECTION_ERROR_DIALOG_REQUEST);
		errorDialog.show();
	}

	@Override
	public void onDisconnected() {
		// TODO ConnectionCallbacks onDisconnected method stub

	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO ConnectionCallbacks onConnected method stub
		mLocation = mLocationClient.getLastLocation();
		mLocationClient.requestLocationUpdates(request, this);
		setUpMap();

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// NearmeMapFragment onCreate method stub
		super.onCreate(savedInstanceState);
		mLocationClient = new LocationClient(getActivity(), this, this);
		request = LocationRequest.create();
		request.setInterval(LOCATION_INTERVAL);
		request.setFastestInterval(LOCATION_FASTEST_INTERVAL);
		request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	}

	@Override
	public void onStart() {
		// NearmeMapFragment onStart method stub
		super.onStart();
		mLocationClient.connect();
		
	}

	@Override
	public void onStop() {
		// NearmeMapFragment onStop method stub
		mLocationClient.removeLocationUpdates(this);
		mLocationClient.disconnect();
		super.onStop();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO NearmeMapFragment onViewCreated method stub
		super.onViewCreated(view, savedInstanceState);
		
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO LocationListener onLocationChanged method stub
		mLocation = location;
	}

}
