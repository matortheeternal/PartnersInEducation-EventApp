package org.imdragon.sbccgroup;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ActivityEvent extends ExpandableListActivity {
	private String events[] = { "Event 1", "Event 2",
		"Event3" };
	String yo = "hello";
//	private String events[] = getResources().getStringArray(R.array.sampleEvents);
	
	
/**
 * strings for child elements
 */
private String childElements[][] = {
		{ "Details1 A", "Details1 B"+yo, "Details1 C" },
		{ "Details2 A", "Details2 B", "Details2 C" },
		{ "Details3 A", "Details3 B", "Details3 C" },
		{ "Details4 A", "Details4 B", "Details4 C" },
		{ "Details5 A", "Details5 B", "Details5 C" },
		{ "Details6 A", "Details6 B", "Details6 C" }, { "Details7" },
		{ "Details8" }, { "Details9" } };

@Override
public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.article_screen);
	setListAdapter(new ExpandableListAdapter(this));
//	events = getResources().getStringArray(R.array.sampleEvents);
}

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
		yourSelection
				.setText(childElements[groupPosition][childPosition]);
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
}}
