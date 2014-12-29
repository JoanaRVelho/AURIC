package hcim.auric.utils;

/**
 * 
 * @author Joana Velho
 * 
 *         String Generator class generates unique strings. This class is used
 *         to generate names for the pictures.
 */
public class StringGenerator {
	/**
	 * Generate a random string
	 * 
	 * @return
	 */
	public static String generate() {
		// return UUID.randomUUID().toString();
		return System.currentTimeMillis() + "";
	}
}
