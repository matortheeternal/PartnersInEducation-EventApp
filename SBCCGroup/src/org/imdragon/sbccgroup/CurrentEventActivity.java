package org.imdragon.sbccgroup;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;

public class CurrentEventActivity extends Activity implements
		OnMyLocationButtonClickListener, LocationListener,
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener,
		com.google.android.gms.location.LocationListener {

	private LocationClient locationClient;
	private Location currentLocation;
	private String stoppingTime = "";
	private String startingTime = "";
	private ToggleButton tgbutton;
	@SuppressWarnings("unused")
	private Button ebutton;
	private TextView thankstext;
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(5000) // 5 seconds
			.setFastestInterval(2000) // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_current);

		locationClient = new LocationClient(this, this, this);

		tgbutton = (ToggleButton) findViewById(R.id.checkinToggle);
		ebutton = (Button) findViewById(R.id.toEvent);
		thankstext = (TextView) findViewById(R.id.thankyou);
		tgbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// Is the toggle on?
				boolean on = ((ToggleButton) v).isChecked();

				if (on) {
					// Toast.makeText(CurrentEventActivity.this,
					// String.valueOf(currentLocation.getTime()),
					// Toast.LENGTH_LONG).show();
					new UpdateUIAsync().execute(null, 0, null);
					TextView startT = (TextView) findViewById(R.id.startTime);
					startT.setText(startingTime);

				} else {
					// Disable vibrate
					tgbutton.setVisibility(View.INVISIBLE);
					// tgbutton.setOnClickListener(null);
					// // tgbutton.setClickable(false);
					// ebutton.setVisibility(View.VISIBLE);
					thankstext.setVisibility(View.VISIBLE);
					new UpdateUIAsync().execute(null, 1, null);
					Toast.makeText(CurrentEventActivity.this, "Thank you!",
							Toast.LENGTH_LONG).show();
					 new CountDownTimer(3000, 1000) {

					     public void onTick(long millisUntilFinished) {
					         thankstext.setText("Thank you!\nReturning to events in: " + millisUntilFinished / 1000);
					     }

					     public void onFinish() {

					     }
					  }.start();
					Thread background = new Thread() {
						public void run() {

							try {
								// Thread will sleep for 5 seconds
								sleep(3 * 1000);

								finish();

							} catch (Exception e) {

							}
						}
					};

					// start thread
					background.start();
				}

			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onStart() {
		locationClient.connect();
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		locationClient.connect();

	}

	@Override
	protected void onPause() {
		locationClient.disconnect();
		super.onPause();
	}

	@Override
	protected void onStop() {
		locationClient.disconnect();
		super.onStop();
	};

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

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onMyLocationButtonClick() {
		// TODO Auto-generated method stub
		return false;
	}

	public void loadProfile(View v) throws IOException {
		startActivity(new Intent(this, ActivityProfile.class));
	}

	public void toEvents(View v) throws IOException {
		// startActivity(new Intent(this, ActivityProfile.class));
		finish();

	}

	// Async to update the UI Lat and Long
	private class UpdateUIAsync extends AsyncTask<Object, Integer, Exception> {
		int i;

		protected Exception doInBackground(Object... params) {
			// int i = (int) params[0];
			i = (Integer) params[1];
			try {
				if (i == 0) {

					startingTime = "Start time: "
							+ String.valueOf(currentLocation.getTime());
				}
				if (i == 1) {

					stoppingTime = "Stop time: "
							+ String.valueOf(currentLocation.getTime());
				}
				return null;
			} catch (Exception ex) {
				return ex;
			}
		}

		@Override
		protected void onPostExecute(Exception error) {
			TextView startT = (TextView) findViewById(R.id.startTime);
			startT.setText(startingTime);
			TextView stopT = (TextView) findViewById(R.id.stopTime);
			stopT.setText(stoppingTime);
			if (i == 1) {

				// tgbutton.setVisibility(View.INVISIBLE);
				// tgbutton.setOnClickListener(null);
				// // tgbutton.setClickable(false);
				// ebutton.setVisibility(View.VISIBLE);
				// thankstext.setVisibility(View.VISIBLE);
			}
		}

	}

	// START Google Play Services connection callbacks section //
	// Need this for GPS services to work or else crash! //
	@Override
	public void onConnected(Bundle connectionHint) {
		currentLocation = locationClient.getLastLocation();
		locationClient.requestLocationUpdates(REQUEST, this); // LocationListener

	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Disconnected. Please re-connect.",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (result.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				result.startResolutionForResult(this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();

			}
		}

	}

}
