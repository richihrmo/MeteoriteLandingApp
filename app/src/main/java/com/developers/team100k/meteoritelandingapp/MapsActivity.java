package com.developers.team100k.meteoritelandingapp;

import static android.R.attr.defaultValue;
import static android.R.attr.labelTextSize;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

  private GoogleMap mMap;
  private double longitude;
  private double latitude;
  private String name;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_maps);

    Toolbar mapsToolbar = (Toolbar) findViewById(R.id.mapstoolbar);

    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
    Intent intent = this.getIntent();
    latitude = intent.getDoubleExtra("latitude", defaultValue);
    longitude = intent.getDoubleExtra("longitude", defaultValue);
    name = intent.getStringExtra("name");

    mapsToolbar.setTitle(name);

    TextView info = (TextView) findViewById(R.id.info);
    info.setText(latitude + ", " + longitude);
  }


  /**
   * Manipulates the map once available.
   * This callback is triggered when the map is ready to be used.
   * This is where we can add markers or lines, add listeners or move the camera. In this case,
   * we just add a marker near Sydney, Australia.
   * If Google Play services is not installed on the device, the user will be prompted to install
   * it inside the SupportMapFragment. This method will only be triggered once the user has
   * installed Google Play services and returned to the app.
   */
  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;

    LatLng landingPoint = new LatLng(latitude, longitude);
    mMap.addMarker(new MarkerOptions().position(landingPoint).title("Marker"));
    mMap.moveCamera(CameraUpdateFactory.newLatLng(landingPoint));


    // Add a marker in Sydney and move the camera
//    LatLng sydney = new LatLng(-34, 151);
//    mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
  }
}
