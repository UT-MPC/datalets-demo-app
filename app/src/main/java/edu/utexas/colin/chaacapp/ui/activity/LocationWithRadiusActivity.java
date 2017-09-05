package edu.utexas.colin.chaacapp.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import edu.utexas.colin.chaacapp.R;
import edu.utexas.colin.chaacapp.ui.BaseActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class LocationWithRadiusActivity extends BaseActivity
		implements GoogleMap.OnMapLongClickListener, OnMapReadyCallback {

	private GoogleMap mMap;
	private LatLng currentLocation;
	private int radius;
	private Marker mMarker;
	private CircleOptions mCircleOptions;
	private Circle mCircle;

	@BindView(R.id.map_edit_radius)
	EditText radiusEditText;

	@Override
	protected int getLayoutResId() {
		return R.layout.activity_location_with_radius;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		currentLocation = new LatLng(bundle.getDouble("latitude"), bundle.getDouble("longitude"));
		radius = bundle.getInt("radius", 50);
		radiusEditText.setText(Integer.toString(radius));
		radiusEditText.setOnEditorActionListener((v, actionId, event) -> {
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				if (radiusEditText.getText() == null) {
					radiusEditText.setText("100");
					radius = 100;
				} else {
					radius = Integer.parseInt(radiusEditText.getText().toString());
				}

				prepResult();
				drawCircle();
			}
			return false;
		});
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
		mMap.setOnMapLongClickListener(this);
		mMap.getUiSettings().setMapToolbarEnabled(false);

		mMarker = mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
		drawCircle();
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14.0f));
	}

	@Override
	public void onMapLongClick(LatLng latLng) {
		currentLocation = latLng;
		mMarker.setPosition(latLng);
		drawCircle();
		prepResult();
	}

	public void drawCircle() {
		if (mCircle == null) {
			mCircleOptions = new CircleOptions();
			mCircleOptions.center(currentLocation)
					.radius(radius)
					.strokeColor(Color.TRANSPARENT)
					.fillColor(0x30ff0000);
			mCircle = mMap.addCircle(mCircleOptions);
		} else {
			mCircle.setCenter(currentLocation);
			mCircle.setRadius(radius);
		}
	}

	public void prepResult() {
		Intent intent = new Intent();
		intent.putExtra("latitude", currentLocation.latitude);
		intent.putExtra("longitude", currentLocation.longitude);
		intent.putExtra("radius", Integer.toString(radius));
		if (getParent() == null) {
			setResult(RESULT_OK, intent);
		} else {
			getParent().setResult(RESULT_OK, intent);
		}
	}
}
