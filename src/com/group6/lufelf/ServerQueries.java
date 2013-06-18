package com.group6.lufelf;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 
 * This class is used to query the server using different PHP scripts.
 * 
 * @author Niall Kelly
 * @version 1.0.0
 * 
 */

public class ServerQueries {

	private static final int IO_BUFFER_SIZE = 4 * 1024;

	String xml = new String();

	public ServerQueries() {
		// BLANK CONSTRUCTOR
	}

	/**
	 * Method to Query login_user.php.
	 * 
	 * @param username
	 *            - username to login.
	 * @param password
	 *            - password relating to the username to login.
	 * @throws UnsupportedEncodingException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * 
	 * @return xml - The xml returned that needs to be parsed.
	 * 
	 */
	public String loginUser(String username, String password) {

		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost("http://148.88.32.47/login_user.php");

		try {

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("username", username));
			nameValuePairs.add(new BasicNameValuePair("password", password));

			// Url Encoding the POST parameters
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Making HTTP Request
			HttpResponse response = httpClient.execute(httpPost);

			HttpEntity httpEntity = response.getEntity();
			xml = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			// writing error to Log
			e.printStackTrace();

		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();

		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();
		}

		return xml;

	}

	/**
	 * Method to Query create_user.php.
	 * 
	 * @param name
	 *            - Name of the new user.
	 * @param lib_no
	 *            - Library card number of new user.
	 * @param username
	 *            - Username of new user.
	 * @param password
	 *            - Password of new user.
	 * @param dob
	 *            - Date Of Birth of new user.
	 * @param location_status
	 *            - Whether to show the users location or not.
	 * @param access_leve
	 *            - Set the new user to admin or not.
	 * @throws UnsupportedEncodingException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * 
	 * @return xml - The xml returned that needs to be parsed.
	 * 
	 */
	public String createUser(String name, String lib_no, String username,
			String password, String dob, String location_status,
			String access_level) {

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost("http://148.88.32.47/create_user.php");

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("name", name));
			nameValuePairs.add(new BasicNameValuePair("lib_no", lib_no));
			nameValuePairs.add(new BasicNameValuePair("username", username));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			nameValuePairs.add(new BasicNameValuePair("dob", dob));
			nameValuePairs.add(new BasicNameValuePair("location_status", "1"));
			nameValuePairs.add(new BasicNameValuePair("access_level", "1"));

			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post request
			HttpResponse response = httpclient.execute(httpPost);

			HttpEntity httpEntity = response.getEntity();
			xml = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			// writing error to Log
			e.printStackTrace();

		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();

		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();
		}

		return xml;
	}

	/**
	 * Method to Query querry_user_deetails.php.
	 * 
	 * @param username
	 *            - username of user to query.
	 * @throws UnsupportedEncodingException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * 
	 * @return xml - The xml returned that needs to be parsed.
	 * 
	 */
	public String queryUserDetails(String username) {

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(
				"http://148.88.32.47/querry_user_deatails.php");

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("username", username));

			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post request
			HttpResponse response = httpclient.execute(httpPost);

			HttpEntity httpEntity = response.getEntity();
			xml = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			// writing error to Log
			e.printStackTrace();

		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();

		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();
		}

		return xml;
	}

	/**
	 * Method to Query delete_user.php.
	 * 
	 * @param user_id
	 *            - User id of user to delete.
	 * @param password
	 *            - MD5 hashed password of user to delete.
	 * @throws UnsupportedEncodingException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * 
	 * @return xml - The xml returned that needs to be parsed.
	 * 
	 */
	public String deleteUser(String user_id, String password) {

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost("http://148.88.32.47/delete_user.php");

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
			nameValuePairs.add(new BasicNameValuePair("password", password));

			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post request
			HttpResponse response = httpclient.execute(httpPost);

			HttpEntity httpEntity = response.getEntity();
			xml = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			// writing error to Log
			e.printStackTrace();

		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();

		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();
		}

		return xml;
	}

	/**
	 * Method to Query make_friends.php.
	 * 
	 * @param user_id1
	 *            - user_id of instigator.
	 * @param user_id2
	 *            - user_id of receiver.
	 * @param password
	 *            - MD5 hashed password of instigator.
	 * @throws UnsupportedEncodingException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * 
	 * @return xml - The xml returned that needs to be parsed.
	 * 
	 */
	public String makeFriends(String user_id1, String user_id2, String password) {

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost("http://148.88.32.47/make_friends.php");

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("user_id1", user_id1));
			nameValuePairs.add(new BasicNameValuePair("user_id2", user_id2));
			nameValuePairs.add(new BasicNameValuePair("password", password));

			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post request
			HttpResponse response = httpclient.execute(httpPost);

			HttpEntity httpEntity = response.getEntity();
			xml = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			// writing error to Log
			e.printStackTrace();

		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();

		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();
		}

		return xml;
	}

	/**
	 * Method to Query delete_friend.php.
	 * 
	 * @param user_id1
	 *            - user_id of you.
	 * @param user_id2
	 *            - user_id of friend you wish to delete.
	 * @param password
	 *            - MD5 hashed password of you.
	 * @throws UnsupportedEncodingException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * 
	 * @return xml - The xml returned that needs to be parsed.
	 * 
	 */
	public String deleteFriend(String user_id1, String user_id2, String password) {

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(
				"http://148.88.32.47/delete_friend.php");

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("user_id1", user_id1));
			nameValuePairs.add(new BasicNameValuePair("user_id2", user_id2));
			nameValuePairs.add(new BasicNameValuePair("password", password));

			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post request
			HttpResponse response = httpclient.execute(httpPost);

			HttpEntity httpEntity = response.getEntity();
			xml = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			// writing error to Log
			e.printStackTrace();

		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();

		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();
		}

		return xml;
	}

	/**
	 * Method to Query get_friend_requests.php.
	 * 
	 * @param user_id
	 *            - user_id of you.
	 * @param password
	 *            - MD5 hashed password of you.
	 * @throws UnsupportedEncodingException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * 
	 * @return xml - The xml returned that needs to be parsed.
	 * 
	 */
	public String getFriendRequests(String user_id, String password) {

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(
				"http://148.88.32.47/get_friend_requests.php");

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
			nameValuePairs.add(new BasicNameValuePair("password", password));

			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post request
			HttpResponse response = httpclient.execute(httpPost);

			HttpEntity httpEntity = response.getEntity();
			xml = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			// writing error to Log
			e.printStackTrace();

		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();

		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();
		}

		return xml;
	}

	/**
	 * Method to Query friend_handshake.php.
	 * 
	 * @param request_id
	 *            - id of friend request.
	 * @param user_id1
	 *            - Your user_id.
	 * @param user_id2
	 *            - Friends user_id.
	 * @param password
	 *            - MD5 hashed password of you.
	 * @param status
	 *            - 1 Accepts Request or 2 to decline.
	 * @throws UnsupportedEncodingException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * 
	 * @return xml - The xml returned that needs to be parsed.
	 * 
	 */
	public String friendHandshake(String request_id, String user_id1,
			String user_id2, String password, String status) {

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(
				"http://148.88.32.47/friend_handshake.php");

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs
					.add(new BasicNameValuePair("request_id", request_id));
			nameValuePairs.add(new BasicNameValuePair("user_id1", user_id1));
			nameValuePairs.add(new BasicNameValuePair("user_id2", user_id2));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			nameValuePairs.add(new BasicNameValuePair("status", status));

			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post request
			HttpResponse response = httpclient.execute(httpPost);

			HttpEntity httpEntity = response.getEntity();
			xml = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			// writing error to Log
			e.printStackTrace();

		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();

		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();
		}

		return xml;
	}

	/**
	 * Method to Query query_friend_list.php.
	 * 
	 * @param user_id
	 *            - Your user_id.
	 * @param password
	 *            - MD5 hashed password of you.
	 * @throws UnsupportedEncodingException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * 
	 * @return xml - The xml returned that needs to be parsed.
	 * 
	 */
	public String queryFriendList(String user_id, String password) {
		String xml = null;

		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(
				"http://148.88.32.47/query_friend_list.php");

		try {

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
			nameValuePairs.add(new BasicNameValuePair("password", password));

			// Url Encoding the POST parameters
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Making HTTP Request
			HttpResponse response = httpClient.execute(httpPost);

			HttpEntity httpEntity = response.getEntity();
			xml = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			// writing error to Log
			e.printStackTrace();

		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();

		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();
		}
		return xml;
	}

	/**
	 * Method to Query update_user_status.php.
	 * 
	 * @param user_id
	 *            - Your user_id.
	 * @param lat
	 *            - Your latitude value.
	 * @param long - You longitude value,
	 * @param password
	 *            - MD5 hashed password of you.
	 * @throws UnsupportedEncodingException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * 
	 * @return xml - The xml returned that needs to be parsed.
	 * 
	 */
	public String updateUserStatus(String user_id, String lat, String lon,
			String password) {

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(
				"http://148.88.32.47/update_user_status.php");

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
			nameValuePairs.add(new BasicNameValuePair("lat", lat));
			nameValuePairs.add(new BasicNameValuePair("lon", lon));
			nameValuePairs.add(new BasicNameValuePair("password", password));

			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post request
			HttpResponse response = httpclient.execute(httpPost);

			HttpEntity httpEntity = response.getEntity();
			xml = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			// writing error to Log
			e.printStackTrace();

		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();

		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();
		}

		return xml;
	}

	/**
	 * Method to Query query_user_status.php.
	 * 
	 * @param user_id
	 *            - Your user id.
	 * @param location_status
	 *            - 1 location is public or 0 is private.
	 * @param password
	 *            - MD5 hashed password of you.
	 * @throws UnsupportedEncodingException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * 
	 * @return xml - The xml returned that needs to be parsed.
	 * 
	 */
	public String queryUserStatus(String user_id, String location_status,
			String password) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(
				"http://148.88.32.47/query_user_status.php");

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
			nameValuePairs.add(new BasicNameValuePair("location_status",
					location_status));
			nameValuePairs.add(new BasicNameValuePair("password", password));

			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post request
			HttpResponse response = httpclient.execute(httpPost);

			HttpEntity httpEntity = response.getEntity();
			xml = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			// writing error to Log
			e.printStackTrace();

		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();

		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();
		}

		return xml;
	}

	/**
	 * Method to Query query_event_list.php.
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * 
	 * @return xml - The xml returned that needs to be parsed.
	 * 
	 */
	public String queryEventList() {

		String xml = null;

		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(
				"http://148.88.32.47/query_event_list.php");

		try {

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			// Url Encoding the POST parameters
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Making HTTP Request
			HttpResponse response = httpClient.execute(httpPost);

			HttpEntity httpEntity = response.getEntity();
			xml = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			// writing error to Log
			e.printStackTrace();

		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();

		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();
		}

		return xml;
	}

	/**
	 * Method to Query create_event.php.
	 * 
	 * @param name
	 *            - Name of the event to create.
	 * @param date
	 *            - Date of the event you wish to create.
	 * @param place_id
	 *            - id of place to have the
	 * @param user_id
	 *            - Your user id.
	 * @param password
	 *            - MD5 hashed password of you.
	 * @throws UnsupportedEncodingException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * 
	 * @return xml - The xml returned that needs to be parsed.
	 * 
	 */
	public String createEvent(String name, String date, String place_id,
			String user_id, String password) {

		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost("http://148.88.32.47/create_event.php");

		try {

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("event_name", name));
			nameValuePairs.add(new BasicNameValuePair("event_date", date));
			nameValuePairs.add(new BasicNameValuePair("place_id", place_id));
			nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
			nameValuePairs.add(new BasicNameValuePair("password", password));

			// Url Encoding the POST parameters
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Making HTTP Request
			HttpResponse response = httpClient.execute(httpPost);

			HttpEntity httpEntity = response.getEntity();
			xml = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			// writing error to Log
			e.printStackTrace();

		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();

		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();
		}

		return xml;
	}

	/**
	 * Method to Query query_event_details.php.
	 * 
	 * @param event_id
	 *            - id of event to get details of.
	 * @throws UnsupportedEncodingException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * 
	 * @return xml - The xml returned that needs to be parsed.
	 * 
	 */
	public String queryEventDetails(String event_id) {

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(
				"http://148.88.32.47/query_event_details.php");

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("event_id", event_id));

			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post request
			HttpResponse response = httpclient.execute(httpPost);

			HttpEntity httpEntity = response.getEntity();
			xml = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			// writing error to Log
			e.printStackTrace();

		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();

		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();
		}

		return xml;
	}

	/**
	 * Method to Query delete_event.php.
	 * 
	 * @param user_id
	 *            - Your user_id.
	 * @param event_id
	 *            - The id of the event to delete.
	 * @param password
	 *            - MD5 hashed password of you.
	 * @throws UnsupportedEncodingException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * 
	 * @return xml - The xml returned that needs to be parsed.
	 * 
	 */
	public String deleteEvent(String user_id, String event_id, String password) {

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost("http://148.88.32.47/delete_event.php");

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
			nameValuePairs.add(new BasicNameValuePair("event_id", event_id));
			nameValuePairs.add(new BasicNameValuePair("password", password));

			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post request
			HttpResponse response = httpclient.execute(httpPost);

			HttpEntity httpEntity = response.getEntity();
			xml = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			// writing error to Log
			e.printStackTrace();

		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();

		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();
		}

		return xml;
	}

	/**
	 * Method to Query attend_event.php.
	 * 
	 * @param user_id
	 *            - Your user_id.
	 * @param event_id
	 *            - ID of event to attend.
	 * @param password
	 *            - MD5 hashed password of you.
	 * @throws UnsupportedEncodingException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * 
	 * @return xml - The xml returned that needs to be parsed.
	 * 
	 */
	public String attendEvent(String user_id, String event_id, String password) {

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost("http://148.88.32.47/attend_event.php");

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
			nameValuePairs.add(new BasicNameValuePair("event_id", event_id));
			nameValuePairs.add(new BasicNameValuePair("password", password));

			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post request
			HttpResponse response = httpclient.execute(httpPost);

			HttpEntity httpEntity = response.getEntity();
			xml = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			// writing error to Log
			e.printStackTrace();

		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();

		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();
		}

		return xml;
	}

	/**
	 * Method to Query create_place.php.
	 * 
	 * @param place_name
	 *            - Name of place to create.
	 * @param place_address
	 *            - Address of place to create.
	 * @param place_lat
	 *            - Latitude of place you wish to create.
	 * @param place_lon
	 *            - Longitude of place you wish to create.
	 * @param user_id
	 *            - Your user_id.
	 * @param password
	 *            - MD5 hashed password of you.
	 * @throws UnsupportedEncodingException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * 
	 * @return xml - The xml returned that needs to be parsed.
	 * 
	 */
	public String createPlace(String place_name, String place_address,
			String place_lat, String place_lon, String user_id, String password) {

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost("http://148.88.32.47/create_place.php");

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs
					.add(new BasicNameValuePair("place_name", place_name));
			nameValuePairs.add(new BasicNameValuePair("place_address",
					place_address));
			nameValuePairs.add(new BasicNameValuePair("place_lat", place_lat));
			nameValuePairs.add(new BasicNameValuePair("place_lon", place_lon));
			nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
			nameValuePairs.add(new BasicNameValuePair("password", password));

			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post request
			HttpResponse response = httpclient.execute(httpPost);

			HttpEntity httpEntity = response.getEntity();
			xml = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			// writing error to Log
			e.printStackTrace();

		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();

		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();
		}

		return xml;
	}

	/**
	 * Method to Query query_place_list.php.
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * 
	 * @return xml - The xml returned that needs to be parsed.
	 * 
	 */
	public String queryPlaceList() {

		String xml = null;

		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(
				"http://148.88.32.47/query_place_list.php");

		try {

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			// Url Encoding the POST parameters
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Making HTTP Request
			HttpResponse response = httpClient.execute(httpPost);

			HttpEntity httpEntity = response.getEntity();
			xml = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			// writing error to Log
			e.printStackTrace();

		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();

		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();
		}

		return xml;

	}

	/**
	 * Method to Query query_received_messages.php.
	 * 
	 * @param user_id
	 *            - User's ID
	 * @param password
	 *            - Hashed Password
	 * @throws UnsupportedEncodingException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * 
	 * @return xml - The xml returned that needs to be parsed.
	 * 
	 */
	public String queryReceivedMessages(String user_id, String password) {

		String xml = null;

		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(
				"http://148.88.32.47/query_received_messages.php");

		try {

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
			nameValuePairs.add(new BasicNameValuePair("password", password));

			// Url Encoding the POST parameters
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Making HTTP Request
			HttpResponse response = httpClient.execute(httpPost);

			HttpEntity httpEntity = response.getEntity();
			xml = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			// writing error to Log
			e.printStackTrace();

		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();

		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();
		}

		return xml;

	}

	/**
	 * Method to Query query_sent_messages.php.
	 * 
	 * @param user_id
	 *            - User's ID
	 * @param password
	 *            - Hashed Password
	 * @throws UnsupportedEncodingException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * 
	 * @return xml - The xml returned that needs to be parsed.
	 * 
	 */
	public String querySentMessages(String user_id, String password) {

		String xml = null;

		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(
				"http://148.88.32.47/query_sent_messages.php");

		try {

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
			nameValuePairs.add(new BasicNameValuePair("password", password));

			// Url Encoding the POST parameters
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Making HTTP Request
			HttpResponse response = httpClient.execute(httpPost);

			HttpEntity httpEntity = response.getEntity();
			xml = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			// writing error to Log
			e.printStackTrace();

		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();

		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();
		}

		return xml;

	}

	/**
	 * Method to Query send_message.php.
	 * 
	 * @param user_id
	 *            - User's ID
	 * @param password
	 *            - Hashed Password
	 * @param user_id_to
	 *            - User ID of user you wish to send message to.
	 * @param message
	 *            - The Message you wish to send.
	 * @throws UnsupportedEncodingException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * 
	 * @return xml - The xml returned that needs to be parsed.
	 * 
	 */
	public String sendMessage(String user_id, String password,
			String user_id_to, String message) {

		String xml = null;

		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost("http://148.88.32.47/send_message.php");

		try {

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("user_id_from", user_id));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			nameValuePairs
					.add(new BasicNameValuePair("user_id_to", user_id_to));
			nameValuePairs.add(new BasicNameValuePair("message", message));

			// Url Encoding the POST parameters
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Making HTTP Request
			HttpResponse response = httpClient.execute(httpPost);

			HttpEntity httpEntity = response.getEntity();
			xml = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			// writing error to Log
			e.printStackTrace();

		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();

		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();
		}

		return xml;

	}

	/**
	 * Method to Query upload_picture.php.
	 * 
	 * @param user_id
	 *            - User's ID
	 * @param password
	 *            - Hashed Password
	 * @param image
	 *            - Image in BASE64 Encoded.
	 * @throws UnsupportedEncodingException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * 
	 * @return xml - The xml returned that needs to be parsed.
	 * 
	 */
	public String uploadAvatar(String user_id, String password, String image) {

		String xml = null;

		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(
				"http://148.88.32.47/upload_picture.php");

		try {

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			nameValuePairs.add(new BasicNameValuePair("image", image));

			// Url Encoding the POST parameters
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Making HTTP Request
			HttpResponse response = httpClient.execute(httpPost);

			HttpEntity httpEntity = response.getEntity();
			xml = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			// writing error to Log
			e.printStackTrace();

		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();

		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();
		}

		return xml;
	}

	/**
	 * Method to Query avatars folder.
	 * 
	 * @param user_id
	 *            - User's ID
	 * @throws UnsupportedEncodingException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * 
	 * @return bitmap - The bitmap of the returned image.
	 * 
	 */
	public Bitmap getAvatar(String user_id) {

		Bitmap bitmap = null;

		try {
			bitmap = BitmapFactory.decodeStream((InputStream) new URL(
					"http://148.88.32.47/avatars/" + user_id + ".jpg")
					.getContent());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bitmap;
	}

}
