package com.group6.lufelf;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * This class handles the registration of a user.
 * 
 * @author Niall Kelly
 * @version 1.0.0
 */

public class RegisterActivity extends Activity implements OnClickListener {

	// Constants
	static final String RSP = "rsp";
	static final String STATUS = "status";
	static final String CODE = "code";
	static final String IS_NEW = "is_new";
	static final String USER = "user";
	static final String USER_ID = "user_id";
	static final String NAME = "name";
	static final String LIB_NO = "lib_no";
	static final String MESSAGE = "message";

	// View

	View vv;

	String xml = new String();

	int duration = Toast.LENGTH_SHORT;

	// Global Variables
	String name;
	String username;
	String password;
	String confPassword;
	String libno;
	String dob;

	// Response variables
	String responseStatus;
	String responseCode;
	String responseMessage;

	// Colours
	int backgroundColour = Color.rgb(234, 234, 234);
	int invalidRed = Color.rgb(255, 148, 148);

	/**
	 * Method called upon the creation of the activity.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		// Register Button OnClick Listener
		Button register_button = (Button) findViewById(R.id.registerAccoutButton);
		register_button.setOnClickListener(this);

	}

	/**
	 * Method called when an item with a click listener is clicked.
	 */
	public void onClick(View v) {

		vv = v;

		Context context = getApplicationContext();

		ValidEmail emailCheck = new ValidEmail();

		boolean emailValid = false;
		boolean passValid = false;

		// Get Text Field Details
		EditText name_input = (EditText) findViewById(R.id.nameTextBox);
		EditText username_input = (EditText) findViewById(R.id.regUsernameText);
		EditText password_input = (EditText) findViewById(R.id.regPasswordText);
		EditText conf_password_input = (EditText) findViewById(R.id.confPassText);
		EditText libno_input = (EditText) findViewById(R.id.libNoText);
		EditText dob_input = (EditText) findViewById(R.id.dobText);

		// Store Details in Strings
		name = name_input.getText().toString();
		username = username_input.getText().toString();
		password = password_input.getText().toString();
		confPassword = conf_password_input.getText().toString();
		libno = libno_input.getText().toString();
		dob = dob_input.getText().toString();

		if (v.getId() == R.id.registerAccoutButton) {

			if (name.length() == 0 || username.length() == 0
					|| password.length() == 0 || confPassword.length() == 0
					|| libno.length() == 0 || dob.length() == 0) {
				CharSequence text = "Some fields are blank.";
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
			}

			if (emailCheck.isEmailValid(username)) {
				emailValid = true;
				username_input.setBackgroundColor(backgroundColour);
			} else {
				username_input.setBackgroundColor(invalidRed);
				CharSequence text = "Email is not valid.";
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
			}

			if (password.equals(confPassword)) {
				passValid = true;
				conf_password_input.setBackgroundColor(backgroundColour);
			} else {
				conf_password_input.setBackgroundColor(invalidRed);
				CharSequence text = "Password are not equal.";
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
			}

			if (!libNoValid()) {
				libno_input.setBackgroundColor(invalidRed);
				CharSequence text = "Please enter a valid Library Card Number.";
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
			} else {
				libno_input.setBackgroundColor(backgroundColour);
			}

			if (!passwordValid()) {
				password_input.setBackgroundColor(invalidRed);
				CharSequence text = "Please enter a valid Password. 6 - 16 Characters";
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
			} else {
				password_input.setBackgroundColor(backgroundColour);
			}

			if (!dobValid()) {
				dob_input.setBackgroundColor(invalidRed);
				CharSequence text = "D.O.B Not valid - DD/MM/YYYY";
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
			} else {
				dob_input.setBackgroundColor(backgroundColour);
			}

			if (passValid && emailValid && passwordValid() && libNoValid()
					&& dobValid()) {
				NetworkAvailability network = new NetworkAvailability();
				if (network.isNetworkAvailable(this)) {
					grabURL();
				} else {
					Toast.makeText(RegisterActivity.this,
							"No Internet Connection", Toast.LENGTH_SHORT)
							.show();
				}
			}
		}
	}

	/**
	 * Method to check if the library card number is valid.
	 * 
	 * @return True if valid, False if not.
	 */
	private boolean libNoValid() {

		if (libno.length() == 8) {
			return true;
		}

		return false;
	}

	/**
	 * Method to check if the Date Of Birth is valid.
	 * 
	 * @return True if valid, False if not.
	 */
	private boolean dobValid() {

		if (ValidDate.isValid(dob)) {
			return true;
		}

		return false;
	}

	/**
	 * Method to check if the password is valid.
	 * 
	 * @return True if valid, False if not.
	 */
	private boolean passwordValid() {

		if (password.length() >= 6 && password.length() <= 16) {
			return true;
		}

		return false;
	}

	/**
	 * Method to handle the user registration.
	 * 
	 * @param v
	 *            - Current view
	 */
	public void register(View v) {

		Context context = getApplicationContext();

		if (responseStatus.equals("fail")) {
			System.out.println(responseMessage);
		}

		if (responseStatus.equals("ok")) {

			SharedPreferences preferences = getSharedPreferences("details",
					MODE_PRIVATE);
			SharedPreferences.Editor editor = preferences.edit();

			editor.putString("location_status", "1");
			editor.commit();

			Intent myIntent = new Intent(v.getContext(), LoginActivity.class);
			startActivityForResult(myIntent, 0);
			System.out.println("Switching to Login Activity");
			CharSequence text = "Reigstered";
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
			finish();
		}

	}

	/**
	 * Parse the registration XML
	 * 
	 * @param xml
	 *            - XML to parse.
	 */
	public void parseXML(String xml) {
		XMLParser parser = new XMLParser();

		Document doc = parser.getDomElement(xml);

		NodeList nl = doc.getElementsByTagName(RSP);

		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);

			String status = e.getAttribute(STATUS);
			responseStatus = status;
			String fail_code = e.getAttribute(CODE);
			responseCode = fail_code;
			String message = parser.getValue(e, MESSAGE);
			responseMessage = message;
			String is_new = parser.getValue(e, IS_NEW);
			String user_id = parser.getValue(e, USER_ID);
			String name = parser.getValue(e, NAME);
			String lib_no = parser.getValue(e, LIB_NO);

			System.out.println("Status: " + status);
			System.out.println("Fail Code: " + fail_code);
			System.out.println("Message: " + message);
			System.out.println("Is New: " + is_new);
			System.out.println("User ID: " + user_id);
			System.out.println("Name: " + name);
			System.out.println("Lib No.: " + lib_no);

			if (status.equals("fail")) {
				CharSequence text = "Library Card Number Or Username Already Taken";
				Toast toast = Toast.makeText(RegisterActivity.this, text,
						duration);
				toast.show();
			}

		}

	}

	/**
	 * Method called upon the creation of the options menu.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_register, menu);
		return true;
	}

	public void grabURL() {
		new GrabURL().execute();
	}

	private class GrabURL extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			ServerQueries query = new ServerQueries();

			xml = query.createUser(name, libno, username, confPassword, dob,
					"1", "1");

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			parseXML(xml);
			register(vv);

		}

	}

}
