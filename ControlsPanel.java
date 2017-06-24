import javax.swing.*;
import java.net.*;
import java.awt.event.*;
import java.util.*;

/**
 * The upper panel of the main
 * frame which handles all of
 * the most important user
 * controls.
 *
 * @author Russell Coleman
 * @version 1.0.0
 */
public class ControlsPanel implements ConnectionUpdateListener, ActionListener {
	private JLabel connectionInfo;
	private JButton end;
	private JSlider speedSlider;
	private JToolBar toolbar;
	private boolean isConnected;
	private ConnectionPreferencesListener preferencesListener;
	private UserPreferences preferences;

	/**
	 * Responds to requests from the
	 * server about how the data should
	 * be handled.
	 *
	 * @author Russell Coleman
	 * @version 1.0.0
	 */
	protected class PreferenceHandler implements UserPreferences {
		/**
		 * Returns the time in ms that
		 * should be delayed between
		 * requests from the client.
		 *
		 * @return Delay time in ms.
		 */
		public long getDelayAmount() {
			return speedSlider.getValue();
		}
	}

	/**
	 * Responds to button presses
	 * and the like.
	 *
	 * @param event The event
	 */
	public void actionPerformed(ActionEvent ae) {
		if(ae.getActionCommand().equals("end")) {
			preferencesListener.endConnection();
		}
	}

	/**
	 * Returns the UserPreferences object
	 * that allows the server to see
	 * what the user has chosen on the
	 * toolbar.
	 *
	 * @return Preferences object
	 */
	public UserPreferences getUserPreferences() {
		return preferences;
	}

	/**
	 * Initializes the class
	 * with the proper listeners.
	 *
	 * @param preferencesListener The preferences update listener
	 * @param updateListener The connection update listener
	 */
	public ControlsPanel() {
		isConnected = false;
		preferences = new PreferenceHandler();
	}

	/**
	 * Sets the listener for when preferences
	 * aare changed
	 *
	 * @param preferencesListener the new listener
	 */
	public void setConnectionPreferencesListener(ConnectionPreferencesListener preferencesListener) {
		this.preferencesListener = preferencesListener;
	}

	//delays in ms
	private static final int SPEED_DELAY_MIN = 0;
	private static final int SPEED_DELAY_MAX = 300;
	private static final int SPEED_DELAY_DEFAULT = 30;

	/**
	 * Initializes the panel
	 * with a title.
	 */
	public void init(String title) {
		toolbar = new JToolBar(title);
		connectionInfo = new JLabel("Not connected.");
		end = new JButton("End");
		end.setActionCommand("end");
		end.addActionListener(this);
		speedSlider = new JSlider(SPEED_DELAY_MIN, SPEED_DELAY_MAX, SPEED_DELAY_DEFAULT);

		toolbar.add(connectionInfo);
		toolbar.addSeparator();
		toolbar.add(speedSlider);
		toolbar.add(end);

		Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
		labelTable.put(SPEED_DELAY_MIN, new JLabel(String.valueOf(SPEED_DELAY_MIN) + "ms"));
		labelTable.put(SPEED_DELAY_MAX, new JLabel(String.valueOf(SPEED_DELAY_MAX) + "ms"));
		labelTable.put((SPEED_DELAY_MAX + SPEED_DELAY_MIN)/2, new JLabel("Speed"));
		speedSlider.setLabelTable(labelTable);
		speedSlider.setPaintLabels(true);

		end.setEnabled(false);
	}

	/**
	 * Returns the JToolBar that
	 * can be displayed on screen
	 *
	 * @return The toolbar
	 */
	public JToolBar getComponent() {
		return toolbar;
	}

	/**
	 * Returns the listener this
	 * class wants to be called
	 * when something changes
	 * with the connection.
	 *
	 * @return This
	 */
	public ConnectionUpdateListener getConnectionUpdateListener() {
		return this;
	}

	/**
	 * Indicates that a connection
	 * has been established.
	 *
	 * @param addr The remote address
	 */
	@Override
	public void connected(SocketAddress address) {
		speedSlider.setEnabled(false);
		end.setEnabled(true);
		connectionInfo.setText("Connected - " + address.toString());
	}

	/**
	 * Indicates that a disconnect
	 * has occured
	 */
	@Override
	public void disconnected() {
		connectionInfo.setText("Ended.");
		speedSlider.setEnabled(true);
		end.setEnabled(false);
	}
}
