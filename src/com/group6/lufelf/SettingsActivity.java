package com.group6.lufelf;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * This class handles the settings menu, Logout, Upload Avatar, Location Status
 * and Deleting Accounts.
 * 
 * @author Niall Kelly
 * @version 1.0.0
 * 
 */

public class SettingsActivity extends Activity implements OnClickListener {

	// Constants for XML parse
	static final String RSP = "rsp";
	static final String STATUS = "status";
	static final String CODE = "code";
	static final String MESSAGE = "message";

	private static final int SELECT_PICTURE = 1;

	int state = 0;

	String xml = new String();
	String userID = new String();
	String password = new String();
	String location_status = new String();
	String image = new String();
	String ba1 = new String();

	AlertDialog alertDialog;

	ArrayList<String> settingsMenuItems = new ArrayList<String>();

	Button logout_button;
	Button logout_cancel_button;
	Button delete_account_button;
	Button enable_location_status_button;
	Button disable_location_status_button;
	Button upload_avatar_button;
	Button load_avatar_button;

	ImageView selected_image;

	EditText delete_account_text;

	/**
	 * Method which is called when the instance is created
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		populateSettingsList();

		// Get details from stored
		SharedPreferences preferences = getSharedPreferences("details",
				MODE_PRIVATE);
		userID = preferences.getString("user_id", "");
		System.out.println("User ID:" + userID);
		password = preferences.getString("password", "");
		System.out.println("Password:" + password);

	}

	/**
	 * Method which is used to get the selected image.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			selected_image
					.setImageBitmap(BitmapFactory.decodeFile(picturePath));

			Bitmap avatar = shrinkBitmap(picturePath, 300, 400);
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			avatar.compress(Bitmap.CompressFormat.JPEG, 100, bao);
			byte[] ba = bao.toByteArray();
			ba1 = Base64.encodeToString(ba, Base64.DEFAULT);
		}
	}

	/**
	 * Method which is called when an item with a click listener has been
	 * clicked.
	 */
	public void onClick(View v) {

		SharedPreferences preferences = getSharedPreferences("details",
				MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();

		// Logout

		if (v.getId() == R.id.logout_button) {

			editor.clear();
			editor.apply();

			alertDialog.dismiss();

			Intent myIntent = new Intent(this, LoginActivity.class);
			startActivityForResult(myIntent, 0);
			finish();
			System.out.println("Switching to Login Activity");
		}

		if (v.getId() == R.id.logout_cancel_button) {
			alertDialog.dismiss();
		}

		// Delete Account

		if (v.getId() == R.id.delete_account_button) {

			HashString md5 = new HashString();

			System.out.println("Delete Account");
			String passwordUnHashed = delete_account_text.getText().toString();

			try {
				String passwordHashed = md5.HashString(passwordUnHashed);

				if (passwordHashed.equals(password)) {
					state = 2;
					NetworkAvailability network = new NetworkAvailability();
					if (network.isNetworkAvailable(this)) {
						grabURL();
					} else {
						Toast.makeText(SettingsActivity.this,
								"No Internet Connection", Toast.LENGTH_SHORT)
								.show();
					}
				} else {
					Toast.makeText(SettingsActivity.this, "Invalid Password",
							Toast.LENGTH_SHORT).show();
					alertDialog.dismiss();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		// Location Status

		if (v.getId() == R.id.enable_location_status_button) {
			location_status = "1";
			state = 0;
			NetworkAvailability network = new NetworkAvailability();
			if (network.isNetworkAvailable(this)) {
				grabURL();
			} else {
				Toast.makeText(SettingsActivity.this, "No Internet Connection",
						Toast.LENGTH_SHORT).show();
			}
		}

		if (v.getId() == R.id.disable_location_status_button) {
			location_status = "0";
			state = 0;
			NetworkAvailability network = new NetworkAvailability();
			if (network.isNetworkAvailable(this)) {
				grabURL();
			} else {
				Toast.makeText(SettingsActivity.this, "No Internet Connection",
						Toast.LENGTH_SHORT).show();
			}
		}

		if (v.getId() == R.id.upload_avatar_button) {
			state = 3;
			NetworkAvailability network = new NetworkAvailability();
			if (network.isNetworkAvailable(SettingsActivity.this)) {
				grabURL();
			} else {
				Toast.makeText(SettingsActivity.this, "No Internet Connection",
						Toast.LENGTH_SHORT).show();
			}
			System.out.println("UPLOAD");
			alertDialog.dismiss();
		}

		if (v.getId() == R.id.choose_avatar_button) {
			Intent i = new Intent();
			i.setType("image/*");
			i.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(Intent.createChooser(i, "Select Avatar"),
					SELECT_PICTURE);
		}

	}

	/**
	 * Populate the settings menu array list.
	 */
	public void populateSettingsList() {
		settingsMenuItems.add("Location Status");
		settingsMenuItems.add("Set Avatar");
		settingsMenuItems.add("Logout");
		settingsMenuItems.add("Delete Account");

		populateList();
	}

	/**
	 * Populate the settings menu list view.
	 */
	public void populateList() {
		ListView settingsListView = (ListView) findViewById(R.id.settingsListView);

		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
				R.layout.menu_row, settingsMenuItems);

		settingsListView.setAdapter(listAdapter);

		settingsListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView parent, View v,
							int position, long id) {

						switch (position) {
						case 0:
							state = 0;
							createLocationStatusDialog();
							break;
						case 1:
							state = 3;
							uploadPictureDialog();
							break;
						case 2:
							state = 1;
							createLogoutDialog();
							break;
						case 3:
							state = 2;
							createDeleteAccountDialog();
							break;
						}
					}
				});
	}

