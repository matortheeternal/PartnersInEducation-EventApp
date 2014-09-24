package org.imdragon.sbccgroup;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ActivityEvent extends ExpandableListActivity {
	
	String dist = "2.4km";
	String dist1 = "9.6km";
	String dist2 = "2.4km";
	Location myLocation, ellwood;
	float [] distance = new float[1];
	
//	private String events[] = { "Mr. Anderson's 4th Grade Science Fair"+ " " +distance[0],
//			"Career Day talk at DP High School",
//			"Kinder-Gardening at Ellwood Elementary" };

	/**
	 * strings for child elements
	 */
	private String childElements[][] = {
			{
					"Ellwood Elementary \nFriday, July 2, 2014 @ 4pm",
					"123 Hollister Ave, Goleta CA 93117",
					"Description: \tVolunteers will be assisting with 4th grade curriculum circuit boards and demonstrating how electricity works!" },
			{
					"Dos Pueblos High School \nTuesday July 15, 2014 @ 10am",
					"7414 Cathedral Oaks, Goleta CA 93117",
					"Description: Help high school students to find future career goals by sharing your passion." },
			{ "Ellwood Elementary \n Thursday, August 12, 2014",
					"123  Hollister Ave, Goleta CA 93117",
					"Description: Help young students understand the plant life cycle." },
			{ "Details4 A", "Details4 B", "Details4 C" },
			{ "Details5 A", "Details5 B", "Details5 C" },
			{ "Details6 A", "Details6 B", "Details6 C" }, { "Details7" },
			{ "Details8" }, { "Details9" } };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
//		myLocation.setLatitude(35.4302701);
//		myLocation.setLongitude(119.8939366);
//		ellwood.setLatitude(34.4302701);
//		ellwood.setLongitude(119.8939366);
		Location.distanceBetween(41.742964, -87.995971, 41.811511, -87.967923, distance);
//		Toast.makeText(this, String.valueOf(distance[0]), Toast.LENGTH_LONG).show();
		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.article_screen);
		setListAdapter(new ExpandableListAdapter(this));
		// events = getResources().getStringArray(R.array.sampleEvents);
	}
	private String events[] = { "Mr. Anderson's 4th Grade Science Fair"+ " - " +dist,
			"Career Day talk at DP High School"+ " - " +dist1,
			"Kinder-Gardening at Ellwood Elementary" + " - " +dist2};

	public class ExpandableListAdapter extends BaseExpandableListAdapter {
		private Context myContext;

		public ExpandableListAdapter(Context context) {
			myContext = context;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return null;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) myContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(
						R.layout.article_list_child_item_layout, null);
			}
			TextView yourSelection = (TextView) convertView
					.findViewById(R.id.articleContentTextView);
			yourSelection.setText(childElements[groupPosition][childPosition]);
			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return childElements[groupPosition].length;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return null;
		}

		@Override
		public int getGroupCount() {
			return events.length;
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) myContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(
						R.layout.article_list_item_layout, null);
			}
			TextView groupName = (TextView) convertView
					.findViewById(R.id.articleHeaderTextView);
			groupName.setText(events[groupPosition]);
			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}
	public void tocheckin (View v){
		Intent i = new Intent(getBaseContext(), CurrentEventActivity.class);
		startActivity(i);
	}
}
