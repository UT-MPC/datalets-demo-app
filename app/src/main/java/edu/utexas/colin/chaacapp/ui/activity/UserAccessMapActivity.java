package edu.utexas.colin.chaacapp.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.utexas.colin.chaacapp.R;
import edu.utexas.colin.chaacapp.model.ChaacModel;
import edu.utexas.colin.chaacapp.model.datalets.Datalet;
import edu.utexas.colin.chaacapp.model.shared.GPSPoint;
import edu.utexas.colin.chaacapp.model.users.User;
import edu.utexas.colin.chaacapp.ui.BaseActivity;

public class UserAccessMapActivity extends BaseActivity
		implements OnMapReadyCallback {

	public static final String TAG = "AccessMapActivity";
	private GoogleMap mMap;
	private User mUser;
	private List<String> mAvailableDataletIDs = new ArrayList<>();

	@Override
	protected int getLayoutResId() {
		return R.layout.activity_datalet_access_map;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		String userID = intent.getStringExtra("userID");
		mUser = ChaacModel.model().getUser(userID);

		String availableDatalets = intent.getStringExtra("availableDatalets");
		if (availableDatalets != null && !availableDatalets.isEmpty()) {
			String[] users = availableDatalets.split(",");
			mAvailableDataletIDs.addAll(Arrays.asList(users));
		}

		Log.e(TAG, "Available Datalets: " + availableDatalets + ", list: " + mAvailableDataletIDs);

		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
		mMap.getUiSettings().setMapToolbarEnabled(false);

		drawUserLocation();
		drawAvailableDatalets();
	}

	private void drawUserLocation() {
		GPSPoint location = mUser.getLocation();
		LatLng point = new LatLng(location.latitude, location.longitude);
		mMap.addMarker(new MarkerOptions()
				.position(point)
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
				.title(mUser.getFullName(false)));

		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 14.0f));
	}

	private void drawAvailableDatalets() {
		for (String id : mAvailableDataletIDs) {
			Datalet datalet = ChaacModel.model().getDatalet(id);
			LatLng point = new LatLng(datalet.getLocation().latitude, datalet.getLocation().longitude);
			mMap.addMarker(new MarkerOptions()
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
					.title(datalet.getTitle())
					.position(point));
		}
	}
}
