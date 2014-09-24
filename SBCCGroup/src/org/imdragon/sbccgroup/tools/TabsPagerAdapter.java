package org.imdragon.sbccgroup.tools;

import org.imdragon.sbccgroup.PastEventsFragment;
import org.imdragon.sbccgroup.UpcomingEventsFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			// Upcoming Events fragment activity
			return new UpcomingEventsFragment();
		case 1:
			// Past Events fragment activity
			return new PastEventsFragment();
		}

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 2;
	}

}
