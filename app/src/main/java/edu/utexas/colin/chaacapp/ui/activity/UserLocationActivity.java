package edu.utexas.colin.chaacapp.ui.activity;


import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.utexas.colin.chaacapp.R;
import edu.utexas.colin.chaacapp.ui.BaseActivity;

public class UserLocationActivity extends BaseActivity
        implements OnMapLongClickListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng currentLocation;
    private Marker mMarker;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_user_location;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        currentLocation = new LatLng(bundle.getDouble("latitude"), bundle.getDouble("longitude"));
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14.0f));
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        currentLocation = latLng;
        mMarker.setPosition(latLng);
        Intent intent = new Intent();
        intent.putExtra("latitude", currentLocation.latitude);
        intent.putExtra("longitude", currentLocation.longitude);
        if (getParent() == null) {
            setResult(RESULT_OK, intent);
        } else {
            getParent().setResult(RESULT_OK, intent);
        }
    }
}
