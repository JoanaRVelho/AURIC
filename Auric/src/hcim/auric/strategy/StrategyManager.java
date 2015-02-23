package hcim.auric.strategy;

import java.util.ArrayList;
import java.util.List;

public class StrategyManager {

	public static final String DEVICE_SHARING = "Device Sharing";
	public static final String VERBOSE_DEVICE_SHARING = "Device Sharing Verbose";
	public static final String CHECK_ONCE = "Check Once";
	private static final String DEFAULT = VERBOSE_DEVICE_SHARING;

	private static List<String> strategies;

	static {
		strategies = new ArrayList<String>();
		strategies.add(DEVICE_SHARING);
		strategies.add(VERBOSE_DEVICE_SHARING);
		strategies.add(CHECK_ONCE);
	}

	public static String getDefault() {
		return DEFAULT;
	}

	public static List<String> getTypesOfStrategies() {
		return strategies;
	}
}
