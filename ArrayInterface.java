/**
 * Acts as a bridge between what is
 * shown on screen and the underlying
 * array.
 *
 * @author Russell Coleman
 * @version 1.0.0
 */
public interface ArrayInterface {
	void set(int index, int newVal);
	void init(int[] arr);
}