	/**
	 * Create a dialog to allow picture upload.
	 */
	public void uploadPictureDialog() {
		alertDialog = new AlertDialog.Builder(SettingsActivity.this).create();
		View dialog_layout = getLayoutInflater().inflate(
				R.layout.upload_avatar_dialog, null);

		upload_avatar_button = (Button) dialog_layout
				.findViewById(R.id.upload_avatar_button);
		load_avatar_button = (Button) dialog_layout
				.findViewById(R.id.choose_avatar_button);
		selected_image = (ImageView) dialog_layout
				.findViewById(R.id.chosen_image);
		upload_avatar_button.setOnClickListener(this);
		load_avatar_button.setOnClickListener(this);

		alertDialog.setView(dialog_layout);
		alertDialog.setTitle("Upload Avatar");
		alertDialog.show();
	}

	/**
	 * Create a dialog that allows you to change your location status.
	 */
	public void createLocationStatusDialog() {

		SharedPreferences preferences = getSharedPreferences("details",
				MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();

		alertDialog = new AlertDialog.Builder(SettingsActivity.this).create();
		View dialog_layout = getLayoutInflater().inflate(
				R.layout.location_status_dialog, null);
		enable_location_status_button = (Button) dialog_layout
				.findViewById(R.id.enable_location_status_button);
		disable_location_status_button = (Button) dialog_layout
				.findViewById(R.id.disable_location_status_button);

		enable_location_status_button.setOnClickListener(this);
		disable_location_status_button.setOnClickListener(this);

		alertDialog.setView(dialog_layout);
		alertDialog.setTitle("Location Status");
		alertDialog.show();
	}

	/**
	 * Create a dialog for logging out.
	 */
	public void createLogoutDialog() {
		alertDialog = new AlertDialog.Builder(SettingsActivity.this).create();
		View dialog_layout = getLayoutInflater().inflate(
				R.layout.logout_dialog, null);

		logout_button = (Button) dialog_layout.findViewById(R.id.logout_button);
		logout_cancel_button = (Button) dialog_layout
				.findViewById(R.id.logout_cancel_button);
		logout_button.setOnClickListener(this);
		logout_cancel_button.setOnClickListener(this);

		alertDialog.setView(dialog_layout);
		alertDialog.setTitle("Logout");
		alertDialog.show();
	}

	/**
	 * Creat a delete account dialog.
	 */
	public void createDeleteAccountDialog() {
		alertDialog = new AlertDialog.Builder(SettingsActivity.this).create();
		View dialog_layout = getLayoutInflater().inflate(
				R.layout.delete_account_dialog, null);

		delete_account_button = (Button) dialog_layout
				.findViewById(R.id.delete_account_button);
		delete_account_text = (EditText) dialog_layout
				.findViewById(R.id.delete_account_text);
		delete_account_button.setOnClickListener(this);

		alertDialog.setView(dialog_layout);
		alertDialog.setTitle("Delete Account");
		alertDialog.show();
	}

	/**
	 * Method to shrink a bitmap to allow for uploading.
	 * 
	 * @param file
	 *            - File path,
	 * @param width
	 *            - Width to shrink to.
	 * @param height
	 *            - Height to shrink to.
	 * @return bitmap - Return the shrunken images.
	 */
	public Bitmap shrinkBitmap(String file, int width, int height) {
		BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
		bmpFactoryOptions.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);

		int heightRation = (int) Math.ceil(bmpFactoryOptions.outHeight
				/ (float) height);
		int widthRation = (int) Math.ceil(bmpFactoryOptions.outWidth
				/ (float) width);

		if (heightRation > 1 || widthRation > 1) {
			if (heightRation > widthRation) {
				bmpFactoryOptions.inSampleSize = 2;
			} else {
				bmpFactoryOptions.inSampleSize = 2;
			}
		}

		bmpFactoryOptions.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);

