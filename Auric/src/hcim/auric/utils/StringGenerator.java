package hcim.auric.utils;

import java.util.UUID;

public class StringGenerator {
	/**
	 * Generate a random string
	 * 
	 * @return
	 */
	public static String generateString() {
		return UUID.randomUUID().toString();
	}
}
