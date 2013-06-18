package com.group6.lufelf;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/**
 * This class handles the list of locations and adding a location.
 * 
 * @author Niall Kelly
 * @version 1.0.0
 * 
 */

public class LocationsActivity extends Activity implements OnClickListener {

	// Constants for Parsing
	static final String RSP = "rsp";
	static final String STATUS = "status";
	static final String CODE = "code";
	static final String MESSAGE = "message";
	static final String PLACE = "place";
	static final String PLACE_ID = "place_id";
	static final String PLACE_NAME = "place_name";
	static final String PLACE_LAT = "place_lat";
	static final String PLACE_LON = "place_lon";
	static final String USER_ID = "user_id";
	static final String PLACE_ADDRESS = "place_adress";

	String xml = new String();

	ArrayList<String> placeIDArrayList = new ArrayList<String>();
	ArrayList<String> placeNameArrayList = new ArrayList<String>();
	ArrayList<String> placeLatArrayList = new ArrayList<String>();
	ArrayList<String> placeLonArrayList = new ArrayList<String>();
	ArrayList<String> placeUserIDArrayList = new ArrayList<String>();
	ArrayList<String> placeAddressArrayList = new ArrayList<String>();

	AlertDialog alertDialog;

	EditText place_name_text;
	EditText place_address_text;
	Button add_place_button;

	/**
	 * Method called upon the creation of the activity
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_popular_locations);

		NetworkAvailability network = new NetworkAvailability();
		if (network.isNetworkAvailable(this)) {
			grabURL();
		} else {
			Toast.makeText(LocationsActivity.this, "No Internet Connection",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Method called upon the creation of the options menu.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_popular_locations, menu);
		return true;
	}

	/**
	 * Method called when an item with a click listener has been clicked.
	 */
	public void onClick(View v) {

		SharedPreferences preferences = getSharedPreferences("details",
				MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();

		if (v.getId() == R.id.add_place_button) {
			String placeName = place_name_text.getText().toString();
			String placeAddress = place_address_text.getText().toString();
			System.out.println("Place Name: " + placeName);
			System.out.println("Place Address: " + placeAddress);

			editor.putString("addPlaceName", placeName);
			editor.putString("addPlaceAddress", placeAddress);
			editor.putString("addPlaceTrue", "1");
			editor.commit();

			alertDialog.dismiss();
			Intent myIntent = new Intent(v.getContext(), MapsActivity.class);
			startActivityForResult(myIntent, 0);
			System.out.println("Switching to Map Activity");
		}

	}

	/**
	 * Parse the locations XML
	 * 
	 * @param xml
	 *            - XML to parse
	 */
	public void parseXML(String xml) {

		XMLParser parser = new XMLParser();

		Document doc = parser.getDomElement(xml);

		NodeList nl = doc.getElementsByTagName(PLACE);

		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);

			String place_id = parser.getValue(e, PLACE_ID);
			String place_name = parser.getValue(e, PLACE_NAME);
			String place_lat = parser.getValue(e, PLACE_LAT);
			String place_lon = parser.getValue(e, PLACE_LON);
			String user_id = parser.getValue(e, USER_ID);
			String place_address = parser.getValue(e, PLACE_ADDRESS);

			placeIDArrayList.add(place_id);
			placeNameArrayList.add(place_name);
			placeLatArrayList.add(place_lat);
			placeLonArrayList.add(place_lon);
			placeUserIDArrayList.add(user_id);
			placeAddressArrayList.add(place_address);

		}
	}

	/**
	 * Method called when an item in the options menu has been clicked.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.new_place) {
			alertDialog = new AlertDialog.Builder(this).create();
			View dialog_layout = getLayoutInflater().inflate(
					R.layout.add_place_dialog, null);
			add_place_button = (Button) dialog_layout
					.findViewById(R.id.add_place_button);
			place_name_text = (EditText) dialog_layout
					.findViewById(R.id.place_name_text);
			place_address_text = (EditText) dialog_layout
					.findViewById(R.id.place_address_text);
			add_place_button.setOnClickListener(LocationsActivity.this);
			alertDialog.setView(dialog_layout);
			alertDialog.setTitle("Add Location");
			alertDialog.show();
		}

		return true;
	}

	/**
	 * Method to populate the list view with a list of locations.
	 */
	public void populateList() {

		// Find the ListView Resource
		ListView locationsListView = (ListView) findViewById(R.id.locations_view_list);

		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
				R.layout.simplerow, placeNameArrayList);

		locationsListView.setAdapter(listAdapter);

		locationsListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView parent, View v,
							int position, long id) {

						MapsActivity map = new MapsActivity();
						System.out.println("Lat: "
								+ placeLatArrayList.get(position));
						System.out.println("Lon: "
								+ placeLonArrayList.get(position));
						System.out.println("Name: "
								+ placeNameArrayList.get(position));

						SharedPreferences preferences = getSharedPreferences(
								"details", MODE_PRIVATE);
						SharedPreferences.Editor editor = preferences.edit();

						editor.putString("markerLat",
								placeLatArrayList.get(position));
						editor.putString("markerLon",
								placeLonArrayList.get(position));
						editor.putString("markerName",
								placeNameArrayList.get(position));
						editor.putString("markerAvailable", "1");
						editor.commit();

						Intent myIntent = new Intent(v.getContext(),
								MapsActivity.class);
						startActivityForResult(myIntent, 0);
						System.out.println("Switching to Map Activity");

					}
				});

	}

	private void printPlaces() {
		for (int i = 0; i < placeIDArrayList.size(); i++) {
			System.out.println("=== Place ===");
			System.out.println("Place ID:" + placeIDArrayList.get(i));
			System.out.println("Place Name:" + placeNameArrayList.get(i));
			System.out.println("Place Lat:" + placeLatArrayList.get(i));
			System.out.println("Place Lon:" + placeLonArrayList.get(i));
			System.out.println("User ID:" + placeUserIDArrayList.get(i));
			System.out.println("Place Address:" + placeAddressArrayList.get(i));

		}
	}

	public void grabURL() {
		new GrabURL().execute();
	}

	private class GrabURL extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			Toast.makeText(LocationsActivity.this, "Loading Locations",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			ServerQueries query = new ServerQueries();

			xml = query.queryPlaceList();

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			parseXML(xml);
			printPlaces();
			populateList();

		}

	}

}
