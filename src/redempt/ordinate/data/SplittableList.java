package redempt.ordinate.data;

public class SplittableList<T> {
	
	private T[] array;
	private int start;
	
	public SplittableList(T[] array, int start) {
		this.array = array;
		this.start = start;
	}
	
	public SplittableList(T[] array) {
		this(array, 0);
	}
	
	public void skip(int amount) {
		start += amount;
		start = Math.min(start, array.length - 1);
	}
	
	public T peek() {
		if (hasNext()) {
			return array[start];
		}
		return null;
	}
	
	public T poll() {
		T val = peek();
		if (val != null) {
			start++;
		}
		return val;
	}
	
	public boolean hasNext() {
		return start < array.length;
	}
	
	public int size() {
		return array.length - start;
	}
	
	public T get(int index) {
		return array[start + index];
	}
	
	public SplittableList<T> split(int newStart) {
		return new SplittableList<>(array, start + newStart);
	}
	
}
