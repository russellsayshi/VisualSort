import javax.swing.*;

/**
 * Works to test the
 * program from command
 * line.
 *
 * @author Russell Coleman
 * @version 1.0.0
 */
public class Runner {
	/**
	 * Creates a new MainFrame class
	 * and shows its gui.
	 *
	 * @param args Command-line arguments
	 */
	public static void main(String[] args) {

		//set system look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(UnsupportedLookAndFeelException|
				ClassNotFoundException|
				InstantiationException|
				IllegalAccessException e) {
			e.printStackTrace();
		}

		final MainFrame frame = new MainFrame();
		UserPreferences guiPrefs = frame.getUserPreferences();
		final ArrayServer server = new ArrayServer(frame.getArrayInterface(), guiPrefs);
		frame.setConnection(server);
		frame.show();
		
		server.run(); //do its thing
	}
}
