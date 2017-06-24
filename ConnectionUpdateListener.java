import java.net.*;

/**
 * Lets the GUI respond
 * to changes that were
 * occured in the connection
 * 
 * @author Russell Coleman
 * @version 1.0.0
 */
public interface ConnectionUpdateListener {
	/**
	 * Indicates that a connection has been
	 * estabilshed with a SocketAddress
	 *
	 * @param addr The address connected
	 */
	public void connected(SocketAddress addr);

	/**
	 * Indicates that the current connection
	 * was lost.
	 */
	public void disconnected();
}
