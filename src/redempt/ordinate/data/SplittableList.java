package redempt.ordinate.data;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class SplittableList<T> implements Iterable<T> {
	
	private T[] array;
	private boolean[] removed;
	private int removedCount = 0;
	private int start;
	
	public SplittableList(T[] array, int start) {
		this.array = array;
		this.removed = new boolean[array.length];
		this.start = start;
	}
	
	public SplittableList(T[] array) {
		this(array, 0);
	}
	
	public void skip(int amount) {
		int counter = 0;
		for (int i = start; i < array.length; i++) {
			if (!removed[i] && counter++ == amount) {
				start = i;
				return;
			}
		}
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
		return array.length - start - removedCount;
	}
	
	public T get(int index) {
		return array[trueIndex(index)];
	}
	
	private int trueIndex(int index) {
		int counter = 0;
		for (int i = start; i < array.length; i++) {
			if (!removed[i] && counter++ == index) {
				return i;
			}
		}
		return array.length;
	}
	
	public void remove(int index) {
		removedCount++;
		index = trueIndex(index);
		removed[index] = true;
	}
	
	public SplittableList<T> split(int newStart) {
		SplittableList<T> list = new SplittableList<>(array, start + newStart);
		list.removed = Arrays.copyOf(removed, removed.length);
		return list;
	}
	
	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			
			int index = start;
			
			private void advanceToNext() {
				while (removed[index]) index++;
			}
			
			@Override
			public boolean hasNext() {
				advanceToNext();
				return index < array.length;
			}
			
			@Override
			public T next() {
				advanceToNext();
				return array[index++];
			}
			
		};
	}
	
	@Override
	public void forEach(Consumer<? super T> action) {
		for (int i = start; i < array.length; i++) {
			if (!removed[i]) {
				action.accept(array[i]);
			}
		}
	}
	
	@Override
	public Spliterator<T> spliterator() {
		return Iterable.super.spliterator();
	}
	
}
