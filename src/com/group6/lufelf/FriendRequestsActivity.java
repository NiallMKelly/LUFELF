package com.group6.lufelf;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 
 * This class allows you handles all things related to the Friends Request
 * Screen.
 * 
 * @author Niall Kelly
 * @version 1.0.0
 * 
 */

public class FriendRequestsActivity extends Activity implements OnClickListener {

	// Constants for Friend Requests
	static final String RSP = "rsp";
	static final String STATUS = "status";
	static final String CODE = "code";
	static final String MESSAGE = "message";
	static final String FRIENDSHIP = "friendship";
	static final String USER = "user";
	static final String REQUEST_ID = "request_id";
	static final String USER_ID = "user_id";
	static final String LIB_NO = "lib_no";
	static final String NAME = "name";
	static final String USERNAME = "username";

	String user_id = new String();
	String password = new String();
	String xml = new String();
	String status = new String();
	String requestID = new String();
	String friendID = new String();

	int state = 0;

	Button accept_button;
	Button decline_button;

	AlertDialog alertDialog;

	ArrayList<String> friendsRequestIDArrayList = new ArrayList<String>();
	ArrayList<String> friendsRequestUserIDArrayList = new ArrayList<String>();
	ArrayList<String> friendsRequestNameArrayList = new ArrayList<String>();

	/**
	 * Method called upon the creations of the activity.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_requests);

		SharedPreferences preferences = getSharedPreferences("details",
				MODE_PRIVATE);
		this.user_id = preferences.getString("user_id", "");
		this.password = preferences.getString("password", "");

		NetworkAvailability network = new NetworkAvailability();
		if (network.isNetworkAvailable(this)) {
			grabURL();
		} else {
			Toast.makeText(FriendRequestsActivity.this,
					"No Internet Connection", Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * Method called when an item with a click listener has been clicked.
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.accept_friend_button) {
			System.out.println("ACCEPT");
			state = 1;
			status = "1";
			alertDialog.dismiss();
			NetworkAvailability network = new NetworkAvailability();
			if (network.isNetworkAvailable(this)) {
				grabURL();
			} else {
				Toast.makeText(FriendRequestsActivity.this,
						"No Internet Connection", Toast.LENGTH_SHORT).show();
			}
			Toast.makeText(FriendRequestsActivity.this,
					"Friend Request Accepted", Toast.LENGTH_SHORT).show();
		}

		if (v.getId() == R.id.decline_friend_button) {
			System.out.println("DECLINE");
			state = 1;
			status = "2";
			alertDialog.dismiss();
			NetworkAvailability network = new NetworkAvailability();
			if (network.isNetworkAvailable(this)) {
				grabURL();
			} else {
				Toast.makeText(FriendRequestsActivity.this,
						"No Internet Connection", Toast.LENGTH_SHORT).show();
			}
			Toast.makeText(FriendRequestsActivity.this,
					"Friend Request Declines", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Method called when options menu has been created.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_friend_requests, menu);
		return true;
	}

	/**
	 * Parse the friend requests XML.
	 * 
	 * @param xml
	 *            - XML to parse.
	 */
	public void parseXML(String xml) {

		XMLParser parser = new XMLParser();

		Document doc = parser.getDomElement(xml);

		NodeList nl = doc.getElementsByTagName(USER);

		for (int i = 0; i < nl.getLength(); i++) {

			Element e = (Element) nl.item(i);

			String request_id = parser.getValue(e, REQUEST_ID);
			String user_id = parser.getValue(e, USER_ID);
			String name = parser.getValue(e, NAME);
			String lib_no = parser.getValue(e, LIB_NO);
			String username = parser.getValue(e, USERNAME);

			friendsRequestIDArrayList.add(request_id);
			friendsRequestUserIDArrayList.add(user_id);
			friendsRequestNameArrayList.add(name);

			System.out.println("Friend Name:" + name);

		}
	}

	/**
	 * Parse the response to friend requests.
	 * 
	 * @param xml
	 *            - XML to parse.
	 */
	public void parseResponseXML(String xml) {
		XMLParser parser = new XMLParser();

		Document doc = parser.getDomElement(xml);

		NodeList nl = doc.getElementsByTagName(RSP);

		for (int i = 0; i < nl.getLength(); i++) {

			Element e = (Element) nl.item(i);

			String status = parser.getValue(e, STATUS);
			String code = parser.getValue(e, CODE);
			String message = parser.getValue(e, MESSAGE);
			String friendship = parser.getValue(e, FRIENDSHIP);

			System.out.println("Friendship: " + friendship);

		}
	}

	/**
	 * Populate the list view with a list of friends requests.
	 */
	public void populateList() {
		ListView friendsListView = (ListView) findViewById(R.id.friends_request_list);

		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
				R.layout.simplerow, friendsRequestNameArrayList);

		friendsListView.setAdapter(listAdapter);

		friendsListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView parent, View v,
							int position, long id) {

						requestID = friendsRequestIDArrayList.get(position);
						friendID = friendsRequestUserIDArrayList.get(position);

						alertDialog = new AlertDialog.Builder(
								FriendRequestsActivity.this).create();
						View dialog_layout = getLayoutInflater().inflate(
								R.layout.friends_request_dialog, null);
						accept_button = (Button) dialog_layout
								.findViewById(R.id.accept_friend_button);
						accept_button
								.setOnClickListener(FriendRequestsActivity.this);
						decline_button = (Button) dialog_layout
								.findViewById(R.id.decline_friend_button);
						decline_button
								.setOnClickListener(FriendRequestsActivity.this);
						alertDialog.setView(dialog_layout);
						alertDialog.setTitle("Friend Request");
						alertDialog.show();

					}
				});

	}

	public void grabURL() {
		new GrabURL().execute();
	}

	private class GrabURL extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			Toast.makeText(FriendRequestsActivity.this,
					"Loading Friend Requests", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			ServerQueries query = new ServerQueries();
			switch (state) {
			case 0:
				xml = query.getFriendRequests(user_id, password);
				break;
			case 1:
				xml = query.friendHandshake(requestID, user_id, friendID,
						password, status);
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
				parseResponseXML(xml);
				state = 0;
				break;
			}

			populateList();

		}

	}
}
