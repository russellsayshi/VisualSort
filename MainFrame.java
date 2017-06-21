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
		panel.setLayout(new BorderLayout());
		frame.setContentPane(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIcon();

		//Add components to frame
		panel.add(new JPanel(), BorderLayout.NORTH);
		panel.add(new SortCanvas(), BorderLayout.CENTER);

		//Place frame on screen
		frame.pack();
		frame.setLocationRelativeTo(null);
	}

	/**
	 * Makes the frame visible
	 * to the user.
	 */
	public void show() {
		frame.setVisible(true);
	}
}
