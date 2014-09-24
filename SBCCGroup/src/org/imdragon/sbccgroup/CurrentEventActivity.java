package org.imdragon.sbccgroup;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
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

public class CurrentEventActivity extends Activity {


	private String stoppingTime = "";
	private String startingTime = "";
	private ToggleButton tgbutton;
	@SuppressWarnings("unused")
	private Button ebutton;
	private TextView thankstext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_current);

//		locationClient = new LocationClient(this, this, this);

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
//					new UpdateUIAsync().execute(null, 0, null);
					TextView startT = (TextView) findViewById(R.id.startTime);
					startT.setText(startingTime);

				} else {
					// Disable vibrate
					tgbutton.setVisibility(View.INVISIBLE);
					// tgbutton.setOnClickListener(null);
					// // tgbutton.setClickable(false);
					// ebutton.setVisibility(View.VISIBLE);
					thankstext.setVisibility(View.VISIBLE);
//					new UpdateUIAsync().execute(null, 1, null);
//					above killed when GPS taken out.  Ditto for If above
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

	

	public void loadProfile(View v) throws IOException {
		startActivity(new Intent(this, ActivityProfile.class));
	}

	public void toEvents(View v) throws IOException {
		// startActivity(new Intent(this, ActivityProfile.class));
		finish();

	}

	// Async to update the UI Lat and Long
	//  Killed when removed GPS stuff since it used current location for time info.  Can easily change to get time from device.
//	private class UpdateUIAsync extends AsyncTask<Object, Integer, Exception> {
//		int i;
//
//		protected Exception doInBackground(Object... params) {
//			// int i = (int) params[0];
//			i = (Integer) params[1];
//			try {
//				if (i == 0) {
//
//					startingTime = "Start time: "
//							+ String.valueOf(currentLocation.getTime());
//				}
//				if (i == 1) {
//
//					stoppingTime = "Stop time: "
//							+ String.valueOf(currentLocation.getTime());
//				}
//				return null;
//			} catch (Exception ex) {
//				return ex;
//			}
//		}
//
//		@Override
//		protected void onPostExecute(Exception error) {
//			TextView startT = (TextView) findViewById(R.id.startTime);
//			startT.setText(startingTime);
//			TextView stopT = (TextView) findViewById(R.id.stopTime);
//			stopT.setText(stoppingTime);
//			if (i == 1) {
//
//				// tgbutton.setVisibility(View.INVISIBLE);
//				// tgbutton.setOnClickListener(null);
//				// // tgbutton.setClickable(false);
//				// ebutton.setVisibility(View.VISIBLE);
//				// thankstext.setVisibility(View.VISIBLE);
//			}
//		}
//
//	}

}
