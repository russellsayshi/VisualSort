/**
 * This keeps the server updated when
 * the user makes a preferences
 * change.
 *
 * @author Russell Coleman
 * @version 1.0.0
 */
public interface ConnectionPreferencesListener {
	/**
	 * Ends the connection abruptly.
	 */
	default public void endConnection() {}

	/**
	 * Changes the speed of the animation.
	 *
	 * @param delay The delay in MS.
	 */
	default public void changeSpeed(int delay) {}
}
