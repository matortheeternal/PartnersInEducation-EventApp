package org.imdragon.sbccgroup;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.apache.commons.codec.binary.Base64;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	public Client client;
	public boolean connected;
	private String username;
	private Volunteer volunteer;
	private ArrayList<Event> events;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}
 Volunteer sampleVolunteer = new Volunteer("mgarcia", "123", "Mark", "Garcia", 12, 3, "Awesome", "3");
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

	private String getHashedToken() {
		EditText passwordEditText = (EditText) findViewById(R.id.passwordText);
		String password = passwordEditText.getText().toString();
		String text = username + password;

		// get a hash
		MessageDigest digest;
		byte[] hash = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			hash = digest.digest(text.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			// e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// e.printStackTrace();
		}

		return new String(Base64.encodeBase64(hash));
	}

	public void loginPressed(View v) {
//		new loginBackGround().execute(null, null, null);
//		if (volunteer == null)
//			Toast.makeText(this, "null volunteer", Toast.LENGTH_LONG).show();
		Intent i = new Intent(getBaseContext(), ActivityProfile.class);
		startActivity(i);
		
		
	}

	private class loginBackGround extends AsyncTask<Object, Integer, Exception> {

		@Override
		protected Exception doInBackground(Object... params) {

			EditText usernameEditText = (EditText) findViewById(R.id.usernameText);
			username = usernameEditText.getText().toString();

			String server = "localhost";
			int port = 1500;
			String token = getHashedToken();
			client = new Client(server, port, username, token,
					LoginActivity.this);
			if (!client.start()) {
				// login failed?
				return new Exception();
			}
			client.sendMessage(new QueryMessage(QueryMessage.LOGIN, token,
					username));
			client.sendMessage(new QueryMessage(QueryMessage.GETVOLUNTEER,
					token, username));
			volunteer = client.getVolunteer();
			events = client.getEvents();
			connected = client.getConnected();
			return new Exception();

		}
	}

//	public void toast(String msg) {
//		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
//	}

}
