package com.group6.lufelf;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * This class handles all things related to the Friends List. Including the
 * friends list, adding friends, friends information, locating a friend and
 * deleting a friend.
 * 
 * @author Niall Kelly
 * @version 1.0.0
 * 
 */

public class FriendsActivity extends Activity implements OnClickListener {

	// XML Constants

	// Friends List
	static final String RSP = "rsp";
	static final String STATUS = "status";
	static final String CODE = "code";
	static final String MESSAGE = "message";
	static final String FRIEND = "friend";
	static final String FRIEND_NAME = "friend_name";
	static final String FRIEND_ID = "friend_id";
	static final String FRIEND_USERNAME = "friend_username";
	static final String LOCATION_STATUS = "location_status";
	static final String LAT = "lat";
	static final String LON = "lon";

	// Query User Details
	static final String USER = "user";
	static final String FRIEND_USER_ID = "user_id";

	// Stored information
	String user_id = new String();
	String password = new String();

	String friendUsername = new String();
	String friendUserId = new String();
	String friend_id = new String();

	String location_status = new String();

	String current_friend_id = new String();
	int current_position;

	AlertDialog alertDialog;

	EditText add_friends_username;
	Button add_friends_button;
	Button delete_friend_button;
	Button friend_track_button;
	ImageView friend_avatar;

	Bitmap bitmap;

	// Current state
	public int state = 0; // 0 = defualt state, 1 = add friend.

	String xml = new String();

	ArrayList<String> friendsNameArrayList = new ArrayList<String>();
	ArrayList<String> friendsUsernameArrayList = new ArrayList<String>();
	ArrayList<String> friendsUserIDArrayList = new ArrayList<String>();
	ArrayList<String> friendsLocationStatusArrayList = new ArrayList<String>();
	ArrayList<String> friendsLatArrayList = new ArrayList<String>();
	ArrayList<String> friendsLonArrayList = new ArrayList<String>();
	ArrayList<String> friendsRequestArrayList = new ArrayList<String>();

