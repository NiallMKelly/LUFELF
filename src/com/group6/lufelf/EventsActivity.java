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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * This class consists of all information relating to the usage, creation and
 * viewing of 'Events' in the LUFELF Android Application.
 * 
 * @author Niall Kelly
 * @version 1.0.0
 * 
 */

public class EventsActivity extends Activity implements OnClickListener {

	// Parse XML Constants
	static final String RSP = "rsp";
	static final String STATUS = "status";
	static final String CODE = "code";
	static final String MESSAGE = "message";
	static final String EVENTS = "event";
	static final String EVENT_ID = "event_id";
	static final String EVENT_NAME = "event_name";
	static final String EVENT_DATE = "event_date";
	static final String LOCATION_NAME = "location_name";
	static final String LOCATION_ADDRESS = "location_address";
	static final String LOCATION = "location";
	static final String ATTENDEES = "attendees";
	static final String ATTENDEE = "attendee";
	static final String NAME = "name";
	static final String DATE_ACCEPTED = "date_accepted";
	static final String DATE = "date";

	// ArrayList for Events
	ArrayList<String> eventsIDArrayList = new ArrayList<String>();
	ArrayList<String> eventsNameArrayList = new ArrayList<String>();
	ArrayList<String> eventsDateArrayList = new ArrayList<String>();

	// ArrayLists for Event Details
	String eventDetailsNameArrayList = new String();
	String eventDetailsDateArrayList = new String();
	String eventDetailsLocationNameArrayList = new String();
	String eventDetailsLocationAddressArrayList = new String();
	String eventDetailsLocationArrayList = new String();

	// Place Names for Spinner
	ArrayList<String> placeNamesArrayList = new ArrayList<String>();
	ArrayList<String> placeIDArrayList = new ArrayList<String>();

	// Place Location Details
	ArrayList<String> placeNameArrayList2 = new ArrayList<String>();
	ArrayList<String> placeLatArrayList = new ArrayList<String>();
	ArrayList<String> placeLonArrayList = new ArrayList<String>();

	// Attendees
	ArrayList<String> eventAttendeesList = new ArrayList<String>();

	AlertDialog alertDialog;

	// Event Details
	Button locate_event;
	Button attend_event;
	TextView event_name_dialog;
	TextView event_location_name_dialog;
	TextView event_date_dialog;
	Spinner attendees_names;

	// Create Event
	EditText add_event_name;
	EditText add_event_date;
	Spinner add_event_place;
	Button add_event_button;

	// Strings
	String xml = new String();
	String event_id = new String();

	// Int
	int state;
	int placesGot = 0;
	int position_list = 0;

	// Create Event Variables
	String createEventName, createEventDate, createEventPlaceID, user_id,
			password;

