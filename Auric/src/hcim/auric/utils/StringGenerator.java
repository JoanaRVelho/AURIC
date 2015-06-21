package hcim.auric.utils;

/**
 * 
 * @author Joana Velho
 * 
 *         String Generator class generates unique strings. This class is used
 *         to generate names for the pictures.
 */
public class StringGenerator {
	private static final String OWNER = "OWNER";

	/**
	 * Generate a unique string based on the current time in milliseconds
	 * 
	 * @return string with the current time in milliseconds
	 */
	public static String generateName() {
		return System.currentTimeMillis() + "";
	}

	/**
	 * Generate a unique string based on the current time in milliseconds
	 * 
	 * @return <owner prefix> + current time in milliseconds
	 */
	public static String generateOwnerName() {
		return OWNER + System.currentTimeMillis();
	}

	/**
	 * Generate a string based on the parameter
	 * 
	 * @param i
	 *            - number
	 * @return <owner prefix> + i
	 */
	public static String generateOwnerName(int i) {
		return OWNER + i;
	}

	/**
	 * @return owner prefix
	 */
	public static String getOwnerPrefix() {
		return OWNER;
	}
}