	/**
	 * Method called on the creation of the activity.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);

		SharedPreferences preferences = getSharedPreferences("details",
				MODE_PRIVATE);
		this.user_id = preferences.getString("user_id", "");
		this.password = preferences.getString("password", "");

		friendsRequestArrayList.add("1");
		friendsRequestArrayList.add("2");
		friendsRequestArrayList.add("3");

		NetworkAvailability network = new NetworkAvailability();
		if (network.isNetworkAvailable(this)) {
			grabURL();
		} else {
			Toast.makeText(FriendsActivity.this, "No Internet Connection",
					Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * Method called when an item with a click listener has been clicked.
	 */
	public void onClick(View v) {

		SharedPreferences preferences = getSharedPreferences("details",
				MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();

		if (v.getId() == R.id.add_friend_button) {
			friendUsername = add_friends_username.getText().toString();
			System.out.println("FRIEND USERNAME: " + friendUsername);
			alertDialog.dismiss();
			state = 1;
			NetworkAvailability network = new NetworkAvailability();
			if (network.isNetworkAvailable(this)) {
				grabURL();
			} else {
				Toast.makeText(FriendsActivity.this, "No Internet Connection",
						Toast.LENGTH_SHORT).show();
			}
		}

		if (v.getId() == R.id.locate_friend_button) {
			System.out.println("LOCATE");
			if (location_status.equals("1")) {
				if (preferences.getString("friendMarkerLat", "").length() > 0) {
					editor.putString("friendMarkerAvailable", "1");
					editor.commit();
					Intent myIntent = new Intent(v.getContext(),
							MapsActivity.class);
					startActivityForResult(myIntent, 0);
					System.out.println("Switching to Map Activity");
					alertDialog.dismiss();
				} else {
					editor.putString("friendMarkerAvailable", "0");
					editor.commit();
					Toast.makeText(FriendsActivity.this,
							"Friend Location Unavailable", Toast.LENGTH_SHORT)
							.show();
					alertDialog.dismiss();
				}
			} else {
				editor.putString("friendMarkerAvailable", "0");
				editor.commit();
				Toast.makeText(FriendsActivity.this,
						"Friend Location Unavailable", Toast.LENGTH_SHORT)
						.show();
				alertDialog.dismiss();
			}
		}

		if (v.getId() == R.id.delete_friend_button) {
			state = 3;
			NetworkAvailability network = new NetworkAvailability();
			if (network.isNetworkAvailable(this)) {
				grabURL();
			} else {
				Toast.makeText(FriendsActivity.this, "No Internet Connection",
						Toast.LENGTH_SHORT).show();
			}
			alertDialog.dismiss();
		}

	}

	public void grabURL() {
		new GrabURL().execute();
	}

	/**
	 * Method that is called upon the creation of the options menu.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_friends, menu);
		return true;
	}

	/**
	 * Method that is called when an item in the options menu is selected.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.add_friend) {
			alertDialog = new AlertDialog.Builder(this).create();
			View dialog_layout = getLayoutInflater().inflate(
					R.layout.add_friends_dialog, null);
			add_friends_username = (EditText) dialog_layout
					.findViewById(R.id.add_friends_username);
			add_friends_button = (Button) dialog_layout
					.findViewById(R.id.add_friend_button);
			add_friends_button.setOnClickListener(this);
			alertDialog.setView(dialog_layout);
			alertDialog.setTitle("Add Friend");
			alertDialog.show();

		}

		if (id == R.id.friend_requests) {
			Intent myIntent = new Intent(this, FriendRequestsActivity.class);
			startActivityForResult(myIntent, 0);
			System.out.println("Switching to Friend Requests Activity");

		}

		return true;
	}

	/**
	 * Populate the list view with a list of view.
	 */
	public void populateList() {
		// Find the ListView Resource
		ListView friendsListView = (ListView) findViewById(R.id.friends_view_list);

		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
				R.layout.simplerow, friendsNameArrayList);

		friendsListView.setAdapter(listAdapter);

		friendsListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView parent, View v,
							int position, long id) {

						current_position = position;
						current_friend_id = friendsUserIDArrayList
								.get(position);

						state = 4;
						NetworkAvailability network = new NetworkAvailability();
						if (network.isNetworkAvailable(FriendsActivity.this)) {
							grabURL();
						} else {
							Toast.makeText(FriendsActivity.this,
									"No Internet Connection",
									Toast.LENGTH_SHORT).show();
						}
					}
				});

	}

	/**
	 * Create a dialog that contains the friends information.
	 */
	public void friendInfoDialog() {

		alertDialog = new AlertDialog.Builder(FriendsActivity.this).create();
		View dialog_layout = getLayoutInflater().inflate(
				R.layout.friend_info_dialog, null);
		TextView friend_name_textview = (TextView) dialog_layout
				.findViewById(R.id.friend_name_dialog);
		TextView friend_username_textview = (TextView) dialog_layout
				.findViewById(R.id.friend_username_dialog);
		friend_track_button = (Button) dialog_layout
				.findViewById(R.id.locate_friend_button);
		delete_friend_button = (Button) dialog_layout
				.findViewById(R.id.delete_friend_button);
		friend_avatar = (ImageView) dialog_layout.findViewById(R.id.avatar);
		friend_track_button.setOnClickListener(FriendsActivity.this);
		delete_friend_button.setOnClickListener(FriendsActivity.this);

		friend_name_textview.setText("Name: "
				+ friendsNameArrayList.get(current_position));
		friend_username_textview.setText("Username: "
				+ friendsUsernameArrayList.get(current_position));

		ServerQueries query = new ServerQueries();

		if (bitmap != null) {
			friend_avatar.setImageBitmap(bitmap);
		}

		SharedPreferences preferences = getSharedPreferences("details",
				MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();

		location_status = friendsLocationStatusArrayList.get(current_position);

		System.out.println("Location Status: " + location_status);
		friend_id = friendsUserIDArrayList.get(current_position);
		System.out.println("Friend ID: "
				+ friendsUserIDArrayList.get(current_position));

		if (location_status.equals("1")) {

			editor.putString("friendMarkerLat",
					friendsLatArrayList.get(current_position));
			editor.putString("friendMarkerLon",
					friendsLonArrayList.get(current_position));
			editor.putString("friendMarkerName",
					friendsNameArrayList.get(current_position));

			System.out.println("Friend Lat: "
					+ friendsLatArrayList.get(current_position));
			System.out.println("Friend Lon: "
					+ friendsLonArrayList.get(current_position));

			editor.commit();

		}

		alertDialog.setView(dialog_layout);
		alertDialog.setTitle("Friend Info");
		alertDialog.show();
	}

	/**
	 * Parse the friends list XML
	 * 
	 * @param xml
	 *            - XML to parse.
	 */
	public void parseXML(String xml) {
		XMLParser parser = new XMLParser();

		Document doc = parser.getDomElement(xml);

		NodeList nl = doc.getElementsByTagName(FRIEND);

		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);

			String friend_name = parser.getValue(e, FRIEND_NAME);
			String friend_id = parser.getValue(e, FRIEND_ID);
			String friend_username = parser.getValue(e, FRIEND_USERNAME);
			String location_status = parser.getValue(e, LOCATION_STATUS);
			String lat = parser.getValue(e, LAT);
			String lon = parser.getValue(e, LON);

			friendsNameArrayList.add(friend_name);
			friendsUserIDArrayList.add(friend_id);
			friendsUsernameArrayList.add(friend_username);
			friendsLocationStatusArrayList.add(location_status);
			friendsLatArrayList.add(lat);
			friendsLonArrayList.add(lon);

		}

	}

	/**
	 * Parse the friends details XML
	 * 
	 * @param xml
	 *            - XML to parse.
	 */
	public void parseFriendDetailsXML(String xml) {
		XMLParser parser = new XMLParser();

		Document doc = parser.getDomElement(xml);

		NodeList nl = doc.getElementsByTagName(USER);

		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);

			friendUserId = parser.getValue(e, FRIEND_USER_ID);
			System.out.println("Friend User ID: " + friendUserId);

			SharedPreferences preferences = getSharedPreferences("details",
					MODE_PRIVATE);
			SharedPreferences.Editor editor = preferences.edit();

			editor.putString("friend_id_temp", friendUserId);
			friend_id = preferences.getString("friend_id_temp", "");
			editor.apply();

			friend_id = preferences.getString("friend_id_temp", "");

			state = 2;

			NetworkAvailability network = new NetworkAvailability();
			if (network.isNetworkAvailable(this)) {
				grabURL();
			} else {
				Toast.makeText(FriendsActivity.this, "No Internet Connection",
						Toast.LENGTH_SHORT).show();
			}

		}
	}

	/**
	 * Parse the sending friend requests XML
	 * 
	 * @param xml
	 *            - XML to parse.
	 */
	public void parseFriendRequestXML(String xml) {
		XMLParser parser = new XMLParser();

		Document doc = parser.getDomElement(xml);

		NodeList nl = doc.getElementsByTagName(RSP);

		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);

			String status = e.getAttribute(STATUS);
			String code = e.getAttribute(CODE);
			String message = e.getAttribute(MESSAGE);

			if (status.equals("ok")) {
				Toast.makeText(FriendsActivity.this, "Friend Request Sent",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(
						FriendsActivity.this,
						"You are already friends or a friend request is pending.",
						Toast.LENGTH_SHORT).show();
			}

		}
	}

	/**
	 * Parse the delete friend XML
	 * 
	 * @param xml
	 *            - XML to parse.
	 */
	public void parseDeleteFriendXML(String xml) {
		XMLParser parser = new XMLParser();

		Document doc = parser.getDomElement(xml);

		NodeList nl = doc.getElementsByTagName(RSP);

		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);

			String status = e.getAttribute(STATUS);
			String code = e.getAttribute(CODE);
			String message = e.getAttribute(MESSAGE);

			if (status.equals("ok")) {
				Toast.makeText(FriendsActivity.this, "Friend Deleted",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(FriendsActivity.this,
						"There was an error deleting that person.",
						Toast.LENGTH_SHORT).show();
			}

		}
	}

	private class GrabURL extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			Toast.makeText(FriendsActivity.this, "Loading Friends",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			ServerQueries query = new ServerQueries();

			switch (state) {
			case 0:
				xml = query.queryFriendList(user_id, password);
				break;
			case 1:
				xml = query.queryUserDetails(friendUsername);
				break;
			case 2:
				xml = query.makeFriends(user_id, friend_id, password);
				break;
			case 3:
				xml = query.deleteFriend(user_id, friend_id, password);
			case 4:
				System.out.println("Getting Avatar");
				System.out.println("FRIEND ID: " + current_friend_id);
				bitmap = query.getAvatar(current_friend_id);
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
				parseFriendDetailsXML(xml);
				break;
			case 2:
				parseFriendRequestXML(xml);
				state = 0;
				break;
			case 3:
				parseDeleteFriendXML(xml);
				state = 0;
				break;
			case 4:
				friendInfoDialog();
				state = 0;
			}

			populateList();

		}

	}

}
