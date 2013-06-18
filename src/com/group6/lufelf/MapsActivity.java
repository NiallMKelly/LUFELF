package com.group6.lufelf;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * This class handles all information relating to the Maps and the markers.
 * 
 * @author Niall Kelly
 * @version 1.0.0
 * 
 */

public class MapsActivity extends FragmentActivity implements
		OnMarkerClickListener, OnMarkerDragListener, OnClickListener {

	// XML Parse Constants
	static final String RSP = "rsp";
	static final String STATUS = "status";
	static final String CODE = "code";
	static final String MESSAGE = "message";
	static final String IS_NEW = "is_new";

	private GoogleMap map;

	String xml = new String();

	int state = 0;

	String add_place_lat = new String();
	String add_place_lon = new String();

	double startLat;
	double startLon;
	double destLat;
	double destLon;

	Button confirm_button;
	Button cancel_button;

	private Marker eventMarker;
	private Marker friendMarker;
	private Marker placeMarker;

	ArrayList<String> pointsLists = new ArrayList<String>();

	List<LatLng> test;

	AlertDialog alertDialog;

	LatLng MyPos;

	/**
	 * Method which is called upon the creation of the activity.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);

		SharedPreferences preferences = getSharedPreferences("details",
				MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();

		GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

		map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		map.setMyLocationEnabled(true);
		map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		map.setIndoorEnabled(true);
		//getMyLocation();

		// If Marker dropped
		// dropMarker();
		if (preferences.getString("markerAvailable", "").equals("1")) {
			dropMarker();
		}

		// If friend marker
		if (preferences.getString("friendMarkerAvailable", "").equals("1")) {
			System.out.println("STRING!!!!"
					+ preferences.getString("friendMarkerAvailable", ""));
			dropFriendMarker();
		} else {
			System.out.println("NO FRIEND MARKER");
		}

		if (preferences.getString("addPlaceTrue", "").equals("1")) {
			dropAddPlaceMarker();
			editor.putString("addPlaceTrue", "0").commit();
		}

		if (preferences.getString("eventMarkerAvailable", "").equals("1")) {
			dropEventMarker();
		}

	}

	/**
	 * Method which is called when an item with a click listener has been
	 * clicked.
	 */
	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.create_place_confirm_button) {
			state = 1;
			NetworkAvailability network = new NetworkAvailability();
			if (network.isNetworkAvailable(this)) {
				grabURL();
			} else {
				Toast.makeText(MapsActivity.this, "No Internet Connection",
						Toast.LENGTH_SHORT).show();
			}
			alertDialog.dismiss();
			Intent myIntent = new Intent(v.getContext(),
					LocationsActivity.class);
			startActivityForResult(myIntent, 0);
			System.out.println("Switching to Location Activity");
			finish();
		}

		if (v.getId() == R.id.create_place_cancel_button) {
			alertDialog.dismiss();
		}

	}

	/**
	 * Method which find the users current location. Using built in Android
	 * services.
	 */
	public void getMyLocation() {
		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		Criteria locationCritera = new Criteria();
		String providerName = locationManager.getBestProvider(locationCritera,
				false);
		Location location = locationManager.getLastKnownLocation(providerName);

		System.out.println("My Latitude: " + location.getLatitude());
		System.out.println("My Longitude: " + location.getLongitude());

		SharedPreferences preferences = getSharedPreferences("details",
				MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();

		double myLat = location.getLatitude();
		double myLon = location.getLongitude();

		startLat = myLat;
		startLon = myLon;

		MyPos = new LatLng(myLat, myLon);

		preferences.edit().remove("my_lat").commit();
		preferences.edit().remove("my_lon").commit();

		editor.putString("my_lat", Double.toString(myLat));
		editor.putString("my_lon", Double.toString(myLon));
		editor.putString("my_loc_available", "1");
		editor.commit();

		// Update my status with current location
		NetworkAvailability network = new NetworkAvailability();
		if (network.isNetworkAvailable(this)) {
			grabURL();
		} else {
			Toast.makeText(MapsActivity.this, "No Internet Connection",
					Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * Method which is called when an item in the options menu is selected.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		startActivity(new Intent(this, MainActivity.class));
		return true;
	}

	/**
	 * Method which is called when the options menu is created.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_maps, menu);
		return true;
	}

	/**
	 * Method to drop a marker at your current location.
	 */
	public void dropMarkerMyLocation() {
		BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA);

		SharedPreferences preferences = getSharedPreferences("details",
				MODE_PRIVATE);

		double latRaw = Double.parseDouble(preferences.getString("my_lat", ""));
		double lonRaw = Double.parseDouble(preferences.getString("my_lon", ""));

		LatLng location = new LatLng(latRaw, lonRaw);

		friendMarker = map.addMarker(new MarkerOptions().icon(bitmapDescriptor)
				.title("My Location").position(location));
		map.setOnMarkerClickListener(this);
	}

	/**
	 * Method to drop a marker where the location chosen is.
	 */
	public void dropMarker() {

		SharedPreferences preferences = getSharedPreferences("details",
				MODE_PRIVATE);

		double latRaw = Double.parseDouble(preferences.getString("markerLat",
				""));
		double lonRaw = Double.parseDouble(preferences.getString("markerLon",
				""));

		System.out.println("Drop Marker Lat:" + latRaw);
		System.out.println("Drop Marker Lon:" + lonRaw);

		LatLng location = new LatLng(latRaw, lonRaw);
		placeMarker = map.addMarker(new MarkerOptions().position(location)
				.title(preferences.getString("markerName", "")));
		map.setOnMarkerClickListener(this);
	}

	/**
	 * Method to drop a marker at the position of the chosen friend.
	 */
	public void dropFriendMarker() {

		BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);

		SharedPreferences preferences = getSharedPreferences("details",
				MODE_PRIVATE);

		double latRaw = Double.parseDouble(preferences.getString(
				"friendMarkerLat", ""));
		double lonRaw = Double.parseDouble(preferences.getString(
				"friendMarkerLon", ""));

		System.out.println("Drop Marker Lat:" + latRaw);
		System.out.println("Drop Marker Lon:" + lonRaw);

		LatLng location = new LatLng(latRaw, lonRaw);

		friendMarker = map.addMarker(new MarkerOptions().icon(bitmapDescriptor)
				.title(preferences.getString("friendMarkerName", ""))
				.position(location));
		map.setOnMarkerClickListener(this);

	}

	/**
	 * Method to drop a marker at the location of the event chosen by the user.
	 */
	public void dropEventMarker() {
		BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);

		SharedPreferences preferences = getSharedPreferences("details",
				MODE_PRIVATE);

		double latRaw = Double.parseDouble(preferences.getString(
				"eventMarkerLat", ""));
		double lonRaw = Double.parseDouble(preferences.getString(
				"eventMarkerLon", ""));

		System.out.println("Drop Marker Lat:" + latRaw);
		System.out.println("Drop Marker Lon:" + lonRaw);

		LatLng location = new LatLng(latRaw, lonRaw);

		eventMarker = map.addMarker(new MarkerOptions().icon(bitmapDescriptor)
				.title(preferences.getString("eventMarkerName", ""))
				.position(location));
		map.setOnMarkerClickListener(this);
	}

	/**
	 * Method to drop the marker needed to choose a location during Add
	 * Location.
	 */
	public void dropAddPlaceMarker() {
		BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);

		SharedPreferences preferences = getSharedPreferences("details",
				MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();

		LatLng location = new LatLng(54.010284, -2.785195);

		map.addMarker(new MarkerOptions().icon(bitmapDescriptor)
				.title("Drag Me").position(location).draggable(true));

		map.setOnMarkerDragListener(MapsActivity.this);

	}

	/**
	 * Method to check which marker has been clicked.
	 */
	public boolean onMarkerClick(final Marker marker) {

		if (marker.equals(placeMarker)) {
			System.out.println("Place Marker");
			LatLng location = placeMarker.getPosition();
			destLat = location.latitude;
			destLon = location.longitude;

			/*
			 * state = 2; // GRAB URL NetworkAvailability network = new
			 * NetworkAvailability(); if(network.isNetworkAvailable(this)) {
			 * grabURL(); } else { Toast.makeText(MapsActivity.this,
			 * "No Internet Connection", Toast.LENGTH_SHORT).show(); }
			 */

			placeMarker.showInfoWindow();
		}

		if (marker.equals(friendMarker)) {
			System.out.println("Friend Marker");
			friendMarker.showInfoWindow();
		}
		if (marker.equals(eventMarker)) {
			System.out.println("Event Marker");
			eventMarker.showInfoWindow();
		}

		return true;
	}

	/**
	 * Method to check which marker has been dragged.
	 */
	public void onMarkerDragEnd(Marker marker) {
		LatLng markerPos = marker.getPosition();
		double lat = markerPos.latitude;
		double lon = markerPos.longitude;
		add_place_lat = Double.toString(lat);
		add_place_lon = Double.toString(lon);
		// state = 1;

		alertDialog = new AlertDialog.Builder(this).create();
		View dialog_layout = getLayoutInflater().inflate(
				R.layout.add_place_confirm_dialog, null);
		confirm_button = (Button) dialog_layout
				.findViewById(R.id.create_place_confirm_button);
		cancel_button = (Button) dialog_layout
				.findViewById(R.id.create_place_cancel_button);
		confirm_button.setOnClickListener(this);
		cancel_button.setOnClickListener(this);
		alertDialog.setView(dialog_layout);
		alertDialog.setTitle("Confirm Location");
		alertDialog.show();

	}

	/**
	 * Method to parse the update location XML
	 * 
	 * @param xml
	 *            - XML To parse.
	 */
	public void parseXML(String xml) {

		XMLParser parser = new XMLParser();

		Document doc = parser.getDomElement(xml);

		NodeList nl = doc.getElementsByTagName(RSP);

		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);

			String status = e.getAttribute(STATUS);
			String failCode = e.getAttribute(CODE);

			String message = parser.getValue(e, MESSAGE);
			String is_new = parser.getValue(e, IS_NEW);

			System.out.println("Status: " + status);
			System.out.println("Fail Code: " + failCode);
			System.out.println("Message: " + message);
			System.out.println("Is New: " + is_new);

		}
	}

	/**
	 * Method to parse the add location XML
	 * 
	 * @param xml
	 *            - XML To parse.
	 */
	public void parseAddPlaceXML(String xml) {

		XMLParser parser = new XMLParser();

		Document doc = parser.getDomElement(xml);

		NodeList nl = doc.getElementsByTagName(RSP);

		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);

			String status = e.getAttribute(STATUS);
			String failCode = e.getAttribute(CODE);

			String message = parser.getValue(e, MESSAGE);

			System.out.println("Location Create Information");
			System.out.println("Status: " + status);
			System.out.println("Fail Code: " + failCode);
			System.out.println("Message: " + message);

			if (status.equals("ok")) {
				Toast.makeText(MapsActivity.this, "Location Added",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(MapsActivity.this, "Error Adding Location",
						Toast.LENGTH_SHORT).show();
			}

			clearCreatePlaceData();

		}
	}

	/**
	 * Method to clear the create place data when needed.
	 */
	public void clearCreatePlaceData() {
		SharedPreferences preferences = getSharedPreferences("details",
				MODE_PRIVATE);

		preferences.edit().remove("addPlaceName").commit();
		preferences.edit().remove("addPlaceAddress").commit();
	}

	public void grabURL() {
		new GrabURL().execute();
	}

	private class GrabURL extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... params) {

			ServerQueries query = new ServerQueries();

			SharedPreferences preferences = getSharedPreferences("details",
					MODE_PRIVATE);

			switch (state) {
			case 0:
				xml = query.updateUserStatus(
						preferences.getString("user_id", ""),
						preferences.getString("my_lat", ""),
						preferences.getString("my_lon", ""),
						preferences.getString("password", ""));
				break;
			case 1:
				xml = query.createPlace(
						preferences.getString("addPlaceName", ""),
						preferences.getString("addPlaceAddress", ""),
						add_place_lat, add_place_lon,
						preferences.getString("user_id", ""),
						preferences.getString("password", ""));
				break;
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			switch (state) {
			case 0:
				parseXML(xml);
				break;
			case 1:
				parseAddPlaceXML(xml);
				state = 0;
				break;
			}

		}
	}

	@Override
	public void onMarkerDrag(Marker arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMarkerDragStart(Marker marker) {
		// TODO Auto-generated method stub

	}

}
