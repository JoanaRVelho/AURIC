package hcim.auric.utils;


public class StringGenerator {
	/**
	 * Generate a random string
	 * 
	 * @return
	 */
	public static String generateString() {
		//return UUID.randomUUID().toString();
		return System.currentTimeMillis() + "";
	}
}
