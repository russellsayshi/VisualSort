import javax.swing.*;
import java.util.*;
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
public class SortCanvas extends JPanel implements ArrayInterface {
	private Thread renderThread;
	private final Object waitObject = new Object();
	private static final int MAX_FPS = 60;
	private static final int MS_PER_FRAME = 1000/MAX_FPS;
	private int[] arr;
	private int arrMax;

	/**
	 * Constructs a canvas to visualize
	 * array sorting, using
	 * arr as the array.
	 */
	public SortCanvas() {
		//do nothing
	}

	/**
	 * Sets an element in the array to have
	 * a new value.
	 *
	 * @param index The index to set
	 * @param val The new value
	 */
	public void set(int index, int val) {
		arr[index] = val;
		update();
	}

	/**
	 * Initializes the SortCanvas with
	 * an array.
	 *
	 * @param arr The array to initialize with
	 */
	public void init(int[] arr) {
		this.arr = arr;
		int arrMax = 0;
		for(int i = 0; i < arr.length; i++) {
			if(arr[i] < 0) {
				throw new IllegalArgumentException(
						"Array passed to update() must contain all positive values."
				);
			}
			if(arr[i] > arrMax) {
				arrMax = arr[i];
			}
		}
		this.arrMax = arrMax;
		update();
	}

	/**
	 * Pushes any changes to the GUI.
	 */
	protected void update() {
		synchronized(waitObject) {
			waitObject.notify();
		}
	}

	/**
	 * Starts the thread that handles
	 * display updates at a maximum
	 * of MAX_FPS frames per second.
	 */
	public void beginRenderThread() {
		if(renderThread != null) {
			renderThread.interrupt();
		}

		renderThread = new Thread(() -> {
			System.out.println("Beginning render thread.");
			synchronized(waitObject) {
				try {
					long lastFrameUpdate = System.currentTimeMillis();
					while(true) {
						waitObject.wait();
						while(true) {
							long newTime = System.currentTimeMillis();
							long timeUntilNextFrame = newTime - lastFrameUpdate;
							if(timeUntilNextFrame >= MS_PER_FRAME) {
								break;
							}
							System.out.println("Waiting for " + timeUntilNextFrame + " ms.");
							waitObject.wait(timeUntilNextFrame);
						}
						System.out.println("Frame update!");
						SortCanvas.this.repaint();
						lastFrameUpdate = System.currentTimeMillis();
					}
				} catch(InterruptedException ie) {
					//do nothing, wait for other
					//thread to take over.
					System.out.println("Thread interrupted.");
				}
			}
		});
		renderThread.start();
	}

	/**
	 * Ends the active render thread.
	 * The redrawing will no longer occur
	 * at MAX_FPS.
	 */
	public void endRenderThread() {
		if(renderThread != null) {
			renderThread.interrupt();
			renderThread = null;
		}
	}

	/**
	 * Overrides JPanel's paintComponent()
	 * method in order to draw the array
	 *
	 * @param Graphcis the graphics object
	 */
	public void paintComponent(Graphics gOld) {
		Graphics2D g = (Graphics2D)gOld;
		g.setColor(Color.RED);

		if(arr == null) {
			return;
		}

		
		int width = getWidth();
		int height = getHeight();
		int barwid = width/arr.length;
		for(int i = 0; i < arr.length; i++) {
			double pos = (double)i/arr.length;
			int x = (int)(pos * width);
			int y = (int)((float)arr[i]/arrMax*height);
			g.fillRect(x, height-y, barwid, y);
		}
	}
}