	/**
	 * Method which is called upon the creation of the activity.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_events);

		SharedPreferences preferences = getSharedPreferences("details",
				MODE_PRIVATE);
		this.user_id = preferences.getString("user_id", "");
		this.password = preferences.getString("password", "");

		NetworkAvailability network = new NetworkAvailability();
		if (network.isNetworkAvailable(this)) {
			grabURL();
		} else {
			Toast.makeText(EventsActivity.this, "No Internet Connection",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Method which is called when an item with an click listener has been
	 * clicked.
	 */
	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.locate_event_button) {
			state = 5;
			NetworkAvailability network = new NetworkAvailability();
			if (network.isNetworkAvailable(this)) {
				grabURL();
			} else {
				Toast.makeText(EventsActivity.this, "No Internet Connection",
						Toast.LENGTH_SHORT).show();
			}
		}

		if (v.getId() == R.id.attend_event_button) {
			state = 4;
			event_id = eventsIDArrayList.get(position_list);
			System.out.println("EVENT ID: " + event_id);
			NetworkAvailability network = new NetworkAvailability();
			if (network.isNetworkAvailable(this)) {
				grabURL();
			} else {
				Toast.makeText(EventsActivity.this, "No Internet Connection",
						Toast.LENGTH_SHORT).show();
			}
			alertDialog.dismiss();
		}

		if (v.getId() == R.id.add_event_button) {
			createEventName = add_event_name.getText().toString();
			createEventDate = add_event_date.getText().toString();
			int id = (int) add_event_place.getSelectedItemPosition();
			createEventPlaceID = placeIDArrayList.get(id);

			System.out.println("Event Name: " + createEventName);
			System.out.println("Event Date: " + createEventDate);
			System.out.println("Event ID: " + createEventPlaceID);
			System.out.println("User ID: " + user_id);
			System.out.println("Password: " + password);

			alertDialog.dismiss();
			state = 3;
			NetworkAvailability network = new NetworkAvailability();
			if (createEventName.length() > 0 && createEventDate.length() > 0) {
				if (network.isNetworkAvailable(this)) {
					grabURL();
				} else {
					Toast.makeText(EventsActivity.this,
							"No Internet Connection", Toast.LENGTH_SHORT)
							.show();
				}
			} else {
				Toast.makeText(EventsActivity.this,
						"Please enter valid details.", Toast.LENGTH_SHORT)
						.show();
			}
		}

	}

	/**
	 * Method called when the options menu is created.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_events, menu);
		return true;
	}

	/**
	 * Method called when an item in the options menu is selected.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.new_event) {

			state = 2;
			Toast.makeText(EventsActivity.this, "Loading Add Events",
					Toast.LENGTH_SHORT).show();
			NetworkAvailability network = new NetworkAvailability();
			if (network.isNetworkAvailable(this)) {
				grabURL();
			} else {
				Toast.makeText(EventsActivity.this, "No Internet Connection",
						Toast.LENGTH_SHORT).show();
			}

		}

		return true;
	}

	/**
	 * Method to create a dialog for create event.
	 */
	public void newEventDialog() {
		alertDialog = new AlertDialog.Builder(this).create();
		View dialog_layout = getLayoutInflater().inflate(
				R.layout.event_create_dialog, null);
		add_event_name = (EditText) dialog_layout
				.findViewById(R.id.add_event_name);
		add_event_date = (EditText) dialog_layout
				.findViewById(R.id.add_event_date);
		add_event_place = (Spinner) dialog_layout
				.findViewById(R.id.add_event_place);
		add_event_button = (Button) dialog_layout
				.findViewById(R.id.add_event_button);
		add_event_button.setOnClickListener(this);

		ArrayAdapter adapter = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, placeNamesArrayList);

		add_event_place.setAdapter(adapter);

		alertDialog.setView(dialog_layout);
		alertDialog.setTitle("Add Event");
		alertDialog.show();
	}

	/**
	 * Method to populate the List View.
	 */
	public void populateList() {
		// Find the ListView Resource
		ListView eventsListView = (ListView) findViewById(R.id.eventsListView);

		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
				R.layout.simplerow, eventsNameArrayList);

		eventsListView.setAdapter(listAdapter);

		eventsListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView parent, View v,
							int position, long id) {

						state = 1;
						position_list = position;
						event_id = eventsIDArrayList.get(position);
						Toast.makeText(EventsActivity.this,
								"Loading Event Information", Toast.LENGTH_SHORT)
								.show();
						NetworkAvailability network = new NetworkAvailability();
						if (network.isNetworkAvailable(EventsActivity.this)) {
							grabURL();
						} else {
							Toast.makeText(EventsActivity.this,
									"No Internet Connection",
									Toast.LENGTH_SHORT).show();
						}

					}
				});

	}

	/**
	 * Method to create the dialog to display the Event information.
	 * 
	 */
	public void eventDetailsDialog() {

		alertDialog = new AlertDialog.Builder(EventsActivity.this).create();
		View dialog_layout = getLayoutInflater().inflate(
				R.layout.event_info_dialog, null);
		event_name_dialog = (TextView) dialog_layout
				.findViewById(R.id.event_name_dialog);
		event_location_name_dialog = (TextView) dialog_layout
				.findViewById(R.id.event_location_name_dialog);
		event_date_dialog = (TextView) dialog_layout
				.findViewById(R.id.event_date_dialog);
		locate_event = (Button) dialog_layout
				.findViewById(R.id.locate_event_button);
		attend_event = (Button) dialog_layout
				.findViewById(R.id.attend_event_button);
		attendees_names = (Spinner) dialog_layout
				.findViewById(R.id.event_attendees);

		ArrayAdapter adapter = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, eventAttendeesList);

		attendees_names.setAdapter(adapter);

		locate_event.setOnClickListener(EventsActivity.this);
		attend_event.setOnClickListener(EventsActivity.this);

		event_name_dialog.setText("Name: " + eventDetailsNameArrayList);
		event_location_name_dialog.setText("Location: "
				+ eventDetailsLocationNameArrayList);
		event_date_dialog.setText("Date: " + eventDetailsDateArrayList);

		alertDialog.setView(dialog_layout);
		alertDialog.setTitle("Event Info");
		alertDialog.show();
	}

	/**
	 * Method to Parse the XML of the Events Details.
	 * 
	 * @param xml
	 *            - xml to parse.
	 * 
	 */
	public void parseEventDetailsXML(String xml) {

		XMLParser parser = new XMLParser();

		Document doc = parser.getDomElement(xml);

		eventAttendeesList.clear();

		NodeList nl = doc.getElementsByTagName(EVENTS);

		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);

			String name = parser.getValue(e, NAME);
			String date = parser.getValue(e, DATE);
			String location_name = parser.getValue(e, LOCATION_NAME);
			String location_address = parser.getValue(e, LOCATION_ADDRESS);
			String location = parser.getValue(e, LOCATION);

			eventDetailsNameArrayList = name;
			eventDetailsDateArrayList = date;
			eventDetailsLocationNameArrayList = location_name;
			eventDetailsLocationAddressArrayList = location_address;
			eventDetailsLocationArrayList = location;

			NodeList nl2 = e.getElementsByTagName("attendees");
			for (int j = 0; j < nl2.getLength(); j++) {
				Element e2 = (Element) nl2.item(j);
				NodeList nl3 = e.getElementsByTagName("attendee");
				for (int k = 0; k < nl3.getLength(); k++) {
					Element e3 = (Element) nl3.item(k);

					String attendee_name = parser.getValue(e3, "name");

					if (attendee_name.length() > 0) {
						eventAttendeesList.add(attendee_name);
						System.out.println("Attendee Name: " + attendee_name);
					}
				}
			}
		}
		eventDetailsDialog();

	}

	/**
	 * Method to Parse the XML of the Events List.
	 * 
	 * @param xml
	 *            - xml to parse.
	 * 
	 */
	public void parseXML(String xml) {
		XMLParser parser = new XMLParser();

		Document doc = parser.getDomElement(xml);

		NodeList nl = doc.getElementsByTagName(EVENTS);

		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);

			String status = e.getAttribute(STATUS);
			String failCode = e.getAttribute(CODE);

			String message = parser.getValue(e, MESSAGE);
			String event = parser.getValue(e, EVENTS);
			String eventID = parser.getValue(e, EVENT_ID);
			String eventName = parser.getValue(e, EVENT_NAME);
			String eventDate = parser.getValue(e, EVENT_DATE);

			eventsIDArrayList.add(eventID);
			eventsNameArrayList.add(eventName);
			eventsDateArrayList.add(eventDate);
		}
	}

	/**
	 * Method to Parse the XML of the Place List.
	 * 
	 * @param xml
	 *            - xml to parse.
	 * 
	 */
	public void parsePlaceListXML(String xml) {

		XMLParser parser = new XMLParser();

		Document doc = parser.getDomElement(xml);

		NodeList nl = doc.getElementsByTagName("place");

		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);

			String place_id = parser.getValue(e, "place_id");
			String place_name = parser.getValue(e, "place_name");

			if (placesGot == 0) {
				placeNamesArrayList.add(place_name);
				placeIDArrayList.add(place_id);
			}

		}
		placesGot = 1;
		newEventDialog();

	}

	/**
	 * Method to Parse the XML of the Place List Locations.
	 * 
	 * @param xml
	 *            - xml to parse.
	 * 
	 */
	public void parsePlaceListLocationsXML(String xml) {

		XMLParser parser = new XMLParser();

		Document doc = parser.getDomElement(xml);

		NodeList nl = doc.getElementsByTagName("place");

		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);

			String place_name = parser.getValue(e, "place_name");
			String place_lat = parser.getValue(e, "place_lat");
			String place_lon = parser.getValue(e, "place_lon");

			placeNameArrayList2.add(place_name);
			placeLatArrayList.add(place_lat);
			placeLonArrayList.add(place_lon);

		}

		System.out.println("LOCATE");
		for (int i = 0; i < placeNameArrayList2.size(); i++) {
			if (eventDetailsLocationNameArrayList.equals(placeNameArrayList2
					.get(i))) {

				SharedPreferences preferences = getSharedPreferences("details",
						MODE_PRIVATE);
				SharedPreferences.Editor editor = preferences.edit();

				editor.putString("eventMarkerLat", placeLatArrayList.get(i));
				editor.putString("eventMarkerLon", placeLonArrayList.get(i));
				editor.putString("eventMarkerName", eventDetailsNameArrayList);
				editor.putString("eventMarkerAvailable", "1");
				editor.commit();

				Intent myIntent = new Intent(this, MapsActivity.class);
				startActivityForResult(myIntent, 0);
				System.out.println("Switching to Map Activity");
				alertDialog.dismiss();
			}
		}

	}

	/**
	 * Method to Parse the XML of the Create Events.
	 * 
	 * @param xml
	 *            - xml to parse.
	 * 
	 */
	public void parseCreateEventXML(String xml) {

		XMLParser parser = new XMLParser();

		Document doc = parser.getDomElement(xml);

		NodeList nl = doc.getElementsByTagName("rsp");

		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);

			String status = e.getAttribute("status");
			String code = e.getAttribute("code");

			String message = parser.getValue(e, "message");

			System.out.println("Message: " + message);

			if (status.equals("ok")) {
				Toast.makeText(EventsActivity.this, "Event Added",
						Toast.LENGTH_SHORT).show();
			} else if (code.equals("400")) {
				Toast.makeText(EventsActivity.this, "Event Name Already Taken",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(EventsActivity.this, "Error Creating Event",
						Toast.LENGTH_SHORT).show();
			}

		}
	}

	/**
	 * Method to Parse the XML of the Attend Events.
	 * 
	 * @param xml
	 *            - xml to parse.
	 * 
	 */
	public void parseAttendEventXML(String xml) {
		XMLParser parser = new XMLParser();

		Document doc = parser.getDomElement(xml);

		NodeList nl = doc.getElementsByTagName("rsp");

		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);

			String status = e.getAttribute("status");
			String code = e.getAttribute("code");

			String message = parser.getValue(e, "message");

			System.out.println("Message: " + message);

			if (status.equals("ok")) {
				Toast.makeText(EventsActivity.this, "Attending Event",
						Toast.LENGTH_SHORT).show();
			} else if (code.equals("400")) {
				Toast.makeText(EventsActivity.this, "Event Does Not Exist",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(EventsActivity.this, "Error Attending Event",
						Toast.LENGTH_SHORT).show();
			}

		}
	}

	/**
	 * Method to print out all of the Events in the LOGCAT. For debuggin
	 * purposes.
	 */
	public void printEvents() {
		for (int i = 0; i < eventsIDArrayList.size(); i++) {
			System.out.println("=== Event ===");
			System.out.println("Event ID:" + eventsIDArrayList.get(i));
			System.out.println("Event Name:" + eventsNameArrayList.get(i));
			System.out.println("Event Date:" + eventsDateArrayList.get(i));
		}
	}

	/**
	 * Method to Execute the AsyncTask.
	 * 
	 */
	public void grabURL() {
		new GrabURL().execute();
	}

	private class GrabURL extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			Toast.makeText(EventsActivity.this, "Loading Events",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			ServerQueries query = new ServerQueries();

			switch (state) {
			case 0:
				xml = query.queryEventList();
				break;
			case 1:
				xml = query.queryEventDetails(eventsIDArrayList
						.get(position_list));
				break;
			case 2:
				xml = query.queryPlaceList();
				break;
			case 3:
				xml = query.createEvent(createEventName, createEventDate,
						createEventPlaceID, user_id, password);
			case 4:
				xml = query.attendEvent(user_id, event_id, password);
			case 5:
				xml = query.queryPlaceList();
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
				parseEventDetailsXML(xml);
				state = 0;
				break;
			case 2:
				parsePlaceListXML(xml);
				state = 0;
				break;
			case 3:
				parseCreateEventXML(xml);
				state = 0;
				break;
			case 4:
				parseAttendEventXML(xml);
				state = 0;
				break;
			case 5:
				parsePlaceListLocationsXML(xml);
				state = 0;
				break;
			}
			// printEvents();
			populateList();
		}

	}

}
