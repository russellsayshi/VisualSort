import javax.swing.*;
import java.awt.*;

/**
 * Extension of JPanel
 * used as a canvas to
 * display visually the
 * array to be sorted.
 *
 * @author Russell Coleman
 * @version 1.0.0
 */
public class SortCanvas extends JPanel {
	/**
	 * Overrides JPanel's paintComponent()
	 * method in order to draw the array
	 *
	 * @param Graphcis the graphics object
	 */
	public void paintComponent(Graphics gOld) {
		Graphics2D g = (Graphics2D)gOld;
		g.setColor(Color.RED);
		g.fillRect(0, 0, 100, 200);
	}
}
