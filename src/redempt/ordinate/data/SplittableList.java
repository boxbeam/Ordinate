package redempt.ordinate.data;

public class SplittableList<T> {

	private Object[] array;

	private int start;

	public SplittableList(T[] array, int start) {
		this.array = array;
		this.start = start;
	}

	public SplittableList(T[] array) {
		this(array, 0);
	}

	public void removeRange(int index, int toRemove) {
		int trueIndex = index + start;
		Object[] newArray = new Object[array.length - start - toRemove];
		System.arraycopy(array, start, newArray, 0, index);
		System.arraycopy(array, trueIndex + toRemove, newArray, index, array.length - trueIndex - toRemove);
		array = newArray;
		start = 0;
	}

	public void remove(int index) {
		removeRange(index, 1);
	}

	public void skip(int amount) {
		start += amount;
		start = Math.min(start, array.length);
	}

	public T peek() {
		if (hasNext()) {
			return (T) array[start];
		}
		return null;
	}

	public T poll() {
		Object val = peek();
		if (val != null) {
			start++;
		}
		return (T) val;
	}

	public boolean hasNext() {
		return start < array.length;
	}

	public int size() {
		return array.length - start;
	}

	public T get(int index) {
		return (T) array[start + index];
	}

	public SplittableList<T> split(int newStart) {
		return new SplittableList(array, start + newStart);
	}

}