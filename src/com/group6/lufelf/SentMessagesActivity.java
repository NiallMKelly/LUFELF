package com.group6.lufelf;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 
 * This class handles the sent messages screen.
 * 
 * @author Niall Kelly
 * @version 1.0.0
 * 
 */

public class SentMessagesActivity extends Activity {

	String xml = new String();
	String user_id = new String();
	String password = new String();

	ArrayList<String> messageIDArrayList = new ArrayList<String>();
	ArrayList<String> messageToArrayList = new ArrayList<String>();
	ArrayList<String> messageArrayList = new ArrayList<String>();

	/**
	 * Method called upon the creation of the activity.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sent_messages);

		SharedPreferences preferences = getSharedPreferences("details",
				MODE_PRIVATE);
		this.user_id = preferences.getString("user_id", "");
		this.password = preferences.getString("password", "");

		NetworkAvailability network = new NetworkAvailability();
		if (network.isNetworkAvailable(this)) {
			grabURL();
		} else {
			Toast.makeText(SentMessagesActivity.this, "No Internet Connection",
					Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * Method called upon the creation of the options menu.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_sent_messages, menu);
		return true;
	}

	/**
	 * Parse the sent messages XML.
	 * 
	 * @param xml
	 *            - XML to parse.
	 */
	public void parseXML(String xml) {

		XMLParser parser = new XMLParser();

		Document doc = parser.getDomElement(xml);

		NodeList nl = doc.getElementsByTagName("sent_messages");
		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);
			NodeList nl2 = e.getElementsByTagName("message");
			for (int j = 0; j < nl2.getLength(); j++) {
				Element e2 = (Element) nl2.item(j);
				String message_id = parser.getValue(e2, "message_id");
				String message_to = parser.getValue(e2, "message_to");
				String message = parser.getValue(e2, "message");

				if (message_id.length() > 0 && message_to.length() > 0
						&& message.length() > 0) {
					messageIDArrayList.add(message_id);
					messageToArrayList.add(message_to);
					messageArrayList.add(message);
				}
			}
		}
		populateList();
	}

	/**
	 * Populate the list view with a list of sent messages.
	 */
	public void populateList() {
		ListView sentMessagesListView = (ListView) findViewById(R.id.sent_messages_view_list);

		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
				R.layout.simplerow, messageArrayList);

		sentMessagesListView.setAdapter(listAdapter);
	}

	public void grabURL() {
		new GrabURL().execute();
	}

	private class GrabURL extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			Toast.makeText(SentMessagesActivity.this, "Loading Messages",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			ServerQueries query = new ServerQueries();

			xml = query.querySentMessages(user_id, password);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			parseXML(xml);

			// populateList();

		}

	}

}
