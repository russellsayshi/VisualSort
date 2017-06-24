/**
 * Acts as a bridge between what is
 * shown on screen and the underlying
 * array.
 *
 * @author Russell Coleman
 * @version 1.0.0
 */
public interface ArrayInterface {
	/**
	 * Sets the value at an index
	 * in the array to a new value.
	 *
	 * @param index The index
	 * @param newVal The new value.
	 */
	void set(int index, int newVal);

	/**
	 * Initializes the array with
	 * an arr.
	 *
	 * @param arr The array to use
	 */
	void init(int[] arr);

	/**
	 * Displays a visual hint
	 * that index is currently
	 * being processed.
	 *
	 * @param index The index being processed
	 */
	void point(int index);

	/**
	 * Lets the GUI know that the
	 * client is done processing
	 * the array.
	 */
	void done();

	/**
	 * Displays a visual hint that
	 * the selected region is
	 * being processed. Start
	 * is inclusive, end is exclusive.
	 *
	 * @param start The beginning of the region
	 * @param end The end of the region
	 */
	void markRegion(int start, int end);

	/**
	 * Clears the region that previously
	 * had a visual hint applied.
	 */
	void clearRegion();
}
