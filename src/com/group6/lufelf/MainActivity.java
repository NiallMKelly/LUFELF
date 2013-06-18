package com.group6.lufelf;

import java.util.ArrayList;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * This class handles the main menu.
 * 
 * @author Niall Kelly
 * @version 1.0.0
 */

public class MainActivity extends FragmentActivity implements OnClickListener {

	ArrayList<String> menuItems = new ArrayList<String>();

	/**
	 * Method called when the activitiy is created.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		populateMenuList();

	}

	/**
	 * Method called when an item with a click listeners has been clicked.
	 */
	@Override
	public void onClick(View v) {

	}

	/**
	 * Method called when the options menu has been created.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * Populate the list view with the menu items.
	 */
	public void populateList() {
		ListView mainListView = (ListView) findViewById(R.id.mainListView);

		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
				R.layout.menu_row, menuItems);

		mainListView.setAdapter(listAdapter);

		mainListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView parent, View v,
							int position, long id) {

						switch (position) {
						case 0:
							Intent mapsIntent = new Intent(v.getContext(),
									MapsActivity.class);
							startActivityForResult(mapsIntent, 0);
							System.out.println("Changing to Map");
							break;
						case 1:
							Intent friendsIntent = new Intent(v.getContext(),
									FriendsActivity.class);
							startActivityForResult(friendsIntent, 0);
							System.out.println("Changing to Friends");
							break;
						case 2:
							Intent messagesIntent = new Intent(v.getContext(),
									MessagesActivity.class);
							startActivityForResult(messagesIntent, 0);
							System.out.println("Changing to Messages");
							break;
						case 3:
							Intent locationsIntent = new Intent(v.getContext(),
									LocationsActivity.class);
							startActivityForResult(locationsIntent, 0);
							System.out.println("Changing to Locations");
							break;
						case 4:
							Intent eventsIntent = new Intent(v.getContext(),
									EventsActivity.class);
							startActivityForResult(eventsIntent, 0);
							System.out.println("Changing to Evenets");
							break;
						case 5:
							Intent settingsIntent = new Intent(v.getContext(),
									SettingsActivity.class);
							startActivityForResult(settingsIntent, 0);
							System.out.println("Changing to Settings");
							break;
						}

					}
				});
	}

	/**
	 * Add each menu item to an array list.
	 */
	public void populateMenuList() {
		menuItems.add("Map");
		menuItems.add("Friends");
		menuItems.add("Messages");
		menuItems.add("Locations");
		menuItems.add("Events");
		menuItems.add("Settings");

		populateList();
	}

}
