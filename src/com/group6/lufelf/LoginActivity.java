package com.group6.lufelf;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * This class handles the login screen.
 * 
 * @author Niall Kelly
 * @version 1.0.0
 * 
 */

public class LoginActivity extends Activity implements OnClickListener {

	// Constants for XML Parsing
	static final String RSP = "rsp";
	static final String STATUS = "status";
	static final String CODE = "code";
	static final String USER_ID = "user_id";
	static final String MESSAGE = "message";
	static final String LOGGED_IN = "logged_in";

	// Constants for storing in preferences
	static final String DETAILS = "details";
	static final String USERNAME = "username";
	static final String PASSWORD = "password";

	String userID;

	// Returned XML
	String xml = new String();

	// Strings
	String username;
	String password;
	String hashedPassword;
	String responseStatus;
	String responseFailCode;

	// Booleans
	boolean emailValid = false;
	boolean usernameEmpty = true;
	boolean passwordEmpty = true;

	// View
	View vv;

	// HTTP Response
	String httpResponse;

	// Toast Variables
	int duration = Toast.LENGTH_SHORT;

	/**
	 * Method called upon creation of the activity.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// Create the Shared Prefs.
		SharedPreferences preferences = getSharedPreferences(DETAILS,
				MODE_PRIVATE);

		// On Click Listener for loginButton
		Button login_button = (Button) findViewById(R.id.loginButton);
		login_button.setOnClickListener(this);

		String loggedIn = preferences.getString(LOGGED_IN, "");

		if (loggedIn.equals("1")) {
			Intent myIntent = new Intent(this, MainActivity.class);
			startActivityForResult(myIntent, 0);
			System.out.println("Switching to Main Activity");
			finish();
		}

	}

	/**
	 * Method called when an item with a click listeners has been clicked.
	 */
	public void onClick(View v) {

		vv = v;

		ValidEmail emailCheck = new ValidEmail();
		NetworkAvailability network = new NetworkAvailability();

		// Get text box info
		EditText username_input = (EditText) findViewById(R.id.usernameText);
		EditText password_input = (EditText) findViewById(R.id.passwordText);

		// Store data in strings.
		username = username_input.getText().toString();
		password = password_input.getText().toString();

		// Check if loginButton is pressed.
		if (v.getId() == R.id.loginButton) {

			// Check if user entered a username
			if (username.length() != 0) {
				usernameEmpty = false;
			} else {
				CharSequence text = "Please Enter a Username";
				Toast toast = Toast.makeText(this, text, duration);
				toast.show();
			}

			// Check if user entered a password
			if (password.length() != 0) {
				passwordEmpty = false;
			} else {
				CharSequence text = "Please Enter a Password";
				Toast toast = Toast.makeText(this, text, duration);
				toast.show();
			}

			// Check if email is valid
			if (emailCheck.isEmailValid(username)) {
				emailValid = true;
			} else {
				CharSequence text = "Please Enter a Valid Email";
				Toast toast = Toast.makeText(this, text, duration);
				toast.show();
			}

			if (emailValid == true && usernameEmpty == false
					&& passwordEmpty == false) {
				if (network.isNetworkAvailable(this)) {
					grabURL();
				} else {
					CharSequence text = "No Internet Connection";
					Toast toast = Toast.makeText(this, text, duration);
					toast.show();
				}
			}

		}

	}

	/**
	 * Method called when an item in the options bar has been selected.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.register_option) {
			Intent myIntent = new Intent(this, RegisterActivity.class);
			startActivityForResult(myIntent, 0);
			System.out.println("Switching to Register Activity");
		}

		return true;
	}

	/**
	 * Method to check if the details are valid and therefore login.
	 * 
	 * @param v
	 *            - The current view of the app.
	 */
	public void loginCheck(View v) {
		// If all condition are met log user in.

		NetworkAvailability network = new NetworkAvailability();
		Context context = getApplicationContext();

		if (responseStatus.equals("fail") && responseFailCode.equals("400")) {
			CharSequence text = "You Entered an Incorrect Username or Password";
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}

		if (responseStatus.equals("fail") && responseFailCode.equals("500")) {
			CharSequence text = "The server is currently down, Please Try Again Later.";
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}

		if (responseStatus.equals("ok")) {

			HashString hash = new HashString();

			SharedPreferences preferences = getSharedPreferences(DETAILS,
					MODE_PRIVATE);
			SharedPreferences.Editor editor = preferences.edit();

			try {
				hashedPassword = hash.HashString(password);
				editor.putString(PASSWORD, hashedPassword);
				editor.apply();
			} catch (Exception e) {
				e.printStackTrace();
			}

			editor.putString(LOGGED_IN, "1");
			editor.apply();
			editor.putString(USERNAME, username);
			editor.apply();
			editor.putString(USER_ID, userID);
			editor.apply();

			Intent myIntent = new Intent(v.getContext(), MainActivity.class);
			startActivityForResult(myIntent, 0);
			System.out.println("Switching to Main Activity");
			finish();
		}
	}

	/**
	 * Parse the login XML
	 * 
	 * @param xml
	 *            - XML to parse
	 */
	public void parseXML(String xml) {
		XMLParser parser = new XMLParser();

		Document doc = parser.getDomElement(xml);

		NodeList nl = doc.getElementsByTagName(RSP);

		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);

			String status = e.getAttribute(STATUS);
			responseStatus = status;
			String failCode = e.getAttribute(CODE);
			responseFailCode = failCode;

			String message = parser.getValue(e, MESSAGE);
			userID = parser.getValue(e, USER_ID);

			System.out.println("LOGIN INFORMAION:");
			System.out.println("Status: " + status);
			System.out.println("Fail Code: " + failCode);
			System.out.println("Message: " + message);
			System.out.println("User ID: " + userID);

		}

	}

	/**
	 * Method called when the options menu has been created.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}

	public void grabURL() {
		new GrabURL().execute();
	}

	private class GrabURL extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			ServerQueries query = new ServerQueries();

			xml = query.loginUser(username, password);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			parseXML(xml);
			loginCheck(vv);

		}

	}

}
