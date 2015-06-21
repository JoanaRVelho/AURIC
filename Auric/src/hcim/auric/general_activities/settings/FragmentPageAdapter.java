package hcim.auric.general_activities.settings;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class FragmentPageAdapter extends FragmentPagerAdapter {
	private Fragment[] fragments;
	private final int MAX = 2;

	public FragmentPageAdapter(FragmentManager fm) {
		super(fm);
		fragments = new Fragment[MAX];
		fragments[0] = new GeneralFragment(); 
		fragments[1] = new FaceRecognitionFragment();
	}

	@Override
	public Fragment getItem(int i) {
		return fragments[i];
	}

	public String getDescription(int i) {
		switch (i) {
		case 0:
			return "General";
		case 1:
			return "Face Recognition";
		default:
			break;
		}
		return "";
	}

	@Override
	public int getCount() {
		return MAX;
	}

}
