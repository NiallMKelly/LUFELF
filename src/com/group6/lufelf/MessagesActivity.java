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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * This class handled received messages and send messages.
 * 
 * @author Niall Kelly
 * @version 1.0.0
 * 
 */

public class MessagesActivity extends Activity implements OnClickListener {

	ArrayList<String> messageIDArrayList = new ArrayList<String>();
	ArrayList<String> messageFromArrayList = new ArrayList<String>();
	ArrayList<String> messageArrayList = new ArrayList<String>();

	ArrayList<String> friendsNameArrayList = new ArrayList<String>();
	ArrayList<String> friendsUserIDArrayList = new ArrayList<String>();

	int state = 0;
	int friendsGot = 0;
	String xml = new String();
	String user_id = new String();
	String password = new String();
	String message = new String();
	String user_id_to = new String();

	AlertDialog alertDialog;

	Button send_message_but;
	EditText message_area;
	Spinner friends_list;

	/**
	 * Method which is called upon Activity creation.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messages);

		SharedPreferences preferences = getSharedPreferences("details",
				MODE_PRIVATE);
		this.user_id = preferences.getString("user_id", "");
		this.password = preferences.getString("password", "");

		NetworkAvailability network = new NetworkAvailability();
		if (network.isNetworkAvailable(this)) {
			grabURL();
		} else {
			Toast.makeText(MessagesActivity.this, "No Internet Connection",
					Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * Method which is called when the options menu is created.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_messages, menu);
		return true;
	}

	/**
	 * Method which is called when an item in the options menu is selected.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.send_message) {
			NetworkAvailability network = new NetworkAvailability();
			if (network.isNetworkAvailable(this)) {
				state = 1;
				grabURL();
			} else {
				Toast.makeText(MessagesActivity.this, "No Internet Connection",
						Toast.LENGTH_SHORT).show();
			}
		}

		if (id == R.id.sent_message) {
			Intent myIntent = new Intent(this, SentMessagesActivity.class);
			startActivityForResult(myIntent, 0);
			System.out.println("Switching to Sent Messages Activity");
		}

		return true;
	}

	/**
	 * Method which is called when an item with a click listeners is clicked.
	 */
	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.send_messsage_button) {
			System.out.println("SEND MESSAGE");
			int id = friends_list.getSelectedItemPosition();
			user_id_to = friendsUserIDArrayList.get(id);
			message = message_area.getText().toString();
			System.out.println("User ID To: " + user_id_to);
			System.out.println("Message: " + message);
			NetworkAvailability network = new NetworkAvailability();
			if (network.isNetworkAvailable(this)) {
				state = 2;
				grabURL();
			} else {
				Toast.makeText(MessagesActivity.this, "No Internet Connection",
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	/**
	 * Method to populate the list view with the received messages.
	 */
	public void populateList() {

		ListView messagesListView = (ListView) findViewById(R.id.messages_view_list);

		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
				R.layout.simplerow, messageArrayList);

		messagesListView.setAdapter(listAdapter);

	}

	/**
	 * Method to create the send message dialog.
	 */
	public void sendMessageDialog() {
		alertDialog = new AlertDialog.Builder(MessagesActivity.this).create();
		View dialog_layout = getLayoutInflater().inflate(
				R.layout.send_message_dialog, null);
		friends_list = (Spinner) dialog_layout
				.findViewById(R.id.send_message_friend);
		message_area = (EditText) dialog_layout.findViewById(R.id.message_area);
		send_message_but = (Button) dialog_layout
				.findViewById(R.id.send_messsage_button);
		send_message_but.setOnClickListener(MessagesActivity.this);

		ArrayAdapter adapter = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, friendsNameArrayList);

		friends_list.setAdapter(adapter);

		alertDialog.setView(dialog_layout);
		alertDialog.setTitle("Send Message");
		alertDialog.show();
	}

	/**
	 * Method to parse the friends list XML
	 * 
	 * @param xml
	 *            - XML to parse.
	 */
	public void parseFriendsListXML(String xml) {

		XMLParser parser = new XMLParser();

		Document doc = parser.getDomElement(xml);

		NodeList nl = doc.getElementsByTagName("friend");

		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);

			String user_id = parser.getValue(e, "friend_id");
			String friend_name = parser.getValue(e, "friend_name");

			if (friendsGot == 0) {
				friendsNameArrayList.add(friend_name);
				friendsUserIDArrayList.add(user_id);
			}

		}
		friendsGot = 1;
		sendMessageDialog();

	}

	/**
	 * Method to parse the send message XML.
	 * 
	 * @param xml
	 *            - XML to parse.
	 */
	public void parseSendMessageXML(String xml) {

		XMLParser parser = new XMLParser();

		Document doc = parser.getDomElement(xml);

		NodeList nl = doc.getElementsByTagName("received_messages");

		System.out.println("GETTING HERE");
		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);

			System.out.println("GETTING HERE TOO");
			String status = e.getAttribute("status");
			String code = e.getAttribute("code");

			String message = parser.getValue(e, "message");

			System.out.println("MESSAGE: " + message);

			if (status.equals("ok")) {
				Toast.makeText(MessagesActivity.this,
						"Message Sent Successfully.", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(MessagesActivity.this, "Error Sending Message",
						Toast.LENGTH_SHORT).show();
			}
		}
		alertDialog.dismiss();
	}

	/**
	 * Method to parse the received message XML.
	 * 
	 * @param xml
	 *            - XML to parse.
	 */
	public void parseReceivedMessageXML(String xml) {

		XMLParser parser = new XMLParser();

		Document doc = parser.getDomElement(xml);

		NodeList nl = doc.getElementsByTagName("received_messages");
		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);
			NodeList nl2 = e.getElementsByTagName("message");
			for (int j = 0; j < nl2.getLength(); j++) {
				Element e2 = (Element) nl2.item(j);
				String message_id = parser.getValue(e2, "message_id");
				String message_from = parser.getValue(e2, "message_from");
				String message = parser.getValue(e2, "message");

				if (message_id.length() > 0 && message_from.length() > 0
						&& message.length() > 0) {
					messageIDArrayList.add(message_id);
					messageFromArrayList.add(message_from);
					messageArrayList.add(message);
				}
			}
		}
		populateList();
	}

	public void grabURL() {
		new GrabURL().execute();
	}

	private class GrabURL extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			Toast.makeText(MessagesActivity.this, "Loading Messages",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			ServerQueries query = new ServerQueries();

			switch (state) {
			case 0:
				xml = query.queryReceivedMessages(user_id, password);
				break;
			case 1:
				xml = query.queryFriendList(user_id, password);
				break;
			case 2:
				System.out.println("GRABURL");
				xml = query.sendMessage(user_id, password, user_id_to, message);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			switch (state) {
			case 0:
				System.out.println(xml);
				parseReceivedMessageXML(xml);
				break;
			case 1:
				parseFriendsListXML(xml);
				state = 0;
				break;
			case 2:
				System.out.println(xml);
				System.out.println("PARSER");
				parseSendMessageXML(xml);
				state = 0;
				break;
			}

			// populateList();

		}

	}

}
