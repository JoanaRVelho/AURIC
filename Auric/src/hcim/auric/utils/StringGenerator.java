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
	 * Generate a unique string based on the current time in milliseconds
	 * 
	 * @return string with the current time in milliseconds
	 */
	public static String generateName() {
		// return UUID.randomUUID().toString();
		return System.currentTimeMillis() + "";
	}
	
	/**
	 * Generate a unique string based on the current time in milliseconds
	 * 
	 * @return "OWNER" + current time in milliseconds
	 */
	public static String generateOwnerName(){
		return "OWNER" + System.currentTimeMillis();
	}
}
