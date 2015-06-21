package hcim.auric.strategy;

import java.util.ArrayList;
import java.util.List;

public class StrategyManager {

	public static final String DEVICE_SHARING = "Device Sharing + records intruders";
	public static final String DEVICE_SHARING_RECORD_ALL = "Device Sharing + records everything";
	public static final String CHECK_ONCE = "Check Once";
	private static final String DEFAULT = DEVICE_SHARING_RECORD_ALL;

	private static List<String> strategies;

	static {
		strategies = new ArrayList<String>();
		
		strategies.add(DEVICE_SHARING);
		strategies.add(DEVICE_SHARING_RECORD_ALL);
		strategies.add(CHECK_ONCE);
	}

	public static String getDefault() {
		return DEFAULT;
	}

	public static List<String> getTypesOfStrategies() {
		return strategies;
	}
}
