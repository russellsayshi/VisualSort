/**
 * Indicates that the class
 * can have connection listeners
 * added and removed.
 *
 * @author Russell Coleman
 * @version 1.0.0
 */
public interface UpdateableConnection {
	/**
	 * Adds a connectionupdatelistener
	 *
	 * @param listener To add
	 */
	public void addConnectionUpdateListener(ConnectionUpdateListener listener);

	/**
	 * Removes a connectionupdatelistener
	 *
	 * @param listener To remove
	 */
	public boolean removeConnectionUpdateListener(ConnectionUpdateListener listener);

	/**
	 * Returns the preferences update listener
	 *
	 * @return The preferences update listener
	 */
	public ConnectionPreferencesListener getConnectionPreferencesListener();
}
