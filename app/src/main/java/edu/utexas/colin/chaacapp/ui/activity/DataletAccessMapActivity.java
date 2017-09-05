package edu.utexas.colin.chaacapp.ui.activity;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.utexas.colin.chaacapp.R;
import edu.utexas.colin.chaacapp.model.ChaacModel;
import edu.utexas.colin.chaacapp.model.datalets.Datalet;
import edu.utexas.colin.chaacapp.model.datalets.conditions.Location;
import edu.utexas.colin.chaacapp.model.shared.GPSPoint;
import edu.utexas.colin.chaacapp.model.users.User;
import edu.utexas.colin.chaacapp.ui.BaseActivity;

import static edu.utexas.colin.chaacapp.ui.activity.DataletInfoActivity.DATALET_ID_KEY;

public class DataletAccessMapActivity extends BaseActivity
		implements OnMapReadyCallback {

	public static final String TAG = "AccessMapActivity";
	private GoogleMap mMap;
	private Datalet mDatalet;
	private List<String> mAccessUserIDs = new ArrayList<>();

	@Override
	protected int getLayoutResId() {
		return R.layout.activity_datalet_access_map;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		String dataletID = intent.getStringExtra(DATALET_ID_KEY);
		mDatalet = ChaacModel.model().getDatalet(dataletID);

		String accessUsers = intent.getStringExtra("accessUsers");
		if (accessUsers != null && !accessUsers.isEmpty()) {
			String[] users = accessUsers.split(",");
			mAccessUserIDs.addAll(Arrays.asList(users));
		}

		Log.e(TAG, "Access Users: " + accessUsers + ", list: " + mAccessUserIDs);

		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
		mMap.getUiSettings().setMapToolbarEnabled(false);

		drawDataletLocation();
		drawUsersWithAccess();
		drawUsersWithoutAccess();
	}

	private void drawDataletLocation() {
		GPSPoint location = mDatalet.getLocation();
		LatLng point = new LatLng(location.latitude, location.longitude);
		mMap.addMarker(new MarkerOptions()
				.position(point)
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
				.title(mDatalet.getTitle()));

		Location availability = mDatalet.checkHasLocationAccess();
		if (availability != null) {
			String operator = availability.getOperator();
			String operand = availability.getOperand();
			if (">".equals(operator)) {
				mMap.addCircle(new CircleOptions()
						.center(point)
						.radius(Double.parseDouble(operand))
						.strokeColor(Color.TRANSPARENT)
						.fillColor(0x30ff0000));
			} else {
				mMap.addCircle(new CircleOptions()
						.center(point)
						.radius(Double.parseDouble(operand))
						.strokeColor(Color.TRANSPARENT)
						.fillColor(0x3000ff00));
			}
		}

		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 14.0f));
	}

	private void drawUsersWithAccess() {
		for (String id : mAccessUserIDs) {
			User user = ChaacModel.model().getUser(id);
			LatLng point = new LatLng(user.getLocation().latitude, user.getLocation().longitude);
			mMap.addMarker(new MarkerOptions()
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
					.title(user.getEmail())
					.position(point));
		}
	}

	private void drawUsersWithoutAccess() {
		List<User> users = ChaacModel.model().getUsers();
		for (User user : users) {
			if (!mAccessUserIDs.contains(user.getId())) {
				LatLng point = new LatLng(user.getLocation().latitude, user.getLocation().longitude);
				mMap.addMarker(new MarkerOptions()
						.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
						.title(user.getEmail())
						.position(point));
			}
		}
	}
}
