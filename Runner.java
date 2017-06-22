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
		final MainFrame frame = new MainFrame();
		frame.show();
		final ArrayServer server = new ArrayServer(frame.getArrayInterface(), () -> {
			//on connection
			frame.switchToConnectedGUI();
		});
		server.run(); //do its thing
	}
}
