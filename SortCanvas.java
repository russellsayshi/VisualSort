import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.net.*;

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
	private static final int TEXT_PADDING = 25;
	private int[] arr;
	private int[] sortedArr;
	private int arrMax;
	private boolean isSortingInProgress = true;
	private boolean sortedProperly = false;
	private long startTime, endTime;
	private int lastChangedIndex = -1;
	private int pointIndex = -1;
	private int regionStart = -1, regionEnd = -1;
	private static final Color BAR_OVERLAY_COLOR = new Color(0, 0, 255, 100);
	private static final Color SUCCESS_COLOR = new Color(15, 15, 15);
	private static final Color FAIL_COLOR = new Color(40, 0, 0);

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
		if(val > arrMax) arrMax = val;
		lastChangedIndex = index;
		arr[index] = val;
		update();
	}

	/**
	 * Points to a certain location
	 *
	 * @param index The place to point
	 */
	public void point(int index) {
		pointIndex = index;
		update();
	}

	/**
	 * Marks a region
	 * on screen.
	 *
	 * @param start Beginning of region, inclusive
	 * @param end End of region, exclusive
	 */
	public void markRegion(int start, int end) {
		regionStart = start;
		regionEnd = end;
		update();
	}


	/**
	 * Clears the selected region
	 */
	public void clearRegion() {
		regionStart = -1;
		regionEnd = -1;
		update();
	}

	/**
	 * Clears the temporary visual
	 * variables, like region and markings.
	 */
	public void clearVisualState() {
		clearRegion();
		pointIndex = -1;
		lastChangedIndex = -1;
	}

	/**
	 * Called once the array is done
	 * sorting.
	 */
	public void done() {
		endTime = System.currentTimeMillis();
		lastChangedIndex = -1;
		pointIndex = -1;
		isSortingInProgress = false;
		sortedProperly = true;
		clearRegion();
		for(int i = 0; i < arr.length; i++) {
			if(arr[i] != sortedArr[i]) {
				sortedProperly = false;
			}
		}
		update();
	}

	/**
	 * Initializes the SortCanvas with
	 * an array.
	 *
	 * @param arr The array to initialize with
	 */
	public void init(int[] arr) {
		clearVisualState();
		isSortingInProgress = true;
		startTime = System.currentTimeMillis();
		this.arr = arr;
		sortedArr = new int[arr.length];
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
			sortedArr[i] = arr[i];
		}
		Arrays.sort(sortedArr);
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
							waitObject.wait(timeUntilNextFrame);
						}
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
	 * Draws the bars on screen.
	 * If color is null, use gradient
	 * based on bar height.
	 *
	 * @param arr The array
	 * @param width Width
	 * @param height Height
	 * @param color Color
	 * @param barwid Bar width
	 * @param g Graphics
	 */
	private void drawBars(int[] arr, Graphics2D g, int width, int height, Color color, int barwid) {
		for(int i = 0; i < arr.length; i++) {
			double pos = (double)i/arr.length; //position in array as percent

			if(lastChangedIndex == i) {
				g.setColor(Color.GREEN);
			} else if(i >= regionStart && i < regionEnd) {
				g.setColor(Color.YELLOW);
			} else {
				if(color == null) g.setColor(new Color((arr[i] % 4) * 80 /*40*/, (int)(pos * 255), 200));
				else g.setColor(color);
			}
			int x = (int)(pos * width);
			int y = (int)((float)arr[i]/arrMax*height);
			g.fillRect(x+1, height-y, barwid, y);
			if(i == pointIndex) {
				int pointHeight = Math.min(width/20, y);
				g.setColor(Color.RED);
				g.fillRect(x+1, height-pointHeight, barwid, pointHeight);
			}
		}
	}


	/**
	 * Overrides JPanel's paintComponent()
	 * method in order to draw the array
	 *
	 * @param Graphcis the graphics object
	 */
	public void paintComponent(Graphics gOld) {
		int width = getWidth();
		int height = getHeight();
		Graphics2D g = (Graphics2D)gOld;

		if(arr == null) {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, width, height);
			g.setColor(Color.WHITE);
			g.drawString("Array will appear here.", TEXT_PADDING, TEXT_PADDING);
			return;
		}

		if(isSortingInProgress) {
			g.setColor(Color.BLACK);
		} else {
			if(sortedProperly) {
				g.setColor(SUCCESS_COLOR);
			} else {
				g.setColor(FAIL_COLOR);
			}
		}

		g.fillRect(0, 0, width, height);
		
		int barwid = width/arr.length;
		barwid -= 2;
		if(barwid <= 0) barwid = 1;
		drawBars(arr, g, width, height, null, barwid);
		lastChangedIndex = -1;

		if(!isSortingInProgress) {
			if(sortedProperly) {
				g.setColor(Color.GREEN);
				g.drawString("Success! " + (endTime - startTime) + "ms.", TEXT_PADDING, TEXT_PADDING);
			} else {
				g.setColor(Color.RED);
				g.drawString("Improperly sorted array. " + (endTime - startTime) + "ms.", TEXT_PADDING, TEXT_PADDING);

				drawBars(sortedArr, g, width, height, BAR_OVERLAY_COLOR, barwid);
			}
		}	
	}
}
