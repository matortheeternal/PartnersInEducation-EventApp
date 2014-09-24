package org.imdragon.sbccgroup;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityProfile extends Activity {
	private SharedPreferences mPreferences;
	private String VOL_URL = "http://pieapi.us.to:3000/vols/";
	private String volunteer_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
		volunteer_id = mPreferences.getString("volunteer_id", "");
		if (volunteer_id.length() != 0)
			loadVolunteerFromAPI(VOL_URL + volunteer_id + ".json");
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
	
	private void loadVolunteerFromAPI(String url) {
	    GetVolunteerTask task = new GetVolunteerTask(ActivityProfile.this);
	    task.setMessageLoading("Loading volunteer data...");
	    task.execute(url);
	}
	
	private class GetVolunteerTask extends GetTask {
	    public GetVolunteerTask(Context context) {
	        super(context);
	    }

	    @Override
        protected void onPostExecute(JSONObject json) {
            try {
            	// set header to Preferred Name
    			TextView pheader = (TextView) findViewById(R.id.profileTop);
    			pheader.setText(json.getString("NamePreferred"));
    			
    			// build name string and put it in the Name field
    			StringBuilder sb = new StringBuilder();
    			sb.append(json.getString("NameFirst"));
    			sb.append(" " + json.getString("NameLast"));
    			TextView namefield = (TextView) findViewById(R.id.textView6);
    			namefield.setText(sb.toString());
    			
    			// set other fields to empty values because
    			// these fields don't exist in the table yet
    			TextView hoursfield = (TextView) findViewById(R.id.textView7);
    			hoursfield.setText("0");
    			TextView eattended = (TextView) findViewById(R.id.textView8);
    			eattended.setText("0");
    			TextView rankfield = (TextView) findViewById(R.id.textView9);
    			rankfield.setText("0");
            } catch (Exception e) {
            	Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
	        } finally {
	            super.onPostExecute(json);
	        }
	    }
	}
	
	// END Google Play Services connection callbacks section //
	
	public void toEvents(View v) {
		startActivity(new Intent(this, EventswipeActivity.class));
	}

}
