package org.imdragon.sbccgroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class UpcomingEventsFragment extends Fragment {
	private ArrayList<String> upcomingEvents = new ArrayList<String>();
	private ArrayList<JSONObject> upcomingEventsJson = new ArrayList<JSONObject>();
	private ArrayAdapter<String> nEventAdapter;
	private ListView nEventView;
	private View rootView;
	private String EVENTS_URL = "http://pieapi.us.to:3000/events.json";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_upcoming_events,
				container, false);
		loadEventsFromAPI(EVENTS_URL);
		
		return rootView;
	}
	

	private void loadEventsFromAPI(String url) {
		EventswipeActivity activity = (EventswipeActivity) getActivity();
	    GetEventsTask task = new GetEventsTask(activity);
	    task.setMessageLoading("Loading events data...");
	    task.execute(url);
	}
	
	private class GetEventsTask extends GetTask {
	    public GetEventsTask(Context context) {
	        super(context);
	    }

	    @Override
        protected void onPostExecute(JSONObject json) {
            try {
            	// put events in lists
            	try {
        			// sort events
        			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy",
        					Locale.US);
        			JSONArray events;
        			events = json.getJSONArray("data");

        			JSONObject event;
        			Date eventDate;
        			Calendar cal = Calendar.getInstance();
        			Calendar eventCal = Calendar.getInstance();
        			int eventsLength = events.length();
        			for (int ndx = 0; ndx < eventsLength; ndx++) {
        				event = events.getJSONObject(ndx);
        				eventDate = sdf.parse(event.getString("date"));
        				eventCal.setTime(eventDate);
        				if (cal.equals(eventCal) || cal.before(eventCal)) {
        					Log.v("ClientProtocol",
        							"Upcoming event: " + sdf.format(eventDate));
        					upcomingEvents.add(event.getString("title")+
        							"  ("+event.getString("date")+")");
        					upcomingEventsJson.add(event);
        				} 
        			}
        			
        			// set up list
        			nEventAdapter = new ArrayAdapter<String>(getActivity(),
        					android.R.layout.simple_list_item_1, upcomingEvents);
        			nEventView = (ListView) rootView.findViewById(R.id.nextEventList);
        			nEventView.setAdapter(nEventAdapter);
        			
        			// create onitemclick listener
        			nEventView.setOnItemClickListener(new OnItemClickListener() {
			            @Override
			            public void onItemClick(AdapterView<?> parent, View view, int position,
			                    long id) {
							JSONObject eventJson = upcomingEventsJson.get(position);
			            	loadEventDetails(eventJson);
			            }
			        });
        		} catch (JSONException e) {
        			e.printStackTrace();
        		} catch (ParseException e) {
        			e.printStackTrace();
        		}
            } catch (Exception e) {
            	Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
	        } finally {
	            super.onPostExecute(json);
	        }
	    }
	}
	
	private void loadEventDetails(JSONObject eventJson) {
		EventswipeActivity activity = (EventswipeActivity) getActivity();
		Intent intent = new Intent(activity, ActivityDetails.class);
		intent.putExtra("json", eventJson.toString());
		startActivity(intent);
	}
}
