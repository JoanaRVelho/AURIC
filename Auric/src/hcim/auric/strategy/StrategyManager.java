package hcim.auric.strategy;

import java.util.ArrayList;
import java.util.List;

public class StrategyManager {

	public static final String DEVICE_SHARING = "Record only intruder's activities";
	public static final String GREEDY_STRATEGY = "Records everything";
	private static final String DEFAULT = GREEDY_STRATEGY;

	private static List<String> strategies;

	static {
		strategies = new ArrayList<String>();
		
		strategies.add(DEVICE_SHARING);
		strategies.add(GREEDY_STRATEGY);
	}

	public static String getDefault() {
		return DEFAULT;
	}

	public static List<String> getTypesOfStrategies() {
		return strategies;
	}
}
