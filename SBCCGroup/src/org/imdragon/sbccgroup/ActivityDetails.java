package org.imdragon.sbccgroup;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ActivityDetails extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// get the json from the intent
		JSONObject j = null;
		try {
			Intent intent = getIntent();
			String m = intent.getStringExtra("json");
			j = new JSONObject(m);
			Log.e("ClientProtocol", m);
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("JSON", "" + e);
		}
		
		setContentView(R.layout.activity_details);
		updateFields(j);
	}
  
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.event_details, menu);
		return true;
	}
	
	public void updateFields(JSONObject json) {
		try {
			TextView eventTitle = (TextView) findViewById(R.id.eventTitle);
			eventTitle.setText(json.getString("title"));
			TextView dateLine = (TextView) findViewById(R.id.dateLine);
			dateLine.setText(json.getString("date") + " " + json.getString("time"));
			TextView locationInfo = (TextView) findViewById(R.id.locationAndContact);
			locationInfo.setText(json.getString("location"));
			TextView description = (TextView) findViewById(R.id.description);
			description.setText(json.getString("description"));
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("JSON", "" + e);
		}
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
}
