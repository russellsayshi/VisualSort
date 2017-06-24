import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.io.*;
import javax.imageio.*;

/**
 * Main JFrame container for all
 * components, including the top
 * bar and canvas area.
 *
 * @author Russell Coleman
 * @version 1.0.0
 */
public class MainFrame {
	private JFrame frame;
	private JPanel mainPanel;
	private SortCanvas sortCanvas;
	private ControlsPanel controlsPanel;
	private UpdateableConnection connection;

	//delays in ms between requests
	private static final int SPEED_MIN = 300;
	private static final int SPEED_MAX = 0;
	private static final int SPEED_DEFAULT = 30;


	/**
	 * Sets the connection that gives the
	 * frame new information about
	 * the connection. Don't use this
	 * unless the previous UpdateableConnection
	 * is not going to fire any new events,
	 * because this does not unregister
	 * preexisting events.
	 *
	 * @param connection The new connection
	 */
	public void setConnection(UpdateableConnection connection) {
		connection.addConnectionUpdateListener(controlsPanel
				.getConnectionUpdateListener());
		controlsPanel.setConnectionPreferencesListener(connection.getConnectionPreferencesListener());
		this.connection = connection;
	}

	/**
	 * Returns the user preferences, as
	 * specified by the user in the GUI.
	 *
	 * @return The user's prefs.
	 */
	public UserPreferences getUserPreferences() {
		return controlsPanel.getUserPreferences();
	}

	/**
	 * The default constructor - builds
	 * a hidden empty frame
	 */
	public MainFrame() {
		initGUI();
	}

	/**
	 * Tries to set icon, ignore if
	 * failed. Not really important.
	 */
	private void setIcon() {
		try {
			frame.setIconImage(ImageIO.read(getClass().getResource("icon.png")));
		} catch(IOException ioe) {
			//log, but do nothing
			System.err.println("Could not read icon image.");
		}
	}

	/**
	 * Fills up the GUI with components
	 * and places the window onscreen.
	 * Do not call more than once!
	 */
	private void initGUI() {
		//Initialize frame & main panel
		frame = new JFrame("Array Sort Visualizer - russellsayshi");
		JPanel panel = new JPanel();
		this.mainPanel = panel;
		panel.setLayout(new BorderLayout());
		frame.setContentPane(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIcon();

		//Add components to frame
		controlsPanel = new ControlsPanel();
		controlsPanel.init("Controls");
		panel.add(controlsPanel.getComponent(), BorderLayout.NORTH);
		panel.add((sortCanvas = new SortCanvas()), BorderLayout.CENTER);
		sortCanvas.beginRenderThread();

		//Place frame on screen
		frame.setSize(1000, 600);
		frame.setLocationRelativeTo(null);
	}

	/**
	 * Returns the interface that the connection
	 * can use to sort the array.
	 *
	 * @return The array interface
	 */
	public ArrayInterface getArrayInterface() {
		return sortCanvas;
	}

	/**
	 * Makes the frame visible
	 * to the user.
	 */
	public void show() {
		frame.setVisible(true);
	}
}
