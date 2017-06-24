/**
 * Interface that the server
 * can query to determine how
 * to respond to input, based
 * on what the user says.
 * 
 * @author Russell Coleman
 * @version 1.0.0
 */
public interface UserPreferences {
	/**
	 * Returns the delay in ms
	 * that the server should
	 * strive for between
	 * requests.
	 *
	 * @return Delay time in ms.
	 */
	long getDelayAmount();
}
