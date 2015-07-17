package hcim.auric.record;

import hcim.auric.data.SettingsPreferences;
import hcim.auric.record.events.EventRecorder;
import hcim.auric.record.screencast.ScreencastRecorder;
import hcim.auric.utils.HeterogeneityManager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public class RecorderManager {

	public static final String SCREENCAST_ROOT = "Screencast (requires root)";
	public static final String EVENT_BASED = "Event Based";
	public static final String DEFAULT = EVENT_BASED;

	private static List<String> recorders;

	static {
		recorders = new ArrayList<String>();
		recorders.add(EVENT_BASED);
		if (HeterogeneityManager.isRooted())
			recorders.add(SCREENCAST_ROOT);
	}

	public static IRecorder getSelectedRecorder(Context context) {
		SettingsPreferences s = new SettingsPreferences(context);
		String type = s.getRecorderType();
		if (type != null) {
			if (type.equals(SCREENCAST_ROOT))
				return new ScreencastRecorder(context);

			if (type.equals(EVENT_BASED))
				return new EventRecorder(context);
		}
		return null;
	}

	public static String getDefault() {
		return DEFAULT;
	}

	public static List<String> getTypesOfRecorders() {
		return recorders;
	}
}