		return bitmap;
	}

	/**
	 * Method which is called upon the options menu being created.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_settings, menu);
		return true;
	}

	/**
	 * Parse the delete user XML.
	 * 
	 * @param xml
	 *            - XML to parse.
	 */
	public void parseDeleteUserXML(String xml) {

		XMLParser parser = new XMLParser();

		Document doc = parser.getDomElement(xml);

		NodeList nl = doc.getElementsByTagName(RSP);

		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);

			String status = e.getAttribute(STATUS);
			String failCode = e.getAttribute(CODE);

			String message = parser.getValue(e, MESSAGE);

			if (status.equals("ok")) {
				Toast.makeText(SettingsActivity.this, "Account Deleted",
						Toast.LENGTH_SHORT).show();
				alertDialog.dismiss();
				Intent myIntent = new Intent(this, LoginActivity.class);
				startActivityForResult(myIntent, 0);
				finish();
				System.out.println("Switching to Login Activity");
			} else {
				Toast.makeText(SettingsActivity.this, "Error Deleting Account",
						Toast.LENGTH_SHORT).show();
				alertDialog.dismiss();
			}

		}
	}

	/**
	 * Parse the update user status XML.
	 * 
	 * @param xml
	 *            - XML to parse.
	 */
	public void parseUpdateUserStatusXML(String xml) {

		XMLParser parser = new XMLParser();

		Document doc = parser.getDomElement(xml);

		NodeList nl = doc.getElementsByTagName(RSP);

		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);

			String status = e.getAttribute(STATUS);
			String failCode = e.getAttribute(CODE);

			String message = parser.getValue(e, MESSAGE);

			if (status.equals("ok")) {
				if (location_status.equals("1")) {
					Toast.makeText(SettingsActivity.this,
							"Location Status Enabled", Toast.LENGTH_SHORT)
							.show();
					alertDialog.dismiss();
				} else {
					Toast.makeText(SettingsActivity.this,
							"Location Status Disabled", Toast.LENGTH_SHORT)
							.show();
					alertDialog.dismiss();
				}
			}

		}
	}

	/**
	 * Parse the set avatar XML.
	 * 
	 * @param xml
	 *            - XML to parse.
	 */
	public void setAvatarParseXML(String xml) {
		XMLParser parser = new XMLParser();

		Document doc = parser.getDomElement(xml);

		NodeList nl = doc.getElementsByTagName(RSP);

		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);

			String status = e.getAttribute(STATUS);
			String failCode = e.getAttribute(CODE);

			String message = parser.getValue(e, MESSAGE);

			if (status.equals("ok")) {
				Toast.makeText(SettingsActivity.this, "Avatar Uploaded",
						Toast.LENGTH_SHORT).show();
				alertDialog.dismiss();
			} else {
				Toast.makeText(SettingsActivity.this, "Avatar Upload Failed",
						Toast.LENGTH_SHORT).show();
				alertDialog.dismiss();
			}

		}
	}

	public void grabURL() {
		new GrabURL().execute();
	}

	private class GrabURL extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			ServerQueries query = new ServerQueries();

			switch (state) {
			case 0:
				xml = query.queryUserStatus(userID, location_status, password);
				break;
			case 1:
				// Logout
				break;
			case 2:
				xml = query.deleteUser(userID, password);
				break;
			case 3:
				// AVATAR STUFF
				xml = query.uploadAvatar(userID, password, ba1);
				break;

			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			switch (state) {
			case 0:
				parseUpdateUserStatusXML(xml);
				state = 0;
				break;
			case 1:
				// Logout
				state = 0;
				break;
			case 2:
				parseDeleteUserXML(xml);
				state = 0;
				break;
			case 3:
				// AVATAR STUFF
				setAvatarParseXML(xml);
				state = 0;
				break;
			}

		}

	}

}
