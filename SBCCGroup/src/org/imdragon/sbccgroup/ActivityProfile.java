package org.imdragon.sbccgroup;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class ActivityProfile extends Activity {
	 Volunteer sampleVolunteer = new Volunteer("mgarcia", "123", "Mark", "Garcia", 12, 3, "Awesome", "3");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		new popVolunteer().execute(null,null,null);
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
	
	private class popVolunteer extends
	AsyncTask<Object, Integer, Exception> {

@Override
protected Exception doInBackground(Object... params) {
	try {

		return null;
	} catch (Exception ex) {
		return ex;
	}
}

@Override
protected void onPostExecute(Exception error) {

	TextView pheader = (TextView) findViewById(R.id.profileTop);
	pheader.setText(sampleVolunteer.getFirstName());
	TextView name = (TextView) findViewById(R.id.profileTop);
	name.setText(sampleVolunteer.getFirstName());
	StringBuilder sb = new StringBuilder();
	sb.append(sampleVolunteer.getFirstName());
	sb.append(" " +sampleVolunteer.getLastName());
	TextView namefield = (TextView) findViewById(R.id.textView6);
	namefield.setText(sb.toString());
	TextView hoursfield = (TextView) findViewById(R.id.textView7);
	hoursfield.setText(String.valueOf(sampleVolunteer.getTotalHours()));
	TextView eattended = (TextView) findViewById(R.id.textView8);
	eattended.setText(String.valueOf(sampleVolunteer.getTotalEvents()));
	TextView rankfield = (TextView) findViewById(R.id.textView9);
	rankfield.setText(String.valueOf(sampleVolunteer.getRank()));
	

	
}

}

	// END Google Play Services connection callbacks section //
	public void toEvents(View v) throws IOException {
		startActivity(new Intent(this, ActivityEvent.class));
	}

}
