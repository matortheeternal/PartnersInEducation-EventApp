package org.imdragon.sbccgroup;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.EditText;
import android.widget.Toast;

import com.savagelook.android.UrlJsonAsyncTask;

public class LoginActivity extends Activity {
	private String mUsername;
	private String mPassword;
	private String csrf_token;
	private SharedPreferences mPreferences;
	private static final String LOGIN_API_STARTPOINT_URL = "http://pieapi.us.to:3000/signin";
	private final static String LOGIN_API_ENDPOINT_URL = "http://pieapi.us.to:3000/sessions.json";
	public boolean connected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		CookieSyncManager.createInstance(LoginActivity.super);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void loginPressed(View v) {
		 EditText usernameField = (EditText) findViewById(R.id.usernameText);
		 mUsername = usernameField.getText().toString();
		 EditText passwordField = (EditText) findViewById(R.id.passwordText);
		 mPassword = passwordField.getText().toString();

		if (mUsername.length() == 0 || mPassword.length() == 0) {
			// input fields are empty
			Toast.makeText(this, "Username/password cannot be blank",
					Toast.LENGTH_LONG).show();
			return;
		} else {
			LoginTask loginTask = new LoginTask(LoginActivity.this);
			loginTask.setMessageLoading("Logging in...");
			loginTask.execute(LOGIN_API_ENDPOINT_URL, LOGIN_API_STARTPOINT_URL);
		}
	}

	private class LoginTask extends UrlJsonAsyncTask {
		private List<Cookie> cookies;

		public LoginTask(Context context) {
			super(context);
		}

		@Override
		protected JSONObject doInBackground(String... urls) {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(urls[0]);
			HttpGet get = new HttpGet(urls[1]);
			JSONObject holder = new JSONObject();
			JSONObject userObj = new JSONObject();
			String response = null;
			String authField = "name=\"authenticity_token\" type=\"hidden\" value=";
			JSONObject json = new JSONObject();

			this.setConnectionParams(4000, 0, 1);

			try {
				try {
					// get the authenticity token from the signin page
					get.setHeader("Accept", "text/html");
					ResponseHandler<String> responseHandler = new BasicResponseHandler();
					response = client.execute(get, responseHandler);
					csrf_token = response.substring(response.indexOf(authField)
							+ authField.length() + 1,
							response.indexOf(authField) + authField.length()
									+ 45);
					// Log.e("ClientProtocol", "CSRF token = "+csrf_token);

					// setup the returned values in case something goes wrong
					json.put("success", false);
					json.put("info", "Something went wrong. Retry!");
					// construct the JSON request params
					holder.put("commit", "Sign in");
					userObj.put("username", mUsername);
					userObj.put("password", mPassword);
					holder.put("session", userObj);
					holder.put("authenticity_token", csrf_token);
					holder.put("utf8", "&#x2713;");
					StringEntity se = new StringEntity(holder.toString());
					post.setEntity(se);

					// setup the request headers
					post.setHeader("Accept", "application/json");
					post.setHeader("Content-Type", "application/json");

					// send the JSON request
					responseHandler = new BasicResponseHandler();
					response = client.execute(post, responseHandler);
					json = new JSONObject(response);
					// Log.e("ClientProtocol",
					// "remember_token = "+json.getJSONObject("data").getString("remember_token"));

					// get all the cookies and sync them across the entire
					// application
					cookies = client.getCookieStore().getCookies();
					CookieSyncManager.getInstance().startSync();
					for (int i = 0; i < cookies.size(); i++) {
						Cookie c = cookies.get(i);
						CookieManager.getInstance().setCookie(c.getDomain(),
								c.getName() + "=" + c.getValue());
					}
					CookieSyncManager.getInstance().sync();
					CookieSyncManager.getInstance().stopSync();
				} catch (HttpResponseException e) {
					e.printStackTrace();
					Log.e("ClientProtocol", "" + e);
					json.put("info",
							"Email and/or password are invalid. Retry!");
				} catch (IOException e) {
					e.printStackTrace();
					Log.e("IO", "" + e);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Log.e("JSON", "" + e);
			}

			return json;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				if (json.getBoolean("success")) {
					// everything is ok
					SharedPreferences.Editor editor = mPreferences.edit();
					// save the returned auth_token into the SharedPreferences
					JSONObject data = json.getJSONObject("data");
					editor.putString("remember_token",
							data.getString("remember_token"));
					editor.putString("volunteer_id",
							data.getString("volunteer_id"));
					editor.commit();

					// launch the HomeActivity and close this one
					Intent intent = new Intent(getApplicationContext(),
							ActivityProfile.class);
					startActivity(intent);
					finish();
				}
				Toast.makeText(context, json.getString("info"),
						Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				// something went wrong: show a Toast with the exception message
				Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG)
						.show();
			} finally {
				super.onPostExecute(json);
			}
		}
	}

}